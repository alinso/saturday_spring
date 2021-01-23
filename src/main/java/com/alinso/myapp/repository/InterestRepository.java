package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {


    @Query("select c from Interest c order by c.name asc")
    List<Interest> findAllOrderByNameAsc();
}
