package com.schohub.repository;

import com.schohub.model.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface SessionTokenRepository extends JpaRepository<SessionToken, String> {

    @Modifying
    @Query("delete from SessionToken s where s.expiresAt < :now")
    int deleteExpired(@Param("now") Instant now);
}
