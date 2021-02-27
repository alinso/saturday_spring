package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventView;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventViewRepository  extends JpaRepository<EventView,Long> {


    @Query("select e from EventView e where e.event=:event and e.viewer=:viewer")
    EventView findByEventAndViewer(@Param("event") Event event, @Param("viewer") User viewer);
}
