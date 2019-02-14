package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Discover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface DiscoverRepository extends JpaRepository<Discover,Long>  {


    @Query("select e from Discover e where e.date > :now order by date")
    List<Discover> findNonExpiredEvents(@Param("now") Date now);
}
