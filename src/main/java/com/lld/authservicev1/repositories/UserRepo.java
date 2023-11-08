package com.lld.authservicev1.repositories;

import com.lld.authservicev1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    // Behaviors
    Optional<User> findUserByEmailAndPassword(String email, String password);
}
