package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long>  {


    @Query("select e from Event e where e.date > :now order by date")
    List<Event> findNonExpiredEvents(@Param("now") Date now);
}
