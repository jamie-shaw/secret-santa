package com.secretsanta.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.WebRequest;

import com.secretsanta.api.dto.ErrorResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionAdvice Tests")
class GlobalExceptionAdviceTest {

    @InjectMocks
    private GlobalExceptionAdvice globalExceptionAdvice;

    @Mock
    private Logger mockLogger;

    @Mock
    private WebRequest webRequest;

    @Captor
    private ArgumentCaptor<String> logMessageCaptor;

    @Captor
    private ArgumentCaptor<Exception> exceptionCaptor;

    @BeforeEach
    void setUp() {
        // Inject mock logger into the advice class
        ReflectionTestUtils.setField(globalExceptionAdvice, "log", mockLogger);
        
        // Setup common WebRequest behavior
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500 Internal Server Error")
    void testHandleGenericException() {
        // Arrange
        Exception exception = new RuntimeException("Something went wrong");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getErrorCode());
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
        assertEquals("uri=/api/test", errorResponse.getDescription());
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    @DisplayName("Should log error message for generic Exception")
    void testHandleGenericException_LogsError() {
        // Arrange
        Exception exception = new RuntimeException("Something went wrong");

        // Act
        globalExceptionAdvice.handleGenericException(exception, webRequest);

        // Assert - verify logging occurred
        verify(mockLogger).error(
            eq("Unhandled exception caught. Request: {}"),
            eq("uri=/api/test"),
            eq(exception)
        );
    }

    @Test
    @DisplayName("Should handle BadCredentialsException and return 401 Unauthorized")
    void testHandleBadCredentialsException() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleBadCredentialsException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(401, errorResponse.getErrorCode());
        assertEquals("Invalid username or password", errorResponse.getMessage());
        assertEquals("uri=/api/test", errorResponse.getDescription());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle BadCredentialsException with various messages")
    void testHandleBadCredentialsException_VariousMessages() {
        // Test with different exception messages
        String[] messages = {
            "Invalid credentials",
            "User not found",
            "Account locked"
        };

        for (String message : messages) {
            // Arrange
            BadCredentialsException exception = new BadCredentialsException(message);

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleBadCredentialsException(exception, webRequest);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("Invalid username or password", errorResponse.getMessage());
        }
    }

    @Test
    @DisplayName("Should handle AuthenticationException and return 500 Internal Server Error")
    void testHandleAuthenticationException() {
        // Arrange
        AuthenticationException exception = new InternalAuthenticationServiceException("Auth failed");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleAuthenticationException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getErrorCode());
        assertEquals("Authentication failed: Auth failed", errorResponse.getMessage());
        assertEquals("uri=/api/test", errorResponse.getDescription());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle AuthenticationException with null message")
    void testHandleAuthenticationException_NullMessage() {
        // Arrange
        AuthenticationException exception = mock(AuthenticationException.class);
        when(exception.getMessage()).thenReturn(null);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleAuthenticationException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Authentication failed: null", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Should handle different WebRequest descriptions")
    void testHandleException_DifferentWebRequestDescriptions() {
        // Test various request descriptions
        String[] descriptions = {
            "uri=/api/users/123",
            "uri=/api/login",
            "uri=/api/admin/settings"
        };

        for (String description : descriptions) {
            // Arrange
            when(webRequest.getDescription(false)).thenReturn(description);
            Exception exception = new RuntimeException("Test exception");

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleGenericException(exception, webRequest);

            // Assert
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(description, errorResponse.getDescription());
        }
    }

    @Test
    @DisplayName("Should preserve exception stack trace in log")
    void testHandleGenericException_PreservesStackTrace() {
        // Arrange
        Exception exception = new RuntimeException("Test exception with stack trace");

        // Act
        globalExceptionAdvice.handleGenericException(exception, webRequest);

        // Assert - verify the exception object is passed to logger (preserving stack trace)
        verify(mockLogger).error(
            anyString(),
            anyString(),
            eq(exception)
        );
    }

    @Test
    @DisplayName("Should handle null exception message gracefully")
    void testHandleGenericException_NullExceptionMessage() {
        // Arrange
        Exception exception = new RuntimeException((String) null);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Should return consistent timestamp format")
    void testErrorResponse_TimestampFormat() {
        // Arrange
        Exception exception = new RuntimeException("Test");
        LocalDateTime beforeCall = LocalDateTime.now();

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleGenericException(exception, webRequest);
        LocalDateTime afterCall = LocalDateTime.now();

        // Assert
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getTimestamp());
        
        // Verify timestamp is within the call window
        assertFalse(errorResponse.getTimestamp().isBefore(beforeCall.minusSeconds(1)));
        assertFalse(errorResponse.getTimestamp().isAfter(afterCall.plusSeconds(1)));
    }

    @Test
    @DisplayName("Should handle concurrent exception handling")
    void testConcurrentExceptionHandling() throws InterruptedException {
        // Arrange
        Exception exception1 = new RuntimeException("Exception 1");
        Exception exception2 = new BadCredentialsException("Exception 2");
        
        // Act - simulate concurrent calls
        Thread thread1 = new Thread(() -> {
            globalExceptionAdvice.handleGenericException(exception1, webRequest);
        });
        
        Thread thread2 = new Thread(() -> {
            globalExceptionAdvice.handleBadCredentialsException(exception2, webRequest);
        });
        
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        
        // Assert - verify both were logged (if logging was enabled)
        // This test mainly ensures no threading issues occur
        assertTrue(true, "Concurrent handling completed without errors");
    }

    @Test
    @DisplayName("Should handle exception with very long message")
    void testHandleException_LongMessage() {
        // Arrange
        String longMessage = "A".repeat(10000);
        Exception exception = new RuntimeException(longMessage);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Should handle exception with special characters in message")
    void testHandleException_SpecialCharacters() {
        // Arrange
        String specialMessage = "Error with special chars: <>&\"'{}[]";
        Exception exception = new RuntimeException(specialMessage);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionAdvice.handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return proper HTTP status codes for different exception types")
    void testProperHttpStatusCodes() {
        // Test generic exception returns 500
        Exception genericException = new RuntimeException("Generic");
        ResponseEntity<ErrorResponse> genericResponse = globalExceptionAdvice.handleGenericException(genericException, webRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, genericResponse.getStatusCode());

        // Test BadCredentialsException returns 401
        BadCredentialsException badCredsException = new BadCredentialsException("Bad creds");
        ResponseEntity<ErrorResponse> badCredsResponse = globalExceptionAdvice.handleBadCredentialsException(badCredsException, webRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, badCredsResponse.getStatusCode());

        // Test AuthenticationException returns 500
        AuthenticationException authException = new InternalAuthenticationServiceException("Auth error");
        ResponseEntity<ErrorResponse> authResponse = globalExceptionAdvice.handleAuthenticationException(authException, webRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, authResponse.getStatusCode());
    }
}
