package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Notification;
import com.alinso.myapp.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
NotificationRepository extends JpaRepository<Notification,Long> {

    @Query("select notification from Notification  notification where notification.target=:target and notification.isRead=false" +
            " and notification.trigger.enabled=true")
    List<Notification> findTargetNotReadedNotifications(@Param("target")User target);

    List<Notification> findByTarget(User target);

    @Query("select n from Notification n where n.target=:target and n.trigger.enabled=true order by id desc ")
    List<Notification> findByTargetOrderByCreatedAtDesc(@Param("target") User target, Pageable pageable);

}
