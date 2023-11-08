package com.lld.authservicev1.controllers;

import com.lld.authservicev1.dtos.ErrorDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvices {
    @ExceptionHandler(UnknownError.class)
    HttpEntity<ErrorDto> handleUnknownExceptions(Exception e) {
        return new ResponseEntity<>(new ErrorDto(420, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
