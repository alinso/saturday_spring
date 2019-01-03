package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    public List<Meeting> findAllByOrderByIdDesc();
}
