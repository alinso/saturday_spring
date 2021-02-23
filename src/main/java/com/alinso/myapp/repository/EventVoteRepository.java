package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventVote;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventVoteRepository extends JpaRepository<EventVote,Long> {

    @Query("select e from EventVote e where e.event=:event and e.voter=:voter")
    EventVote findByVoterAndEvent(@Param("voter") User voter, @Param("event") Event event);

    @Query("select sum(e.vote) from EventVote e where e.event=:event")
    Integer findTotalByEvent(@Param("event") Event event);
}
