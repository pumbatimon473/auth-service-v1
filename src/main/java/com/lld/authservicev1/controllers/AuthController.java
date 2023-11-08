package com.lld.authservicev1.controllers;

import com.lld.authservicev1.dtos.ErrorDto;
import com.lld.authservicev1.dtos.UserCredDto;
import com.lld.authservicev1.dtos.UserDto;
import com.lld.authservicev1.dtos.UserTokenDto;
import com.lld.authservicev1.exceptions.InvalidLoginCredentials;
import com.lld.authservicev1.exceptions.InvalidToken;
import com.lld.authservicev1.models.User;
import com.lld.authservicev1.services.AuthService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth-service")
public class AuthController {
    // Fields
    private AuthService authService;

    // CTOR
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Behaviors
    @PostMapping("/login")
    public HttpEntity<UserTokenDto> login(@RequestBody UserCredDto userCredDto) throws InvalidLoginCredentials {
        HttpEntity<UserTokenDto> responseEntity = null;
        try {
            String authToken = this.authService.loginByEmail(userCredDto.getEmail(), userCredDto.getPassword());
            if (authToken == null)
                throw new InvalidLoginCredentials("Invalid login! Please check the given email id and password.");
            responseEntity = new ResponseEntity<>(new UserTokenDto(authToken), HttpStatus.OK);
        } catch (InvalidLoginCredentials e) {
            throw e;
        } catch (Exception e) {
            throw new UnknownError(e.getClass() + ": " + e.getMessage());
        }
        return responseEntity;
    }

    @ExceptionHandler(InvalidLoginCredentials.class)
    public HttpEntity<ErrorDto> handleInvalidLoginCredentialsException(InvalidLoginCredentials e) {
        return new ResponseEntity<>(new ErrorDto(101, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/validate")
    public HttpEntity<UserDto> validate(@RequestBody UserTokenDto userTokenDto) throws InvalidToken {
        HttpEntity<UserDto> responseEntity = null;
        try {
            Optional<User> userOptional = this.authService.validate(userTokenDto.getToken());
            if (userOptional.isEmpty()) {
                throw new InvalidToken("The given token is invalid or expired.");
            }
            User user = userOptional.get();
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFullName(user.getFullName());
            userDto.setEmail(user.getEmail());
            responseEntity = new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (InvalidToken e) {
            throw e;
        } catch (Exception e) {
            throw new UnknownError(e.getClass() + ": " + e.getMessage());
        }
        return responseEntity;
    }

    @ExceptionHandler(InvalidToken.class)
    public HttpEntity<ErrorDto> handleInvalidTokenException(InvalidToken e) {
        return new ResponseEntity<>(new ErrorDto(102, e.getMessage()), HttpStatus.FORBIDDEN);
    }
}
