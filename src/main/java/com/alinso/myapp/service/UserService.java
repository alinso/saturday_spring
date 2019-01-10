package com.alinso.myapp.service;

import com.alinso.myapp.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.dto.security.ChangePasswordDto;
import com.alinso.myapp.dto.security.ResetPasswordDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.ForgottenPasswordToken;
import com.alinso.myapp.entity.MailVerificationToken;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.RecordNotFound404Exception;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.service.security.ForgottenPasswordTokenService;
import com.alinso.myapp.service.security.MailVerificationTokenService;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    FileStorageUtil fileStorageService;

    @Autowired
    ReferenceService referenceService;

    @Autowired
    MailVerificationTokenService mailVerificationTokenService;

    @Autowired
    ForgottenPasswordTokenService forgottenPasswordTokenService;

    @Autowired
    CityService cityService;

    @Autowired
    BlockService blockService;

    @Value("${upload.profile.path}")
    private String profilPicUploadPath;

    public User register(User newUser) {

        newUser.setConfirmPassword("");
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        User user = userRepository.save(newUser);
        String token = mailVerificationTokenService.saveToken(user);
        mailVerificationTokenService.sendMail(token, user.getEmail());
        referenceService.useReferenceCode(newUser);
        referenceService.createNewReferenceCodes(user);
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
        forgottenPasswordTokenService.sendMail(token, user.getEmail());
    }

    public void verifyMail(String tokenString) {
        MailVerificationToken token = mailVerificationTokenService.findByToken(tokenString);

        if (token == null) {
            throw new RecordNotFound404Exception("Geçersiz link");
        }

        User user = userRepository.findById(token.getUser().getId()).get();
        user.setEnabled(true);
        userRepository.save(user);
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


    public ProfileInfoForUpdateDto update(ProfileInfoForUpdateDto profileInfoForUpdateDto) {
        User loggedUser  =(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        loggedUser.setName(profileInfoForUpdateDto.getName());
        loggedUser.setCity(cityService.findById(profileInfoForUpdateDto.getCityId()));
        loggedUser.setSurname(profileInfoForUpdateDto.getSurname());
        loggedUser.setEmail(profileInfoForUpdateDto.getEmail());
        loggedUser.setPhone(profileInfoForUpdateDto.getPhone());
        loggedUser.setBirthDate(DateUtil.stringToDate(profileInfoForUpdateDto.getbDateString(),"dd/MM/yyyy"));
        loggedUser.setMotivation(profileInfoForUpdateDto.getMotivation());
        loggedUser.setAbout(profileInfoForUpdateDto.getAbout());
        loggedUser.setGender(profileInfoForUpdateDto.getGender());
        loggedUser.setReferenceCode(profileInfoForUpdateDto.getReferenceCode());

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
            ProfileDto profileDto = modelMapper.map(user, ProfileDto.class);

            if (user.getBirthDate() != null) {
                profileDto.setAge(UserUtil.calculateAge(user));
            }
            return profileDto;
    }


    public ProfileInfoForUpdateDto getMyProfileInfoForUpdate() {
        try {

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ProfileInfoForUpdateDto profileInfoForUpdateDto = modelMapper.map(user, ProfileInfoForUpdateDto.class);
            profileInfoForUpdateDto.setbDateString(DateUtil.dateToString(user.getBirthDate(),"dd/MM/yyyy"));

            return profileInfoForUpdateDto;
        } catch (Exception e) {
            throw new UserWarningException("Hata oluştu" );
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
        } catch (Exception e) {
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
        String newName = fileStorageService.makeFileName() + "." + extension;

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //save new file and remove old one
        if(!loggedUser.getProfilePicName().equals(""))
        fileStorageService.deleteFile(profilPicUploadPath + loggedUser.getProfilePicName());
        fileStorageService.storeFile(singlePhotoUploadDto.getFile(), profilPicUploadPath, newName);

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
            ProfileDto profileDto = modelMapper.map(user, ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(user));
            profileDtos.add(profileDto);
        }
        return profileDtos;
    }
}
