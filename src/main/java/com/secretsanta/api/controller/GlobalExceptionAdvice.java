package com.secretsanta.api.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.secretsanta.api.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, WebRequest request) {
        
        log.error("Unhandled exception caught. Request: {}", request.getDescription(false), e);
        
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request, "An unexpected error occurred");
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(Exception e, WebRequest request) {
        return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, request, "Invalid username or password");
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception e, WebRequest request) {
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request, "Authentication failed: " + e.getMessage());
    }
    
    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, HttpStatus status, WebRequest request, String message) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                message,
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
    

}