package com.alinso.myapp.repository;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("select user from User user where user.phone like CONCAT('%',:phone,'%') ")
    User findByPhone(@Param("phone") String phone);

    @Query("select user from User user where  CONCAT( user.name,  ' ', user.surname )  like CONCAT('%',:search,'%') ")
    List<User> searchUser(@Param("search") String search, Pageable pageable);

    @Query("select user from User  user where user.parent=:parent")
    List<User> findByParent(@Param("parent") User parent);

    User findByReferenceCode(String referenceCode);

    @Query("select count(*) from User")
    Integer getUserCount();

    User findByPassword(String pasword);


    //auto message queries
    @Query("select user from User user where user.gender=:gender and user.point<10")
    List<User> findZeroPointWomen(@Param("gender")Gender gender);


    @Query("select user from User user where user.gender=:gender")
    List<User> findAllWomen(@Param("gender")Gender gender);


    @Query("select user from User user where  user.point>0")
    List<User> findNonZeroUsers();

    @Query("select user from User user order by user.point desc")
    List<User> top100(Pageable pageable);

    @Query("select user from User user order by user.socialScore desc")
    List<User> socialScoreTop100(Pageable pageable);

    @Query("select user from User user where user.point>=0 and user.point<10")
    List<User> findInactiveUsers();


    @Query("select user from User  user where user.smsCode=:code")
    User findBySmsCode(@Param("code") Integer code);

    @Query("select user from User user where user.point>=20")
    List<User> findAbove20();

    @Query("select user from User user where user.createdAt > :August8")
    List<User> find8August(@Param("August8")Date august8);

    @Query("select user from User user where user.city=:city")
    List<User> allOfACity(@Param("city") City city);


    @Query("select user from User user where user.city=:city and user.gender=:female")
    List<User> allAnkaraWomen(@Param("city") City city, @Param("female") Gender female);

    @Query("select user from User user where user.city=:city and user.point>=:point")
    List<User> findAbovePoint(@Param("point") int point, @Param("city") City city);
}
