package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.Invitation;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    @Query("select i from Invitation i where i.event=:event and i.reader=:reader and i.reader.enabled=true and i.event.creator.enabled=true")
    Invitation findByEventAndReader(@Param("event") Event event, @Param("reader") User reader);


    @Query("select i from Invitation i where i.event=:event and i.reader.enabled=true and i.event.creator.enabled=true")
    List<Invitation> findByEvent(Event event);
}
