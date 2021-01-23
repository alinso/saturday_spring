package com.alinso.myapp.repository;

import com.alinso.myapp.entity.MessageWall;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageWallRepository extends JpaRepository<MessageWall,Long> {

    @Query("select g from MessageWall g order by g.id DESC")
    List<MessageWall> findLast200(Pageable pageable);

}
