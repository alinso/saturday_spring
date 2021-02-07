package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Follow;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.FollowStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {

    @Query("select follow from  Follow follow where follow.follower=:follower and follow.leader=:leader" +
            " and follow.follower.enabled=true and follow.leader.enabled=true")
    Follow findFollowingByLeaderAndFollower(@Param("leader") User leader, @Param("follower")User follower);

    @Query("select follow from  Follow follow where follow.follower=:follower " +
            " and follow.follower.enabled=true and follow.leader.enabled=true")
    List<Follow> findFollowingsOfTheUser(@Param("follower") User loggedUser);

    @Query("select follow from  Follow follow where follow.leader=:leader " +
            " and follow.follower.enabled=true and follow.leader.enabled=true")
    List<Follow> findFollowersOfUser(@Param("leader") User leader);

    @Query("select count(follow) from Follow  follow where follow.leader=:user and follow.status=:status " +
            " and follow.follower.enabled=true and follow.leader.enabled=true")
    Integer findFollowerCount(@Param("user") User user, @Param("status")FollowStatus status);

    @Query("select count(follow) from  Follow follow where follow.follower=:follower" +
            " and follow.follower.enabled=true and follow.leader.enabled=true")
    Integer last3MonthsFollowingCount(@Param("follower")User user);

    @Query("select follow.leader from Follow follow group by follow.leader having count(follow)>100 order by count(follow) desc")
    List<User> maxFollowedUsers();

    @Query("select follow from  Follow follow where follow.leader=:leader  and follow.follower.enabled=true and follow.leader.enabled=true")
    List<Follow> findFollowersOfUserPaged(@Param("leader") User leader, Pageable pageable);
}
