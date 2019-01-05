package com.alinso.myapp.service;

import com.alinso.myapp.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.dto.security.ChangePasswordDto;
import com.alinso.myapp.dto.security.ResetPasswordDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.ForgottenPasswordToken;
import com.alinso.myapp.entity.MailVerificationToken;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.service.security.ForgottenPasswordTokenService;
import com.alinso.myapp.service.security.MailVerificationTokenService;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    MailVerificationTokenService mailVerificationTokenService;

    @Autowired
    ForgottenPasswordTokenService forgottenPasswordTokenService;


    @Value("${upload.profile.path}")
    private String profilPicUploadPath;

    public User register(User newUser) {

        newUser.setEmail(newUser.getEmail());
        newUser.setConfirmPassword("");
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        User user = userRepository.save(newUser);
        String token = mailVerificationTokenService.saveToken(user);
        mailVerificationTokenService.sendMail(token, user.getEmail());
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
            throw new UserWarningException("Geçersiz link");
        }

        User user = userRepository.findById(token.getUser().getId()).get();
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        ForgottenPasswordToken token = forgottenPasswordTokenService.findByToken(resetPasswordDto.getToken());

        User user = userRepository.findById(token.getUser().getId()).get();
        user.setPassword(bCryptPasswordEncoder.encode(resetPasswordDto.getPassword()));
        forgottenPasswordTokenService.delete(token);
        userRepository.save(user);
    }


    public ProfileInfoForUpdateDto update(ProfileInfoForUpdateDto profileInfoForUpdateDto) {
        User user = modelMapper.map(profileInfoForUpdateDto, User.class);
        User userInDb;
        try {
            userInDb = userRepository.findById(user.getId()).get();
        } catch (Exception e) {
            throw new UserWarningException("user not found id : " + profileInfoForUpdateDto.getId());
        }

        user.setPassword(userInDb.getPassword());
        user.setProfilePicName(userInDb.getProfilePicName());
        user.setReferenceCode(userInDb.getReferenceCode());
        user.setEnabled(userInDb.getEnabled());

        if (profileInfoForUpdateDto.getbDateString() != null && !profileInfoForUpdateDto.getbDateString().equals("")) {
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(profileInfoForUpdateDto.getbDateString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            user.setBirthDate(birthDate);
        }
        userRepository.save(user);
        return profileInfoForUpdateDto;
    }

    public ProfileDto getProfileById(Long id) {
        User user;
        try {
             user = userRepository.findById(id).get();
        } catch (Exception e) {
            throw new UserWarningException("user not found id : " + id);
        }
            ProfileDto profileDto = modelMapper.map(user, ProfileDto.class);

            if (user.getBirthDate() != null) {
                profileDto.setAge(UserUtil.calculateAge(user));
            }
            return profileDto;

    }


    public ProfileInfoForUpdateDto getMyProfileInfoForUpdate(Long id) {
        try {

            User user = userRepository.findById(id).get();
            ProfileInfoForUpdateDto profileInfoForUpdateDto = modelMapper.map(user, ProfileInfoForUpdateDto.class);

            if (user.getBirthDate() != null) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String birthDateString = format.format(user.getBirthDate());
                profileInfoForUpdateDto.setbDateString(birthDateString);
            }
            return profileInfoForUpdateDto;
        } catch (Exception e) {
            throw new UserWarningException("user not found id : " + id);
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
        User userInDb = userRepository.findById(loggedUser.getId()).get();

        //save new file and remove old one
        fileStorageService.deleteFile(profilPicUploadPath + userInDb.getProfilePicName());
        fileStorageService.storeFile(singlePhotoUploadDto.getFile(), profilPicUploadPath, newName);

        //update database
        userInDb.setProfilePicName(newName);
        userRepository.save(userInDb);
        return newName;
    }


    public Boolean changePassword(ChangePasswordDto changePasswordDto) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User userInDb = userRepository.findById(loggedUser.getId()).get();
        userInDb.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword()));

        userRepository.save(userInDb);

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
