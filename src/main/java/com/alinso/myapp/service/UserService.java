package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.entity.dto.security.ChangePasswordDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.enums.EventRequestStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.PremiumDuration;
import com.alinso.myapp.entity.enums.VoteType;
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
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.util.*;

@Service
public class UserService {


    @Autowired
    ReviewService reviewService;

    @Autowired
    EventRequestService eventRequestService;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BlockRepository blockRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    VoteService voteService;

    @Autowired
    EventRequestRepository eventRequestRepository;

    @Autowired
    PhotoService photoService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

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
    VoteRepository voteRepository;


    @Autowired
    ComplainRepository complainRepository;

    //this the registration without mail verification
    public User register(User newUser) {

        City ankara = cityService.findById(Long.valueOf(1));
        String referenceCode = referenceService.makeReferenceCode();


        //reference code for men
        Integer starterPoint = 0;
        User parent=null;
        if(newUser.getGender()==Gender.MALE) {
            parent = userRepository.findByReferenceCode(newUser.getReferenceCode());
        }

        if(newUser.getGender()==Gender.FEMALE && !newUser.getReferenceCode().equals("")){
            parent = userRepository.findByReferenceCode(newUser.getReferenceCode());
        }

        if (newUser.getPhone().length() == 10)
            newUser.setPhone("0" + newUser.getPhone());

        newUser.setConfirmPassword("");
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        newUser.setPoint(0);
        newUser.setTooNegative(0);
        newUser.setExtraPoint(starterPoint);
        newUser.setCity(ankara);

        newUser.setReferenceCode(referenceCode);
        newUser.setEnabled(false);
        newUser.setParent(parent);


        Random rnd = new Random();
        Integer code = rnd.nextInt(999999);
        newUser.setSmsCode(code);
        User user = userRepository.save(newUser);


       // if (parent != null && parent.getId()!=3212) {
        //    parent.setReferenceCode(referenceService.makeReferenceCode());
        //    userRepository.save(parent);
        //}


        SendSms.send("Activuss kaydı tamamlamak için sms onay kodu : " + code.toString(), newUser.getPhone());
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
        user.setSmsCode(null);
        userEventService.newUserRegistered(user);


        Premium premium = new Premium();
        premium.setStartDate(new Date());


        if(user.getGender()==Gender.FEMALE) {
            premium.setDuration(PremiumDuration.GTHREE_MONTHS);
            premiumService.saveGift(premium, user);
            user.setTrialUser(0);

        }
        if(user.getGender()==Gender.MALE) {
            user.setTrialUser(99);
        }
        userRepository.save(user);

        return user;
    }


    public Integer getMaleCount() {
        City city=cityService.findById(Long.valueOf(1));
        return userRepository.getUserCountGende(city,Gender.MALE);
    }
    public Integer getFemaleCount() {
        City city=cityService.findById(Long.valueOf(1));
        return userRepository.getUserCountGende(city,Gender.FEMALE);
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

        SendSms.send("Saturday yeni şifreniz : " + pass.toString(), phone);
    }

//    public void verifyMail(String tokenString) {
//        MailVerificationToken token = mailVerificationTokenService.findByActiveToken(tokenString);
//
//        if (token == null) {
//            throw new RecordNotFound404Exception("Geçersiz link");
//        }
//        User user = userRepository.findById(token.getWriter().getId()).get();
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

//    public void resetPassword(ResetPasswordDto resetPasswordDto) {
//        if (resetPasswordDto.getToken() == null) {
//            throw new RecordNotFound404Exception("Geçersiz link");
//        }
//
//        ForgottenPasswordToken token = forgottenPasswordTokenService.findByToken(resetPasswordDto.getToken());
//
//        User user = userRepository.findById(token.getUser().getId()).get();
//        user.setPassword(bCryptPasswordEncoder.encode(resetPasswordDto.getPassword()));
//        forgottenPasswordTokenService.delete(token);
//        userRepository.save(user);
//    }


