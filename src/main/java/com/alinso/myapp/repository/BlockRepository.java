package com.alinso.myapp.repository;

import com.alinso.myapp.entity.Block;
import com.alinso.myapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping
public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("select block from  Block block where block.blocker=:blocker and block.blocked=:blocked")
    Block findBlockByBlockedAndBlocker(@Param("blocked") User blocked, @Param("blocker")User blocker);

    @Query("select block.blocked from  Block block where block.blocker=:blocker")
    List<User> findUsersBlcokedByTheUser(@Param("blocker") User blocker);

}
