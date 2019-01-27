package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Hashtag;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Transactional
    @Modifying
    @Query("delete from Hashtag h where h.user=:user")
    void deleteByUser(@Param("user") User user);


    @Transactional
    @Modifying
    @Query("delete from Hashtag h where h.activity=:activity")
    void deleteByActivity(@Param("activity") Activity activity);

    List<Hashtag> findByActivity(Activity activity);

    List<Hashtag> findByUser(User user);
}