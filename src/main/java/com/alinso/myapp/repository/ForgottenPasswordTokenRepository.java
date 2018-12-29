package com.alinso.myapp.repository;

import com.alinso.myapp.entity.ForgottenPasswordToken;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgottenPasswordTokenRepository extends JpaRepository<ForgottenPasswordToken,Long> {
    Optional<ForgottenPasswordToken> findByToken(String token);
    Optional<ForgottenPasswordToken> findByUser(User user);

}
