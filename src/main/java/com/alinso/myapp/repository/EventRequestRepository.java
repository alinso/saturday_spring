package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.EventRequestStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {



    @Query("select eventRequest from EventRequest eventRequest where  eventRequest.applicant = :applicant and eventRequest.event=:event " +
            "and eventRequest.applicant.enabled=true")
    public EventRequest findByEventAndApplicant(@Param("applicant") User applicant, @Param("event") Event event);

    List<EventRequest> findByEventId(Long id);
    List<EventRequest> findByApplicantId(Long id);

    @Query("select count(eventRequest) from EventRequest eventRequest " +
            "where  eventRequest.eventRequestStatus=:status and eventRequest.event=:event and eventRequest.applicant.enabled=true")
    Integer countOfAprrovedForThisEvent(@Param("event") Event event, @Param("status") EventRequestStatus status);

    @Query("select eventRequest.event from EventRequest eventRequest " +
            "where  eventRequest.eventRequestStatus=:status and eventRequest.applicant=:user and eventRequest.result=1 " +
            "and eventRequest.applicant.enabled=true")
    List<Event> eventsAttendedByUser(@Param("user") User user, @Param("status") EventRequestStatus status);

    @Query("select eventRequest.applicant from EventRequest eventRequest where eventRequest.event=:event and eventRequest.eventRequestStatus=:status" +
            " and eventRequest.applicant.enabled=true")
    List<User> attendantsOfEvent(@Param("event") Event event, @Param("status") EventRequestStatus status);


    @Query("select count(eventRequest) from EventRequest eventRequest " +
            "where eventRequest.event.creator = :user1 " +
            "and eventRequest.applicant=:user2 " +
            "and eventRequest.event.deadLine>:twoDaysEgo " +
            "and eventRequest.eventRequestStatus=:status" +
            " and eventRequest.applicant.enabled=true" +
            " and eventRequest.event.creator.enabled=true")
    Integer haveUser1HostUser2(@Param("user1") User user1, @Param("user2") User user2, @Param("status") EventRequestStatus status, @Param("twoDaysEgo") Date twoDaysEgo);


    @Query("select count(eventRequest) from EventRequest  eventRequest"+
            " where eventRequest.event.creator=:user" +
            " and eventRequest.applicant.enabled=true")
    Integer incomingRequestCount(@Param("user") User user);

    @Query("select eventRequest from EventRequest  eventRequest"+
            " where eventRequest.event.creator=:user" +
            " and eventRequest.eventRequestStatus=:status" +
            " and eventRequest.applicant.enabled=true")
    List<EventRequest> incomingApprovedRequests(@Param("user") User user, @Param("status") EventRequestStatus status);

    @Query("delete from EventRequest r where r.event=:event ")
    void deleteByEventCreator(@Param("event") Event event);


    @Query("select count(eventRequest) from EventRequest eventRequest " +
            "where eventRequest.event.creator = :user1 " +
            "and eventRequest.applicant=:user2 " +
            " and eventRequest.result=1 "+
            "and eventRequest.eventRequestStatus=:status" +
            " and eventRequest.applicant.enabled=true" +
            " and eventRequest.event.creator.enabled=true")
    Integer haveUser1HostUser2AllTimes(@Param("user2") User user2, @Param("user1") User user1, @Param("status") EventRequestStatus status);

    @Query("select eventRequest from EventRequest  eventRequest"+
            " where eventRequest.event.creator=:user" +
            " and eventRequest.eventRequestStatus=:status" +
            " and eventRequest.applicant.enabled=true" )
    List<EventRequest> last3MonthsIncomingApprovedRequests(@Param("user")User user, @Param("status") EventRequestStatus approved);

    @Query("select count(eventRequest) from EventRequest  eventRequest"+
            " where eventRequest.applicant=:user" +
            " and eventRequest.event.creator.enabled=true" )
    Integer last3MonthSentRequestsOfUser(@Param("user")User user);

    @Query("select count(eventRequest) from EventRequest eventRequest where eventRequest.applicant=:user and " +
            "eventRequest.eventRequestStatus=:approved and eventRequest.result=1 and eventRequest.event.creator.enabled=true")
    Integer findApprovedRequestCountByApplicant(@Param("user") User user, @Param("approved") EventRequestStatus approved);

    @Query("select eventRequest.applicant from EventRequest eventRequest where eventRequest.event=:event and eventRequest.applicant.enabled=true")
    List<User> applicantsOfEvent(@Param("event") Event event);

    @Query("select eventRequest.event from EventRequest eventRequest " +
            "where  eventRequest.eventRequestStatus=:status and eventRequest.applicant=:user and eventRequest.result=1" +
            " and eventRequest.event.creator.enabled=true order by eventRequest.createdAt desc")
    List<Event> activitiesAttendedByUserPaged(@Param("user")User user, @Param("status") EventRequestStatus status, Pageable pageable);
}
