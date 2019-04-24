package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ActivityRequesRepository extends JpaRepository<ActivityRequest, Long> {



    @Query("select activityRequest from ActivityRequest activityRequest where  activityRequest.applicant = :applicant and activityRequest.activity=:activity")
    public ActivityRequest findByActivityAndApplicant(@Param("applicant") User applicant, @Param("activity") Activity activity);

    List<ActivityRequest> findByActivityId(Long id);
    ActivityRequest findByApplicantId(Long id);

    @Query("select count(activityRequest) from ActivityRequest activityRequest " +
            "where  activityRequest.activityRequestStatus=:status and activityRequest.activity=:activity")
    Integer countOfAprrovedForThisActivity(@Param("activity") Activity activity, @Param("status") ActivityRequestStatus status);

    @Query("select activityRequest.activity from ActivityRequest activityRequest " +
            "where  activityRequest.activityRequestStatus=:status and activityRequest.applicant=:user")
    List<Activity> activitiesAttendedByUser(@Param("user") User user, @Param("status") ActivityRequestStatus status);

    @Query("select activityRequest.applicant from ActivityRequest activityRequest where activityRequest.activity=:activity and activityRequest.activityRequestStatus=:status")
    List<User> attendantsOfActivity(@Param("activity") Activity activity, @Param("status") ActivityRequestStatus status);


    @Query("select count(activityRequest) from ActivityRequest activityRequest " +
            "where activityRequest.activity.creator = :user1 " +
            "and activityRequest.applicant=:user2 " +
            "and activityRequest.activityRequestStatus=:status")
    Integer haveUser1HostUser2(@Param("user1") User user1, @Param("user2") User user2, @Param("status") ActivityRequestStatus status);


}
