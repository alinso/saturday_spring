package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Photo;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

    @Override
    Optional<Photo> findById(Long aLong);

    List<Optional<Photo>> findByUser(User user);
}