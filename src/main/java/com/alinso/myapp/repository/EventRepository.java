package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.Interest;
import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select event from Event event where event.deadLine > :now and event.city= :city order by event.deadLine asc ")
    public List<Event> findAllNonExpiredByCityIdOrderByDeadLine(@Param("now") Date now, @Param("city")City city, Pageable pageable);

    public List<Event> findByCreatorOrderByDeadLineDesc(User creator);

    public Integer countEventsByCreator(User creator);


    @Query("select event from Event event where event.deadLine > :start and event.deadLine < :finish and event.isCommentNotificationSent=false")
    List<Event> recentUncommentedEvents(@Param("start")Date start, @Param("finish")Date finish);

    @Query("select event from Event event where  event.creator=:creator ")
    List<Event> last3MonthEventsOfUser(@Param("creator") User creator);

    @Query("select count(event) from Event event where  event.creator.gender=:gender and event.deadLine>:now ")
    Integer aasByGender(@Param("gender")Gender gender, @Param("now")Date now);

    @Query("select a from Event a where a.creator=:creator order by a.deadLine desc")
    List<Event> findByCreatorOrderByDeadLineDescPaged(@Param("creator") User creator, Pageable pageable);

    List<Event> findByInterestsOrderByDeadLine(@Param("interest") Interest interest, Pageable pageable);

    @Query("select a from Event a where a.deadLine>:now order by a.deadLine asc")
    List<Event> findAllOrderByDeadLineAsc(@Param("now") Date now, Pageable pageable);

    @Query("select a from Event  a where a.deadLine>:start and a.deadLine<:finish")
    List<Event> eventsOfDay(@Param("start") Date start, @Param("finish") Date finish);
}
