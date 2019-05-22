package com.alinso.myapp.repository;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    @Query("select user from User user where  CONCAT( user.name,  ' ', user.surname )  like CONCAT('%',:search,'%') ")
    List<User> searchUser(@Param("search") String search, Pageable pageable);

    @Query("select user from User  user where user.parent=:parent")
    List<User> findByParent(@Param("parent") User parent);

    Optional<User> findByReferenceCode(String referenceCode);

    @Query("select count(*) from User")
    Integer getUserCount();

    User findByPassword(String pasword);


    //auto message queries
    @Query("select user from User user where user.gender=:gender and user.point=0")
    List<User> findZeroPointWomen(@Param("gender")Gender gender);

    @Query("select user from User user where  user.point>0")
    List<User> findNonZeroUsers();

    @Query("select user from User user order by user.point desc")
    List<User> top100(Pageable pageable);
}
