package com.lld.authservicev1.services;

import com.lld.authservicev1.models.User;

import java.util.Optional;

public interface AuthService {
    String loginByEmail(String email, String password); // Returns AuthToken for the existing user
    Optional<User> validate(String userToken);    // Returns User details if the provided token is associated with a session
}
