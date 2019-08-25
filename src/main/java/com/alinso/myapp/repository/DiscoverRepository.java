package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Discover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface DiscoverRepository extends JpaRepository<Discover,Long>  {


    List<Discover> findAll();

    @Query("select d.id from Discover  d where d.youtube <> ''")
    List<Long> findIds();
}
