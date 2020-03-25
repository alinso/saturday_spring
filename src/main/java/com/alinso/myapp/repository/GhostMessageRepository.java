package com.alinso.myapp.repository;

import com.alinso.myapp.entity.GhostMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GhostMessageRepository extends JpaRepository<GhostMessage,Long> {

    @Query("select g from GhostMessage g order by g.id DESC")
    List<GhostMessage> findLast200(Pageable pageable);

}
