package com.lld.authservicev1.services;

import com.lld.authservicev1.models.Session;
import com.lld.authservicev1.models.User;
import com.lld.authservicev1.repositories.SessionRepo;
import com.lld.authservicev1.repositories.UserRepo;
import com.lld.authservicev1.util.RandomString;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Allows only ONE ACTIVE SESSION per user
 */

@Service
public class AuthServiceSingleSession implements AuthService {
    // Fields
    private UserRepo userRepo;
    private SessionRepo sessionRepo;

    // CTOR
    public AuthServiceSingleSession(UserRepo userRepo, SessionRepo sessionRepo) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
    }

    // Behaviors
    @Override
    public String loginByEmail(String email, String password) {
        Optional<User> persistentUserOptional = userRepo.findUserByEmailAndPassword(email, password);
        if (persistentUserOptional.isEmpty()) return null;  // Invalid User
        // Generate a new auth-token
        String authToken = new RandomString(47, 122).ofLength(20);
        Optional<Session> persistentSessionOptional = this.sessionRepo.findSessionByUserId(persistentUserOptional.get().getId());
        if (persistentSessionOptional.isPresent()) {    // Invalidate the older session
            this.sessionRepo.deleteById(persistentSessionOptional.get().getId());
        }
        Session newSession = this.sessionRepo.save(new Session(persistentUserOptional.get(), authToken));
        return newSession.getToken();
    }

    @Override
    public Optional<User> validate(String userToken) {
        Optional<Session> persistentSessionOptional = this.sessionRepo.findSessionByToken(userToken);
        if (persistentSessionOptional.isEmpty()) return Optional.empty();
        // return only required User attributes
        User user = persistentSessionOptional.get().getUser();
        user.setPassword(null); // Removing password from the returned object
        user.setActiveSessions(null);
        return Optional.of(user);
    }
}
