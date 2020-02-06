package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityPhotoRepository extends JpaRepository<ActivityPhoto,Long> {

    List<ActivityPhoto> findActivityPhotosByActivity(Activity activity);
    Optional<ActivityPhoto> findByFileName(String fileName);

}
