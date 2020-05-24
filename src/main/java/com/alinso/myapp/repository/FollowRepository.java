package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Follow;
import com.alinso.myapp.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {

    @Query("select follow from  Follow follow where follow.follower=:follower and follow.leader=:leader")
    Follow findFollowingByLeaderAndFollower(@Param("leader") User leader, @Param("follower")User follower);

    @Query("select follow.leader from  Follow follow where follow.follower=:follower ")
    List<User> findUsersFollowedByTheUser(@Param("follower") User loggedUser);

    @Query("select follow.follower from  Follow follow where follow.leader=:leader ")
    List<User> findFollowersOfUser(@Param("leader") User leader);

    @Query("select count(follow) from Follow  follow " +
            "where follow.leader=:user")
    Integer findFollowerCount(@Param("user") User user);

    @Query("select count(follow) from  Follow follow where follow.follower=:follower")
    Integer last3MonthsFollowingCount(@Param("follower")User user);

    @Query("select follow.leader from Follow follow group by follow.leader having count(follow)>100 order by count(follow) desc")
    List<User> maxFollowedUsers();

    @Query("select follow.follower from  Follow follow where follow.leader=:leader ")
    List<User> findFollowersOfUserPaged(@Param("leader") User leader, Pageable pageable);
}
