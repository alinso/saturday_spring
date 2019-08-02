package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.MessageActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageActivityRepository extends JpaRepository<MessageActivity,Long> {

    @Query("select m from MessageActivity m where m.activity=:activity order by m.id")
    List<MessageActivity> findByActivity(@Param("activity")Activity activity);
}
