package com.lld.authservicev1.services;

import com.lld.authservicev1.models.Session;
import com.lld.authservicev1.models.User;
import com.lld.authservicev1.repositories.SessionRepo;
import com.lld.authservicev1.repositories.UserRepo;
import com.lld.authservicev1.util.RandomString;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class AuthServiceSingleSessionIntegrationTest {
    // Fields
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SessionRepo sessionRepo;

    @Test
    @Transactional
    void test_login_withInvalidCredentials_returnsNull() {
        // Arrange
        User user = new User();
        user.setFullName("Donald Duck");
        user.setEmail("donald@disney.com");
        user.setPassword("troublealwaysfindsme");
        User savedUser = this.userRepo.save(user);
        // Act
        String authToken = this.authService.loginByEmail(user.getEmail(), "troubletroubletrouble");
        // Assert
        assertThat(authToken).isNull();
    }

    @Test
    @Transactional
    void test_login_withValidCredentials_returnsAuthToken() {
        // Arrange
        User user = new User();
        user.setFullName("Donald Duck");
        user.setEmail("donald@disney.com");
        user.setPassword("troublealwaysfindsme");
        User savedUser = this.userRepo.save(user);
        // Act
        String authToken = this.authService.loginByEmail(user.getEmail(), user.getPassword());
        // Assert
        assertThat(authToken).hasSize(20);
    }

    @Test
    @Transactional
    void test_validate_withInvalidToken_returnsEmptyUser() {
        // Arrange
        User user = new User();
        user.setFullName("Donald Duck");
        user.setEmail("donald@disney.com");
        user.setPassword("troublealwaysfindsme");
        User savedUser = this.userRepo.save(user);
        String authToken = new RandomString(47, 122).ofLength(20);
        Session savedSession = this.sessionRepo.save(new Session(savedUser, authToken));
        // Act
        Optional<User> userOptional = this.authService.validate(new RandomString(47, 122).ofLength(20));
        // Assert
        assertThat(userOptional).isEmpty();
    }

    @Test
    @Transactional
    void test_validate_withValidToken_returnsUser() {
        // Arrange
        User user = new User();
        user.setFullName("Donald Duck");
        user.setEmail("donald@disney.com");
        user.setPassword("troublealwaysfindsme");
        User savedUser = this.userRepo.save(user);
        String authToken = new RandomString(47, 122).ofLength(20);
        Session savedSession = this.sessionRepo.save(new Session(savedUser, authToken));
        // Act
        Optional<User> userOptional = this.authService.validate(authToken);
        // Assert
        assertThat(userOptional)
                .isPresent();
        assertThat(userOptional.get())
                .returns(savedUser.getId(), User::getId)
                .returns(savedUser.getFullName(), User::getFullName)
                .returns(savedUser.getEmail(), User::getEmail)
                .returns(null, User::getPassword)   // Service should not return the password of the User
                .returns(null, User::getActiveSessions);    // Service should not return the list of active sessions
    }
}