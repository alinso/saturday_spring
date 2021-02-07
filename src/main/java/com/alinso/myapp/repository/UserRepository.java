package com.alinso.myapp.repository;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select user from User user where user.phone=:phone  and user.enabled=true")
    User findByPhone(@Param("phone") String phone);

    @Query("select user from User user where  CONCAT( user.name,   user.surname )  like CONCAT('%',:search,'%')  and user.enabled=true")
    List<User> searchUser(@Param("search") String search, Pageable pageable);

    @Query("select count(*) from User")
    Integer getUserCount();

    User findByPassword(String pasword);

    //auto message queries
    @Query("select user from User user where user.gender=:gender and user.point<10 and user.enabled=true")
    List<User> findZeroPointWomen(@Param("gender")Gender gender);


    @Query("select user from User user where user.gender=:gender  and user.enabled=true")
    List<User> findAllWomen(@Param("gender")Gender gender);


    @Query("select user from User user where  user.point>0  and user.enabled=true")
    List<User> findNonZeroUsers();


    @Query("select user from User user where user.point>=0 and user.point<10  and user.enabled=true")
    List<User> findInactiveUsers();


    @Query("select user from User user where user.point>=20  and user.enabled=true")
    List<User> findAbove20();


    @Query("select user from User user where user.city=:city  and user.enabled=true")
    List<User> allOfACity(@Param("city") City city);


    @Query("select user from User user where user.city=:city and user.gender=:female  and user.enabled=true")
    List<User> allAnkaraWomen(@Param("city") City city, @Param("female") Gender female);

    @Query("select user from User user where user.city=:city and user.point>=:point  and user.enabled=true")
    List<User> findAbovePoint(@Param("point") int point, @Param("city") City city);


    @Query("select count(user) from User user where user.gender=:gender and user.createdAt>:start and user.createdAt<:finish  and user.enabled=true")
    Integer userCountCreatedGivenDate(@PathVariable("gender") Gender gender, @Param("start")Date start, @Param("finish") Date finish);

    @Query("select count(user) from User user where user.gender=:gender and user.city=:city and user.point>:point  and user.enabled=true")
    Integer userCountByGenderCityPoint(@Param("gender")Gender gender, @Param("city")City ankara, @Param("point")int point);

    @Query("select count(user) from User user where user.gender=:gender and user.createdAt>:start  and user.enabled=true")
    Integer userCountCreatedToday(@Param("gender") Gender gender, @Param("start")Date start);


    @Query("select count(user) from User user where user.gender=:gender and user.city=:city  and user.enabled=true")
    Integer getUserCountGende(@Param("city")City city, @Param("gender")Gender gender);


    @Query("select u from User u where u.approvalCode=:approvalCode and u.approvalCode is not null ")
    User findByApprovalCode(@Param("approvalCode") String approvalCode);

    @Query("select u from User u where u.name=:name")
    List<User> findByName(@Param("name") String name);

    @Query("select count(a) from User a  where a.approvalCode=:code")
    Integer findCountByApprovalCode(@Param("code") String code);
}

