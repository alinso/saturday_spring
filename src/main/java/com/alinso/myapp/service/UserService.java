package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.entity.dto.security.ChangePasswordDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.dto.user.RegisterDto;
import com.alinso.myapp.entity.enums.EventRequestStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.VoteType;
import com.alinso.myapp.exception.RecordNotFound404Exception;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.*;
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

    // USER ID :1 AND USER ID 4 IS USED FOR TESTING.
// THESE 2 USERS SHOULD NOT BE BOTHERED

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
    ReferenceRepository referenceRepository;

    @Autowired
    CityService cityService;

    @Autowired
    UserEventService userEventService;


    @Autowired
    BlockService blockService;


    @Autowired
    VoteRepository voteRepository;


    @Autowired
    ComplainRepository complainRepository;

    //this the registration without mail verification
    public User register(RegisterDto registerDto) {

        User userInDb = userRepository.findByApprovalCode(registerDto.getApprovalCode());

        if (userInDb == null) {
            throw new UserWarningException("no user found!");
        }
        City ankara = cityService.findById(Long.valueOf(1));
        String newReferenceCode = referenceService.makeReferenceCode();

        userInDb.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
        userInDb.setPoint(0);
        userInDb.setTooNegative(0);
        userInDb.setExtraPercent(0);
        userInDb.setCity(ankara);
        userInDb.setApprovalCode(null);
        userInDb.setEnabled(true);
        userInDb.setGender(registerDto.getGender());
        User user = userRepository.save(userInDb);

        Reference reference = new Reference();
        reference.setReferenceCode(newReferenceCode);
        reference.setParent(user);
        referenceRepository.save(reference);

        return user;
    }


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

    public String getNameForRegistration(String approvalCode) {
        User user = userRepository.findByApprovalCode(approvalCode);
        return user.getName();
    }


    public void setLastLogin() {
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
        loggedUser.setBirthDate(DateUtil.stringToDate(profileInfoForUpdateDto.getbDateString(), "dd/MM/yyyy"));
        loggedUser.setMotivation(profileInfoForUpdateDto.getMotivation());
        loggedUser.setAbout(profileInfoForUpdateDto.getAbout());

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
            e.printStackTrace();
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


            //move children todo: aliinsan handle references
//            User batman = userRepository.getOne(Long.valueOf(3211));
//            List<User> children = referenceService.getChildrenOfParent(user);
//            for (User child : children) {
//                child.setParent(batman);
//                userRepository.save(child);
//            }

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
        searchText.replaceAll("\\s+", "");
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

        if (user.getId() == 3212) {
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

        if (user.getGender() == Gender.FEMALE)
            OPENING_EVENT = 10;

        int FOLLOW = 1;


        List<Event> userLast3MonthEvents = eventRepository.last3MonthEventsOfUser(user);
        Integer eventCount = userLast3MonthEvents.size();

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

        if (negativeVoteCount * 6 > positiveVoteCount * 4) {
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


//    public List<ProfileDto> top100() {
//        Pageable pageable = PageRequest.of(0, 100);
//        List<User> users = userRepository.top100(pageable);
//
//        List<ProfileDto> profileDtos = toProfileDtoList(users);
//
//        return profileDtos;
//    }


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














