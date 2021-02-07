package com.alinso.myapp.repository;


import com.alinso.myapp.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {

    @Query("select a from Application a order by id DESC ")
    List<Application> all(Pageable pageable);

    @Query("select a from Application a where a.phone=:phone and a.name=:name")
    List<Application> findByPhoneAndName(@Param("phone") String phone, @Param("name") String name);

    @Query("select a from Application a where a.referenceCode=:referenceCode")
    List<Application> findByReferenceCode(@Param("referenceCode") String referenceCode);
}
