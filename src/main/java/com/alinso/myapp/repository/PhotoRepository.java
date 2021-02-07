package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Photo;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

    @Query("select p from Photo p where p.user=:user and p.user.enabled=true")
    List<Photo> findByUser(@Param("user") User user);

    @Query("select p from Photo  p where p.fileName=:fileName and p.user.enabled=true")
    Optional<Photo> findByFileName(@Param("fileName") String fileName);

}
