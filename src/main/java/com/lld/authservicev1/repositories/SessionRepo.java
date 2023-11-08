package com.lld.authservicev1.repositories;

import com.lld.authservicev1.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepo extends JpaRepository<Session, Long> {
    Optional<Session> findSessionByUserId(Long userId);

    Optional<Session> findSessionByToken(String userToken);
}
