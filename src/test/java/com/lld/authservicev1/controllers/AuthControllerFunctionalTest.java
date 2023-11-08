package com.lld.authservicev1.controllers;

import com.lld.authservicev1.dtos.UserCredDto;
import com.lld.authservicev1.dtos.UserDto;
import com.lld.authservicev1.dtos.UserTokenDto;
import com.lld.authservicev1.exceptions.InvalidLoginCredentials;
import com.lld.authservicev1.exceptions.InvalidToken;
import com.lld.authservicev1.models.Session;
import com.lld.authservicev1.models.User;
import com.lld.authservicev1.repositories.SessionRepo;
import com.lld.authservicev1.repositories.UserRepo;
import com.lld.authservicev1.util.RandomString;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerFunctionalTest {
    // Fields
    @Autowired
    private AuthController authController;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SessionRepo sessionRepo;

    @Test
    @Transactional
    @Rollback(value = true)
    void test_login_withInvalidUserCredentials_returnsNull() {
        // Arrange
        User user = new User();
        user.setFullName("Scrooge McDuck");
        user.setEmail("scrooge@disney.com");
        user.setPassword("scroogyrichestduck");
        User savedUser = this.userRepo.save(user);
        UserCredDto userCredDto = new UserCredDto(user.getEmail(), "scroogerichestduct");
        // Act
        InvalidLoginCredentials invalidLoginCredentials = assertThrows(InvalidLoginCredentials.class,
                () -> this.authController.login(userCredDto),
                "Expected login() to throw InvalidLoginCredentials exception, but it didn't.");
        // Assert
        assertThat(invalidLoginCredentials)
                .returns("Invalid login! Please check the given email id and password.", InvalidLoginCredentials::getMessage);
    }

    @Test
    @Transactional
    void test_login_withValidCredentials_returnsAuthToken() throws InvalidLoginCredentials {
        // Arrange
        User user = new User();
        user.setFullName("Scrooge McDuck");
        user.setEmail("scrooge@disney.com");
        user.setPassword("scroogyrichestduck");
        User savedUser = this.userRepo.save(user);
        UserCredDto userCredDto = new UserCredDto(user.getEmail(), user.getPassword());
        // Act
        HttpEntity<UserTokenDto> userTokenDtoHttpEntity = this.authController.login(userCredDto);
        // Assert
        assertThat(userTokenDtoHttpEntity)
                .returns(true, HttpEntity::hasBody);
        assertThat(userTokenDtoHttpEntity.getBody().getToken())
                .hasSize(20);
    }

    @Test
    @Transactional
    void test_validate_withInvalidToken_throwsException() {
        // Arrange
        User user = new User();
        user.setFullName("Scrooge McDuck");
        user.setEmail("scrooge@disney.com");
        user.setPassword("scroogyrichestduck");
        User savedUser = this.userRepo.save(user);
        String randomAuthToken = new RandomString(47, 122).ofLength(20);
        Session savedSession = this.sessionRepo.save(new Session(savedUser, randomAuthToken));
        UserTokenDto userTokenDto = new UserTokenDto(new RandomString(47, 122).ofLength(20));
        // Act
        InvalidToken invalidToken = assertThrows(InvalidToken.class,
                () -> this.authController.validate(userTokenDto),
                "Expected validate() to throw InvalidToken exception, but it didn't.");
        // Assert
        assertThat(invalidToken)
                .hasMessage("The given token is invalid or expired.");
    }

    @Test
    @Transactional
    void test_validate_withValidToken_returnsUserDetails() throws InvalidToken {
        // Arrange
        User user = new User();
        user.setFullName("Scrooge McDuck");
        user.setEmail("scrooge@disney.com");
        user.setPassword("scroogyrichestduck");
        User savedUser = this.userRepo.save(user);
        String randomAuthToken = new RandomString(47, 122).ofLength(20);
        Session savedSession = this.sessionRepo.save(new Session(savedUser, randomAuthToken));
        UserTokenDto userTokenDto = new UserTokenDto(savedSession.getToken());
        // Act
        HttpEntity<UserDto> userDtoHttpEntity = this.authController.validate(userTokenDto);
        // Assert
        assertThat(userDtoHttpEntity)
                .returns(true, HttpEntity::hasBody);
        assertThat(userDtoHttpEntity.getBody())
                .returns(savedUser.getId(), UserDto::getId)
                .returns(savedUser.getFullName(), UserDto::getFullName)
                .returns(savedUser.getEmail(), UserDto::getEmail);
    }
}