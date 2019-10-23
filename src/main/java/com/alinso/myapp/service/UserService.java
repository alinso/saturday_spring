package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.activity.ActivityDto;
import com.alinso.myapp.entity.dto.activity.ActivityRequestDto;
import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.entity.dto.review.ReviewDto;
import com.alinso.myapp.entity.dto.security.ChangePasswordDto;
import com.alinso.myapp.entity.dto.security.ResetPasswordDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.PremiumDuration;
import com.alinso.myapp.entity.enums.VibeType;
import com.alinso.myapp.exception.RecordNotFound404Exception;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.mail.service.MailService;
import com.alinso.myapp.repository.*;
import com.alinso.myapp.service.security.ForgottenPasswordTokenService;
import com.alinso.myapp.service.security.MailVerificationTokenService;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.SendSms;
import com.alinso.myapp.util.UserUtil;
import com.sun.jmx.snmp.SnmpMsg;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.util.*;

@Service
public class UserService {

    @Autowired
    HashtagService hashtagService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    ActivityRequestService activityRequestService;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ActivityRequesRepository activityRequesRepository;

    @Autowired
    PhotoService photoService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityService activityService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    FollowRepository followRepository;

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

    @Autowired
    VibeRepository vibeRepository;


    @Autowired
    ComplainRepository complainRepository;

    //this the registration without mail verification
    public User register(User newUser) {

        //City ankara = cityService.findById(Long.valueOf(1));
        String referenceCode = referenceService.makeReferenceCode();


        //reference code for men
        Integer starterPoint = 0;
        User parent = userRepository.findByReferenceCode(newUser.getReferenceCode());


        if (newUser.getPhone().length() == 10)
            newUser.setPhone("0" + newUser.getPhone());

        newUser.setConfirmPassword("");
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        newUser.setPhotoCount(0);
        newUser.setReviewCount(0);
        newUser.setPoint(0);
        newUser.setTooNegative(0);
        newUser.setExtraPoint(starterPoint);
        // newUser.setCity(ankara);
        newUser.setActivityCount(0);
        newUser.setReferenceCode(referenceCode);
        newUser.setEnabled(false);
        newUser.setParent(parent);


        Random rnd = new Random();
        Integer code = rnd.nextInt(999999);
        newUser.setSmsCode(code);
        User user = userRepository.save(newUser);


        if (parent != null) {
            parent.setReferenceCode(referenceService.makeReferenceCode());
            userRepository.save(parent);
        }


        SendSms.send("Activity Friend kaydı tamamlamak için sms onay kodu : " + code.toString(), newUser.getPhone());
        //String token = mailVerificationTokenService.saveToken(user);
        //mailService.sendMailVerificationMail(user, token);
        // userEventService.setReferenceChain(user);

        return user;
    }


    public User completeRegistration(Integer code) {

        User user = userRepository.findBySmsCode(code);
        if (user == null || user.getName().equals("silinen"))
            throw new UserWarningException("Bu kullanıcı bulunamadı");


        user.setEnabled(true);
        userRepository.save(user);
        userEventService.newUserRegistered(user);


        Premium premium = new Premium();
        premium.setStartDate(new Date());
        premium.setDuration(PremiumDuration.SONE_MONTH);
        premiumService.saveGift(premium, user);


        return user;
    }


    public Integer getUserCount() {
        return userRepository.getUserCount();
    }


    //this the registration with mail verification
//    public User register(User newUser) {
//
//        newUser.setConfirmPassword("");
//        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
//        newUser.setPhotoCount(0);
//        newUser.setReviewCount(0);
//        newUser.setPoint(0);
//        newUser.setActivityCount(0);
//
//        User user = userRepository.save(newUser);
//        String token = mailVerificationTokenService.saveToken(user);
//        mailService.sendMailVerificationMail(user, token);
//        return user;
//    }

