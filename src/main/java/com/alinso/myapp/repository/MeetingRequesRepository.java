package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.MeetingRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.MeetingRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MeetingRequesRepository extends JpaRepository<MeetingRequest, Long> {


    @Query("select meetingRequest from MeetingRequest meetingRequest where  meetingRequest.applicant = :applicant and meetingRequest.meeting=:meeting")
    public MeetingRequest findByMeetingaAndApplicant(@Param("applicant") User applicant, @Param("meeting") Meeting meeting);

    List<MeetingRequest> findByMeetingId(Long id);
    MeetingRequest findByApplicantId(Long id);

    @Query("select count(meetingRequest) from MeetingRequest meetingRequest " +
            "where  meetingRequest.meetingRequestStatus=:status and meetingRequest.meeting=:meeting")
    Integer countOfAprrovedForThisMeetingId(@Param("meeting") Meeting meeting, @Param("status")MeetingRequestStatus status);
}
