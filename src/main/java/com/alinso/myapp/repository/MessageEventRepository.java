package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.MessageEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageEventRepository extends JpaRepository<MessageEvent,Long> {

    @Query("select m from MessageEvent m where m.event=:event and m.writer.enabled=true and m.event.creator.enabled=true order by m.id")
    List<MessageEvent> findByEvent(@Param("event") Event event);
}
