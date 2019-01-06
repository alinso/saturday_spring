package com.alinso.myapp.repository;

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

    @Query("select meeting from Meeting meeting where meeting.deadLine > :now ")
    public List<Meeting> findAllNonExpired(@Param("now") Date now);

    public List<Meeting> findByCreatorOrderByIdDesc(User creator);
}