    public void forgottePasswordSendPass(String phone) {
        User user;
        try {
            user = userRepository.findByPhone(phone);
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Bu numara ile kayıtlı kullanıcı bulunamadı");
        }

        if (user.getName().equals("silinen"))
            throw new UserWarningException("Bu numara ile kayıtlı kullanıcı bulunamadı");


        Random rnd = new Random();
        Integer pureRand = rnd.nextInt(999999);
        Integer pass = pureRand + 100000;
        user.setPassword(bCryptPasswordEncoder.encode(pass.toString()));
        userRepository.save(user);

        SendSms.send("Activity Friend yeni şifreniz : " + pass.toString(), phone);
    }

//    public void verifyMail(String tokenString) {
//        MailVerificationToken token = mailVerificationTokenService.findByActiveToken(tokenString);
//
//        if (token == null) {
//            throw new RecordNotFound404Exception("Geçersiz link");
//        }
//        User user = userRepository.findById(token.getUser().getId()).get();
//        user.setEnabled(true);
//
//        //userEventService.setReferenceChain(user);
//        userRepository.save(user);
//        userEventService.newUserRegistered(user);
//
//        //if we dont delete token every time user clicks the link, same process above duplicates
//        mailVerificationTokenService.delete(token);
//
//    }

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
        //loggedUser.setPhone(profileInfoForUpdateDto.getPhone());
        loggedUser.setBirthDate(DateUtil.stringToDate(profileInfoForUpdateDto.getbDateString(), "dd/MM/yyyy"));
        loggedUser.setMotivation(profileInfoForUpdateDto.getMotivation());
        loggedUser.setAbout(profileInfoForUpdateDto.getAbout());
        //loggedUser.setGender(profileInfoForUpdateDto.getGender());
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
//        user.setPoint(calculateUserPoint(user));
        //      userRepository.save(user);
        return toProfileDto(user);
    }

    public ProfileInfoForUpdateDto getMyProfileInfoForUpdate() {
        try {

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ProfileInfoForUpdateDto profileInfoForUpdateDto = modelMapper.map(user, ProfileInfoForUpdateDto.class);
            profileInfoForUpdateDto.setbDateString(DateUtil.dateToString(user.getBirthDate(), "dd/MM/yyyy"));
            profileInfoForUpdateDto.setInterests(hashtagService.findByUserStr(user));

            profileInfoForUpdateDto.setReferenceCode("");


            return profileInfoForUpdateDto;
        } catch (Exception e) {
            throw new UserWarningException("Hata oluştu");
        }
    }

    public ProfileInfoForUpdateDto findByPhone(String phone) {
        try {
            User user = userRepository.findByPhone(phone);
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

            User user = userRepository.getOne(id);
            List<Activity> res = activityRepository.findByCreatorOrderByDeadLineDesc(user);

            for (Activity a : res) {
                List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(a.getId());
                for (ActivityRequest r : activityRequests) {
                    activityRequesRepository.deleteById(r.getId());
                }
            }
            //Delete profile photo
            String profilePhoto = user.getProfilePicName();
            fileStorageUtil.deleteFile(profilePhoto);

            //Delete album photos
            List<Photo> photos = photoService.findByUserId(id);
            for (Photo p : photos) {
                photoService.deletePhoto(p.getFileName());
            }

            //Delete Activity Photos
            List<Activity> meetingsCreatedByUser = activityRepository.findByCreatorOrderByDeadLineDesc(user);
            for (Activity a : meetingsCreatedByUser) {
                if (a.getPhotoName() != null)
                    fileStorageUtil.deleteFile(a.getPhotoName());
            }


            //move children
            User batman = userRepository.getOne(Long.valueOf(3211));
            List<User> children = userRepository.findByParent(user);
            for (User child : children) {
                child.setParent(batman);
                userRepository.save(child);
            }


            StoredProcedureQuery delete_user_sp = entityManager.createNamedStoredProcedureQuery("delete_user_sp");
            delete_user_sp.setParameter("userId", id);
            delete_user_sp.execute();

        } catch (Exception e) {
            e.printStackTrace();
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

    public List<ProfileDto> searchUser(String searchText, Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 20);
        List<User> users = userRepository.searchUser(searchText, pageable);
        List<ProfileDto> profileDtos = new ArrayList<>();
        for (User user : users) {
            profileDtos.add(toProfileDto(user));
        }
        return profileDtos;
    }


    //    @Scheduled(fixedRate = 6*60*60 * 1000, initialDelay = 60 * 1000)
//    private void calculateAllUserPoints() {
//        List<User> all = userRepository.findAll();
//        List<User> toBeSaved = new ArrayList<>();
//
//
//        for (User u : all) {
//            Integer p = calculateUserPoint(u);
//            u.setPoint(p);
//            toBeSaved.add(u);
//        }
//
//        userRepository.saveAll(toBeSaved);
//    }
    public Integer calculateUserPoint(User user) {

        Integer oldPoint = user.getPoint();
        Integer point = 0;

        /*
         * send and get request : 1 (max:8 of activity count)
         * accept a unique request:3(max:6 of activity count)
         * your request is accepted :2
         * positive review  : 3
         * being followed  : 2
         * being blocked -5
         * negative review: -5
         * opening an activity 2
         * complain 2
         * */

        int OPTIMAL_REQUEST_COUNT = 7;
        int OPTIMAL_APPROVAL_COUNT = 4;
        int ACCEPT_UNIQUE_REQUEST = 3;
        int POSITIVE_REVIEW = 2;
        int NEGATIVE_REVIEW = -2;
        int POSITIVE_VIBE = 8;
        int NEGATIVE_VIBE = -10;
        int VOTE_VIBE = 1;
        int APPROVED_REQUEST = 2;
        int BEING_FOLLOWED = 2;
        int BEING_BLOCKED = -5;
        int OPENING_ACTIVITY = 2;
        int COMPLAIN = 2;


        List<Activity> userActivities = activityRepository.findByCreatorOrderByDeadLineDesc(user);
        Integer activityCount = userActivities.size();

        //plain incoming requests
        Integer incomingRequestPoint = activityRequesRepository.incomingRequestCount(user);

        if (incomingRequestPoint > (activityCount * OPTIMAL_REQUEST_COUNT))
            incomingRequestPoint = activityCount * OPTIMAL_REQUEST_COUNT;

        point = point + incomingRequestPoint; //////// get request


        //opening an activity
        point = point + (activityCount * OPENING_ACTIVITY);


        //approved requests
        List<ActivityRequest> activityRequestList = activityRequesRepository.incomingApprovedRequests(user, ActivityRequestStatus.APPROVED);
        List<Long> userIds = new ArrayList<>();
        int approvalCount = 0;
        for (ActivityRequest r : activityRequestList) {

            if (r.getActivityRequestStatus() == ActivityRequestStatus.APPROVED) {
                Boolean uniqueUser = true;
                for (Long oldUserId : userIds) {
                    if (oldUserId == r.getApplicant().getId()) {
                        uniqueUser = false;
                        break;
                    }
                }
                if (uniqueUser) {
                    point = point + ACCEPT_UNIQUE_REQUEST; ////////////// accept a request
                    userIds.add(r.getApplicant().getId()); ////////accept a unique request
                    approvalCount++;
                    if (approvalCount > (OPTIMAL_APPROVAL_COUNT * activityCount))
                        break;
                }
            }
        }


        //positive-negative reviews
        List<Review> rewiReviewList = reviewRepository.findByReader(user);
        for (Review r : rewiReviewList) {
            if (r.getPositive())
                point = point + POSITIVE_REVIEW;  ////get a posivie review
            if (!r.getPositive())
                point = point + NEGATIVE_REVIEW; ////get a negative review
        }

        List<Vibe> vibeList = vibeRepository.findByReader(user);
        for (Vibe v : vibeList) {
            if (v.getVibeType() == VibeType.POSITIVE)
                point = point + POSITIVE_VIBE;
            if (v.getVibeType() == VibeType.NEGATIVE)
                point = point + NEGATIVE_VIBE;
        }


        List<Vibe> vibeListOfWriter = vibeRepository.findByWriter(user);

        Integer positiveVibeCount = 0;
        Integer negativeVibeCount = 0;

        for (Vibe v : vibeListOfWriter) {
            if (v.getVibeType() == VibeType.POSITIVE)
                positiveVibeCount++;
            if (v.getVibeType() == VibeType.NEGATIVE)
                negativeVibeCount++;
        }

        if (negativeVibeCount > positiveVibeCount) {
            user.setTooNegative(1);
        } else {
            point = point + vibeListOfWriter.size() * VOTE_VIBE;
            user.setTooNegative(0);
        }

        //send request and being accepted by activity owner
        List<ActivityRequest> activityRequests = activityRequestService.activityRequesRepository.findByApplicantId(user.getId());
        for (ActivityRequest a : activityRequests) {


            Integer requestResult = a.getResult();
            if (a.getResult() == null)
                requestResult = 1;

            if (a.getActivityRequestStatus() == ActivityRequestStatus.APPROVED && requestResult != 0) {
                point = point + APPROVED_REQUEST;  //////// your request is approved
            } else if (a.getActivityRequestStatus() == ActivityRequestStatus.APPROVED && requestResult == 0) {
                point = point - APPROVED_REQUEST;
            } else if (a.getActivityRequestStatus() == ActivityRequestStatus.WAITING && user.getGender() == Gender.FEMALE) {
                point = point + 1;
            }
        }

        //followed by someone
        Integer followerCount = followRepository.findFollowerCount(user);
        point = point + followerCount * BEING_FOLLOWED;

        //blocked by someoneac
        Integer blockedCount = blockService.blockRepository.blockerCount(user);
        point = point + (blockedCount * BEING_BLOCKED);  //////////being blocked


        //complain count
        Integer complainCount = complainRepository.countOfComplaintsByTheUser(user);
        point = point + (complainCount * COMPLAIN);


        //vibe limits the points
//        if (vibeList.size() == 0 && point > 20) {
//            point = 20;
//        } else if (vibeList.size() > 0 && vibeList.size() < 5 && point > 60) {
//            point = 50;
//        } else if (vibeList.size() > 5 && vibeList.size() < 10 && point > 150) {
//            point = 150;
//        }

        Integer newPoint = (point * 3) / 4;

        Integer extraPoint = user.getExtraPoint();
        if (extraPoint == null)
            extraPoint = 0;
        newPoint = newPoint + extraPoint;

        if (oldPoint < 10 && newPoint > 10 && user.getGender() == Gender.FEMALE && user.getParent() != null) {
            User parent = user.getParent();
            Integer parentPoint = parent.getExtraPoint();
            if (parentPoint == null)
                parentPoint = 10;

            parent.setExtraPoint(parentPoint);
            userRepository.save(parent);
        }


        return newPoint;
    }

    public List<ProfileDto> top100() {
        Pageable pageable = PageRequest.of(0, 100);
        List<User> users = userRepository.top100(pageable);

        List<ProfileDto> profileDtos = toProfileDtoList(users);
        return profileDtos;
    }


    public Integer followerCount(Long userId) {
        User user = userRepository.findById(userId).get();
        //followers
        Integer followerCount = followRepository.findFollowerCount(user);
        return followerCount;
    }


    public Integer attendanceRate(Long userId) {
        List<ActivityRequest> activityRequests = activityRequesRepository.findByApplicantId(userId);

        Integer approveCount = 0;
        Integer nonAttendCount = 0;

        for (ActivityRequest a : activityRequests) {

            Integer result = a.getResult();
            if (result == null)
                result = 1;

            if (a.getActivityRequestStatus() == ActivityRequestStatus.APPROVED) {
                approveCount++;
            }
            if (result == 0 && a.getActivityRequestStatus() == ActivityRequestStatus.APPROVED) {
                nonAttendCount++;
            }
        }

        if (approveCount > 0) {
            Integer attendCount = (approveCount - nonAttendCount) * 100;
            Integer percent = attendCount / approveCount;
            return percent;
        }
        return 0;

    }

    public ProfileDto toProfileDto(User user) {
        ProfileDto profileDto = modelMapper.map(user, ProfileDto.class);
        profileDto.setAge(UserUtil.calculateAge(user));
        profileDto.setInterests(hashtagService.findByUserStr(user));
        profileDto.setPremiumType(premiumService.userPremiumType(user));


        return profileDto;
    }

    public List<ProfileDto> toProfileDtoList(List<User> users) {
        List<ProfileDto> dtos = new ArrayList<>();

        for (User u : users) {
            dtos.add(toProfileDto(u));
        }
        return dtos;
    }


//    @Scheduled(fixedRate = 60*60 * 1000, initialDelay = 60 * 1000)
//    public void deleteTestUser() {
//        try {
//            User test = userRepository.findByEmail("ankaratangoclub@gmail.com").get();
//            StoredProcedureQuery delete_user_sp = entityManager.createNamedStoredProcedureQuery("delete_user_sp");
//            delete_user_sp.setParameter("userId", test.getId());
//            delete_user_sp.execute();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            User test2 = userRepository.findByEmail("cincihocam@hotmail.com").get();
//            StoredProcedureQuery delete_user_sp = entityManager.createNamedStoredProcedureQuery("delete_user_sp");
//            delete_user_sp.setParameter("userId", test2.getId());
//            delete_user_sp.execute();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
