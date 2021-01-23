package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiscoverRepository extends JpaRepository<Announcement,Long>  {


    List<Announcement> findAll();

    @Query("select d.id from Announcement  d where d.youtube <> ''")
    List<Long> findIds();
}
