package com.alinso.myapp.repository;

import com.alinso.myapp.entity.GhostNotification;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GhostNotificationRepository extends JpaRepository<GhostNotification,Long> {


    GhostNotification findByReceiver(User u);

    @Query("select g from GhostNotification g where g.send=:booleanx")
    List<GhostNotification> notificationList(@Param("booleanx") boolean b);
}
