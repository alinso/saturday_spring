package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("select meeting from Activity meeting where meeting.deadLine > :now and meeting.city= :city order by meeting.deadLine asc ")
    public List<Activity> findAllNonExpiredByCityIdOrderByDeadLine(@Param("now") Date now, @Param("city")City city, Pageable pageable);

    public List<Activity> findByCreatorOrderByDeadLineDesc(User creator);

    public Integer countActivitiesByCreator(User creator);

    @Query("select meeting from Activity meeting where meeting.deadLine > :start and meeting.deadLine < :finish and meeting.creator=:user")
    List<Activity> recentActivitiesOfCreator(@Param("start")Date start, @Param("finish")Date finish, @Param("user")User user);

    @Query("select meeting from Activity meeting where meeting.deadLine > :start and meeting.deadLine < :finish and meeting.isCommentNotificationSent=false")
    List<Activity> recentUncommentedActivities(@Param("start")Date start, @Param("finish")Date finish);


    @Query("select meeting from Activity meeting where  meeting.creator=:creator ")
    List<Activity> last3MonthActivitiesOfUser( @Param("creator") User creator);

    @Query("select count(meeting) from Activity meeting where  meeting.creator.gender=:gender and meeting.deadLine>:now ")
    Integer aasByGender(@Param("gender")Gender gender, @Param("now")Date now);

}
