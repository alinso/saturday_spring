package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventPhotoRepository extends JpaRepository<EventPhoto,Long> {

    List<EventPhoto> findPhotosByEvent(Event event);
    Optional<EventPhoto> findByFileName(String fileName);

}
