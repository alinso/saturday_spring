package com.alinso.myapp.repository;

import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    @Query("select user from User user where  CONCAT( user.name,  ' ', user.surname )  like (:search) ")
    List<User> searchUser(@Param("search") String search);
}
