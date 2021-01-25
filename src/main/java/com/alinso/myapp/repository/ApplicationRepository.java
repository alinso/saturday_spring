package com.alinso.myapp.repository;


import com.alinso.myapp.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {


    @Query("select a from Application a where referenceCode=:referenceCode")
    Application findByReferenceCode(@Param("referenceCode") String referenceCode);

}
