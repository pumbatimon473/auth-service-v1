package com.lld.authservicev1.services;

import com.lld.authservicev1.models.Session;
import com.lld.authservicev1.models.User;
import com.lld.authservicev1.repositories.SessionRepo;
import com.lld.authservicev1.repositories.UserRepo;
import com.lld.authservicev1.util.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class AuthServiceSingleSessionUnitTest {
    // Fields
    @Autowired
    private AuthService authService;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private SessionRepo sessionRepo;

    @Test
    void testLoginByEmailWithInvalidCredentialsReturnsNull() {
        // Arrange
        when(this.userRepo.findUserByEmailAndPassword("lui@disney.com", "crustycrab"))
                .thenReturn(Optional.empty());
        // Act
        String authToken = this.authService.loginByEmail("lui@disney.com", "crustycrab");
        // Assert
        assertThat(authToken)
                .isNull();
    }

    @Test
    void testLoginByEmailWithValidCredentialsReturnsAuthToken() {
        // Arrange
        User user = new User();
        user.setId(100L);
        user.setFullName("Launchpad McQuack");
        user.setEmail("launchpad@disney.com");
        user.setPassword("crashlanding");
        when(this.userRepo.findUserByEmailAndPassword("launchpad@disney.com", "crashlanding"))
                .thenReturn(Optional.of(user));
        String randomAuthToken = new RandomString(47, 122).ofLength(20);
        Session session = new Session(user, randomAuthToken);
        session.setId(1L);
        when(this.sessionRepo.save(any(Session.class))).thenReturn(session);
        // Act
        String authToken = this.authService.loginByEmail("launchpad@disney.com", "crashlanding");
        // Assert
        assertThat(authToken)
                .hasSize(20)
                .isEqualTo(randomAuthToken);
    }

    @Test
    void testValidateWithInvalidTokenRetunsEmptyUser() {
        // Arrange
        String invalidToken = "ygcdfvcgt";
        // Act
        Optional<User> userOptional = this.authService.validate(invalidToken);
        // Assert
        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    void testValidateWithValidTokenReturnsUser() {
        // Arrange
        String randomToken = new RandomString(47, 122).ofLength(20);
        User user = new User();
        user.setId(440L);
        user.setFullName("Magica");
        user.setEmail("magica@disney.com");
        user.setPassword("iwantscroogesnumberonecoin");
        Session session = new Session(user, randomToken);
        when(this.sessionRepo.findSessionByToken(randomToken)).thenReturn(Optional.of(session));
        // Act
        Optional<User> userOptional = this.authService.validate(randomToken);
        // Assert
        assertThat(userOptional)
                .isPresent();
        assertThat(userOptional.get())
                .returns(user.getId(), User::getId)
                .returns(user.getFullName(), User::getFullName)
                .returns(user.getEmail(), User::getEmail)
                .returns(null, User::getPassword)
                .returns(null, User::getActiveSessions);
    }
}