    public void setLastLogin(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setLastLogin(new Date());
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
        loggedUser.setNick(profileInfoForUpdateDto.getNick());
        //loggedUser.setGender(profileInfoForUpdateDto.getGender());
        //loggedUser.setInterests(profileInfoForUpdateDto.getInterests());
        //loggedUser.setReferenceCode(profileInfoForUpdateDto.getReferenceCode());
       // hashtagService.saveUserHashtag(loggedUser, profileInfoForUpdateDto.getInterests());

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
           // profileInfoForUpdateDto.setInterests(hashtagService.findByUserStr(user));

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
            List<Event> res = eventRepository.findByCreatorOrderByDeadLineDesc(user);

            for (Event a : res) {
                List<EventRequest> eventRequests = eventRequestRepository.findByEventId(a.getId());
                for (EventRequest r : eventRequests) {
                    eventRequestRepository.deleteById(r.getId());
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
            List<Event> meetingsCreatedByUser = eventRepository.findByCreatorOrderByDeadLineDesc(user);
            for (Event a : meetingsCreatedByUser) {
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
        searchText.replaceAll("\\s+","");
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

        if(user.getId()==3212){
            return 0;
        }

        Integer point = 0;

        /*
         * send  request : 1 (max:8 of activity count)
         * accept a unique request:2(max:6 of activity count)
         * vote :1
         * write review  : 2
         * opening an activity 5
         * complain 2
         * */

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        Date threeMonthsAgo = cal.getTime();

        int ACCEPT_UNIQUE_REQUEST = 2;
        int WRITE_REVIEW = 2;
        int VOTE = 1;
        int SEND_REQUEST = 2;
        int OPENING_EVENT = 5;

        if(user.getGender()==Gender.FEMALE)
            OPENING_EVENT=10;

        int FOLLOW = 1;


        List<Event> userActivities = eventRepository.last3MonthActivitiesOfUser( user);
        Integer eventCounts = userActivities.size();

        //opening an activity
        point = point + (eventCount * OPENING_EVENT);


        //send a request
        Integer requestCount = eventRequestRepository.last3MonthSentRequestsOfUser(user);
        point = point + requestCount * SEND_REQUEST;


        //approved unique requests
        List<EventRequest> eventRequestList = eventRequestRepository.last3MonthsIncomingApprovedRequests(user, EventRequestStatus.APPROVED);
        List<Long> userIds = new ArrayList<>();
        for (EventRequest r : eventRequestList) {

            if (r.getEventRequestStatus() == EventRequestStatus.APPROVED) {
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
                }
            }
        }


        //review count
        List<Review> rewiReviewList = reviewRepository.last3MonthReviewsOfUser(user);
        point = point + (rewiReviewList.size() * WRITE_REVIEW);


        //voting
        List<Vote> voteListOfWriter = voteRepository.findByWriter(user);
        Integer positiveVoteCount = 0;
        Integer negativeVoteCount = 0;

        for (Vote v : voteListOfWriter) {
            if (v.getVoteType() == VoteType.POSITIVE)
                positiveVoteCount++;
            if (v.getVoteType() == VoteType.NEGATIVE)
                negativeVoteCount++;
        }

        if (negativeVoteCount*6 > positiveVoteCount*4) {
            user.setTooNegative(1);
        } else {
            point = point + voteListOfWriter.size() * VOTE;
            user.setTooNegative(0);
        }


        //follow someone
        //Integer followingCount = followRepository.last3MonthsFollowingCount(user);
        //point = point + followingCount * FOLLOW;


        //complain count
       // Integer complainCount = complainRepository.last3MonthscountOfComplaintsByTheUser(user, threeMonthsAgo);
       // point = point + (complainCount * COMPLAIN);


        return point;
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
        List<EventRequest> eventRequests = eventRequestRepository.findByApplicantId(userId);

        Integer approveCount = 0;
        Integer nonAttendCount = 0;

        for (EventRequest a : eventRequests) {

            Integer result = a.getResult();
            if (result == null)
                result = 1;

            if (a.getEventRequestStatus() == EventRequestStatus.APPROVED) {
                approveCount++;
            }
            if (result == 0 && a.getEventRequestStatus() == EventRequestStatus.APPROVED) {
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
        //profileDto.setInterests(hashtagService.findByUserStr(user));
        profileDto.setPremiumType(premiumService.userPremiumType(user));
        profileDto.setReferenceCode("");

        return profileDto;
    }

    public List<ProfileDto> toProfileDtoList(List<User> users) {
        List<ProfileDto> dtos = new ArrayList<>();

        for (User u : users) {
            dtos.add(toProfileDto(u));
        }
        return dtos;
    }


    public Integer attendanceRateOfRequestOwner(Long id) {
        EventRequest r = eventRequestRepository.findById(id).get();
        User user = r.getApplicant();

        return attendanceRate(user.getId());
    }
}














