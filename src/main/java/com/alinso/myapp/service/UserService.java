package com.alinso.myapp.service;

import com.alinso.myapp.dto.ChangePasswordDto;
import com.alinso.myapp.dto.PhotoUploadDto;
import com.alinso.myapp.dto.ResetPasswordDto;
import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.ForgottenPasswordToken;
import com.alinso.myapp.entity.MailVerificationToken;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.exception.UserWarningException;
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

        User user = token.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        ForgottenPasswordToken token = forgottenPasswordTokenService.findByToken(resetPasswordDto.getToken());

        User user = token.getUser();
        user.setPassword(bCryptPasswordEncoder.encode(resetPasswordDto.getPassword()));
        forgottenPasswordTokenService.delete(token);
        userRepository.save(user);
    }


    public UserDto update(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        User userInDb;
        try {
            userInDb = userRepository.findById(user.getId()).get();
        } catch (Exception e) {
            throw new UserWarningException("user not found id : " + userDto.getId());
        }

        user.setPassword(userInDb.getPassword());
        user.setProfilePicName(userInDb.getProfilePicName());
        user.setReferenceCode(userInDb.getReferenceCode());
        user.setEnabled(userInDb.getEnabled());

        if (userDto.getbDateString()!=null && !userDto.getbDateString().equals("")) {
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(userDto.getbDateString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            user.setBirthDate(birthDate);
        }
        userRepository.save(user);
        return userDto;
    }


    public UserDto findById(Long id) {
        try {

            User user = userRepository.findById(id).get();
            UserDto userDto = modelMapper.map(user, UserDto.class);

            if (user.getBirthDate() != null) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String birthDateString = format.format(user.getBirthDate());
                userDto.setbDateString(birthDateString);
            }
            return userDto;
        } catch (Exception e) {
            throw new UserWarningException("user not found id : " + id);
        }
    }

    public UserDto findByPhone(String phone) {
        try {
            User user = userRepository.findByPhone(phone).get();
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return userDto;
        } catch (Exception e) {
            return null;
        }
    }

    public UserDto findByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email).get();
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return userDto;
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

    public String updateProfilePic(PhotoUploadDto photoUploadDto) {

        String extension = FilenameUtils.getExtension(photoUploadDto.getFile().getOriginalFilename());
        String newName = fileStorageService.makeFileName() + "." + extension;

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //save new util and remove old one
        fileStorageService.deleteFile(profilPicUploadPath + user.getProfilePicName());
        fileStorageService.storeFile(photoUploadDto.getFile(), profilPicUploadPath, newName);

        //update database
        user.setProfilePicName(newName);
        userRepository.save(user);
        return newName;
    }


    public Boolean changePassword(ChangePasswordDto changePasswordDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword()));

        userRepository.save(user);

        return true;
    }

    public List<UserDto> searchUser(String searchText) {

        List<User> users = userRepository.searchUser("%" + searchText + "%");
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = modelMapper.map(user, UserDto.class);
            userDto.setAge(UserUtil.calculateAge(user));
            userDtos.add(userDto);
        }
        return userDtos;
    }
}
