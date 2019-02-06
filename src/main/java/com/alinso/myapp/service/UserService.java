package com.alinso.myapp.service;

import com.alinso.myapp.entity.ForgottenPasswordToken;
import com.alinso.myapp.entity.MailVerificationToken;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.entity.dto.security.ChangePasswordDto;
import com.alinso.myapp.entity.dto.security.ResetPasswordDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.exception.RecordNotFound404Exception;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.mail.service.MailService;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.service.security.ForgottenPasswordTokenService;
import com.alinso.myapp.service.security.MailVerificationTokenService;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    HashtagService hashtagService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    ReferenceService referenceService;

    @Autowired
    MailVerificationTokenService mailVerificationTokenService;

    @Autowired
    ForgottenPasswordTokenService forgottenPasswordTokenService;

    @Autowired
    CityService cityService;

    @Autowired
    UserEventService userEventService;

    @Autowired
    MailService mailService;

    @Autowired
    BlockService blockService;

    @Autowired
    PremiumService premiumService;

    public User register(User newUser) {

        newUser.setConfirmPassword("");
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        newUser.setPhotoCount(0);
        newUser.setReviewCount(0);
        newUser.setPoint(0);
        newUser.setActivityCount(0);

        User user = userRepository.save(newUser);
        String token = mailVerificationTokenService.saveToken(user);
        mailService.sendMailVerificationMail(user, token);
        return user;
    }

    public void forgottePasswordSendMail(String email) {
        User user;
        try {
            user = userRepository.findByEmail(email).get();
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Bu E-Posta ile kayıtlı kullanıcı bulunamadı");
        }
        String token = forgottenPasswordTokenService.saveToken(user);
        mailService.sendForgottenPasswordMail(user, token);
    }

    public void verifyMail(String tokenString) {
        MailVerificationToken token = mailVerificationTokenService.findByActiveToken(tokenString);

        if (token == null) {
            throw new RecordNotFound404Exception("Geçersiz link");
        }
        User user = userRepository.findById(token.getUser().getId()).get();
        user.setEnabled(true);

        userEventService.setReferenceChain(user);
        userRepository.save(user);
        userEventService.newUserRegistered(user);

        //if we dont delete token every time user clicks the link, same process above duplicates
        mailVerificationTokenService.delete(token);

    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        if (resetPasswordDto.getToken() == null) {
            throw new RecordNotFound404Exception("Geçersiz link");
        }

        ForgottenPasswordToken token = forgottenPasswordTokenService.findByToken(resetPasswordDto.getToken());

        User user = userRepository.findById(token.getUser().getId()).get();
        user.setPassword(bCryptPasswordEncoder.encode(resetPasswordDto.getPassword()));
        forgottenPasswordTokenService.delete(token);
        userRepository.save(user);
    }

    public User findEntityById(Long id) {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Kullanıcı Bulunamadı");
        }
    }

    public ProfileInfoForUpdateDto update(ProfileInfoForUpdateDto profileInfoForUpdateDto) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        loggedUser.setName(profileInfoForUpdateDto.getName());
        loggedUser.setCity(cityService.findById(profileInfoForUpdateDto.getCityId()));
        loggedUser.setSurname(profileInfoForUpdateDto.getSurname());
        loggedUser.setEmail(profileInfoForUpdateDto.getEmail());
        loggedUser.setPhone(profileInfoForUpdateDto.getPhone());
        loggedUser.setBirthDate(DateUtil.stringToDate(profileInfoForUpdateDto.getbDateString(), "dd/MM/yyyy"));
        loggedUser.setMotivation(profileInfoForUpdateDto.getMotivation());
        loggedUser.setAbout(profileInfoForUpdateDto.getAbout());
        loggedUser.setGender(profileInfoForUpdateDto.getGender());
        //loggedUser.setInterests(profileInfoForUpdateDto.getInterests());
        //loggedUser.setReferenceCode(profileInfoForUpdateDto.getReferenceCode());
        hashtagService.saveUserHashtag(loggedUser, profileInfoForUpdateDto.getInterests());

        userRepository.save(loggedUser);
        return profileInfoForUpdateDto;
    }

    public ProfileDto getProfileById(Long id) {
        User user;

        try {
            user = userRepository.findById(id).get();
        } catch (Exception e) {
            throw new RecordNotFound404Exception("Kullanıcı Bulunamadı: " + id);
        }
        return toProfileDto(user);
    }

    public ProfileInfoForUpdateDto getMyProfileInfoForUpdate() {
        try {

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ProfileInfoForUpdateDto profileInfoForUpdateDto = modelMapper.map(user, ProfileInfoForUpdateDto.class);
            profileInfoForUpdateDto.setbDateString(DateUtil.dateToString(user.getBirthDate(), "dd/MM/yyyy"));
            profileInfoForUpdateDto.setInterests(hashtagService.findByUserStr(user));
            return profileInfoForUpdateDto;
        } catch (Exception e) {
            throw new UserWarningException("Hata oluştu");
        }
    }

    public ProfileInfoForUpdateDto findByPhone(String phone) {
        try {
            User user = userRepository.findByPhone(phone).get();
            ProfileInfoForUpdateDto profileInfoForUpdateDto = modelMapper.map(user, ProfileInfoForUpdateDto.class);
            return profileInfoForUpdateDto;
        } catch (Exception e) {
            return null;
        }
    }

    public ProfileInfoForUpdateDto findByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email).get();
            ProfileInfoForUpdateDto profileInfoForUpdateDto = modelMapper.map(user, ProfileInfoForUpdateDto.class);
            return profileInfoForUpdateDto;
        } catch (NoSuchElementException e) {
            return null;
        }
    }


    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserWarningException("user not found id : " + id);
        }
    }

    public String updateProfilePic(SinglePhotoUploadDto singlePhotoUploadDto) {

        String extension = FilenameUtils.getExtension(singlePhotoUploadDto.getFile().getOriginalFilename());
        String newName = fileStorageUtil.makeFileName() + "." + extension;

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //save new file and remove old one
        if (!loggedUser.getProfilePicName().equals(""))
            fileStorageUtil.deleteFile(loggedUser.getProfilePicName());
        fileStorageUtil.storeFile(singlePhotoUploadDto.getFile(), newName, true);

        //update database
        loggedUser.setProfilePicName(newName);
        userRepository.save(loggedUser);
        return newName;
    }

    public Boolean changePassword(ChangePasswordDto changePasswordDto) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loggedUser.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(loggedUser);

        return true;
    }

    public List<ProfileDto> searchUser(String searchText) {

        List<User> users = userRepository.searchUser("%" + searchText + "%");
        List<ProfileDto> profileDtos = new ArrayList<>();
        for (User user : users) {
            profileDtos.add(toProfileDto(user));
        }
        return profileDtos;
    }

    public ProfileDto toProfileDto(User user) {
        ProfileDto profileDto = modelMapper.map(user, ProfileDto.class);
        profileDto.setAge(UserUtil.calculateAge(user));
        profileDto.setInterests(hashtagService.findByUserStr(user));
        profileDto.setUserPremium(premiumService.isUserPremium(user));
        return profileDto;
    }

    public List<ProfileDto> toProfileDtoList(List<User> users) {
        List<ProfileDto> dtos = new ArrayList<>();
        for (User u : users) {
            dtos.add(toProfileDto(u));
        }
        return dtos;
    }

    @Scheduled(fixedRate = 60*60 * 1000, initialDelay = 60 * 1000)
    public void deleteTestUser() {
        try {
            User test = userRepository.findByEmail("ankaratangoclub@gmail.com").get();
            StoredProcedureQuery delete_user_sp = entityManager.createNamedStoredProcedureQuery("delete_user_sp");
            delete_user_sp.setParameter("userId", test.getId());
            delete_user_sp.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            User test2 = userRepository.findByEmail("cincihocam@hotmail.com").get();
            StoredProcedureQuery delete_user_sp = entityManager.createNamedStoredProcedureQuery("delete_user_sp");
            delete_user_sp.setParameter("userId", test2.getId());
            delete_user_sp.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
