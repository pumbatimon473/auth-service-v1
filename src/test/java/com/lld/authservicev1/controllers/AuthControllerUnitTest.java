package com.lld.authservicev1.controllers;

import com.lld.authservicev1.dtos.UserCredDto;
import com.lld.authservicev1.dtos.UserDto;
import com.lld.authservicev1.dtos.UserTokenDto;
import com.lld.authservicev1.exceptions.InvalidLoginCredentials;
import com.lld.authservicev1.exceptions.InvalidToken;
import com.lld.authservicev1.models.User;
import com.lld.authservicev1.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class AuthControllerUnitTest {
    // Fields
    @Autowired
    private AuthController authController;
    @MockBean
    private AuthService authServiceSingleSession;

    @Test
    void testLoginWIthInvalidCredentialsThrowsException() {
        // Arrange
        when(this.authServiceSingleSession.loginByEmail("timon@disney.com", "hakunamatata!"))
                .thenReturn(null);
        // Act
        InvalidLoginCredentials invalidLoginCredentials = assertThrows(InvalidLoginCredentials.class,
                () -> this.authController.login(new UserCredDto("timon@disney.com", "hakunamatata!")),
                "Expected login() to throw InvalidLoginCredentials exception but it didn't!");
        // Assert
        assertThat(invalidLoginCredentials)
                .returns("Invalid login! Please check the given email id and password.", InvalidLoginCredentials::getMessage);
    }

    @Test
    void testLoginWithValidCredentialsReturnsAuthToken() throws InvalidLoginCredentials {
        // Arrange
        when(this.authServiceSingleSession.loginByEmail("pumba@disney.com", "hakunamatata!"))
                .thenReturn("avcdfgchtyokjvgfcght");
        // Act
        HttpEntity<UserTokenDto> userTokenDtoHttpEntity = this.authController.login(new UserCredDto("pumba@disney.com", "hakunamatata!"));
        // Assert
        assertThat(userTokenDtoHttpEntity)
                .returns(true, HttpEntity::hasBody);
        assertThat(userTokenDtoHttpEntity.getBody())
                .returns("avcdfgchtyokjvgfcght", UserTokenDto::getToken);
    }

    @Test
    void testValidateWithInvalidTokenThrowsException() {
        // Arrange
        String invalidToken = "7yhdreboh";
        UserTokenDto userTokenDto = new UserTokenDto(invalidToken);
        // Act
        InvalidToken invalidTokenException = assertThrows(InvalidToken.class,
                () -> this.authController.validate(userTokenDto),
                "Expected validate() to throw InvalidToken exception, but it didn't!");
        // Assert
        assertThat(invalidTokenException)
                .returns("The given token is invalid or expired.", InvalidToken::getMessage);
    }

    @Test
    void testValidateWithValidTokenReturnsUser() throws InvalidToken {
        // Arrange
        String validToken = "thehchehchuhdfvcbnmk";
        UserTokenDto userTokenDto = new UserTokenDto(validToken);
        User user = new User();
        user.setId(7L);
        user.setFullName("Mira Nova");
        user.setEmail("miranova@disney.com");
        when(this.authServiceSingleSession.validate(userTokenDto.getToken()))
                .thenReturn(Optional.of(user));
        // Act
        HttpEntity<UserDto> userDtoHttpEntity = this.authController.validate(userTokenDto);
        // Assert
        assertThat(userDtoHttpEntity)
                .returns(true, HttpEntity::hasBody);
        assertThat(userDtoHttpEntity.getBody())
                .returns(7L, UserDto::getId)
                .returns("Mira Nova", UserDto::getFullName)
                .returns("miranova@disney.com", UserDto::getEmail);
    }
}