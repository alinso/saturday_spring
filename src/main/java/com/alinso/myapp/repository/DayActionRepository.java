package com.alinso.myapp.repository;

import com.alinso.myapp.entity.DayAction;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface DayActionRepository extends JpaRepository<DayAction, Long> {
    DayAction findByUser(User user);

    @Transactional
    @Modifying
    @Query("update DayAction d set d.eventCount=0")
    void clearEvent();


    @Transactional
    @Modifying
    @Query("update DayAction d set d.requestCount=0")
    void clearRequest();
}
