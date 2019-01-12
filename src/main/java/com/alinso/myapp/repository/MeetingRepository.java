package com.alinso.myapp.repository;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("select meeting from Meeting meeting where meeting.deadLine > :now and meeting.city= :city order by meeting.deadLine asc ")
    public List<Meeting> findAllNonExpiredByCityIdOrderByDeadLine(@Param("now") Date now, @Param("city")City city);

    public List<Meeting> findByCreatorOrderByDeadLineDesc(User creator);

    @Query("select meeting from Meeting meeting where meeting.deadLine > :start and meeting.deadLine < :finish and meeting.creator=:user")
    List<Meeting> recentMeetingsOfCreator(@Param("start")Date start, @Param("finish")Date finish, @Param("user")User user);

}
