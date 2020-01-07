package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Invitation;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    @Query("select i from Invitation i where i.activity=:activity and i.reader=:reader")
    Invitation findByActivityAndReader(@Param("activity") Activity activity, @Param("reader") User reader);


    @Query("select i from Invitation i where i.activity=:activity ")
    List<Invitation> findByActivity(Activity activity);
}
