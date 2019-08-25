package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Complain;
import com.alinso.myapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplainRepository extends JpaRepository<Complain,Long> {

    @Query("select count(c) from Complain c where c.reporter=:user")
    Integer countOfComplaintsByTheUser(@Param("user") User user);
}
