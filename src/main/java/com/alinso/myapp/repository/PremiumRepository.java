package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Premium;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PremiumRepository extends JpaRepository<Premium, Long> {

    @Query("select p from Premium p where p.user=:user")
    Optional<Premium> findPremiumRecordOfByUser(@Param("user") User user);
}
