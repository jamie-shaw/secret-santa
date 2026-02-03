package com.secretsanta.api.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

@DisplayName("StubEmailService Tests")
class StubEmailServiceTest {

    private StubEmailService stubEmailService;

    @BeforeEach
    void setUp() {
        stubEmailService = new StubEmailService();
    }

    @Test
    @DisplayName("Should execute sendEmail without throwing exception")
    void testSendEmail_NoException() {
        // Arrange
        String address = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate.html";
        Context context = new Context();

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> 
            stubEmailService.sendEmail(address, subject, templateName, context)
        );
    }

    @Test
    @DisplayName("Should handle null address")
    void testSendEmail_NullAddress() {
        // Arrange
        String subject = "Test Subject";
        String templateName = "testTemplate.html";
        Context context = new Context();

        // Act & Assert
        assertDoesNotThrow(() -> 
            stubEmailService.sendEmail(null, subject, templateName, context)
        );
    }

    @Test
    @DisplayName("Should handle null subject")
    void testSendEmail_NullSubject() {
        // Arrange
        String address = "test@example.com";
        String templateName = "testTemplate.html";
        Context context = new Context();

        // Act & Assert
        assertDoesNotThrow(() -> 
            stubEmailService.sendEmail(address, null, templateName, context)
        );
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testSendEmail_EmptyStrings() {
        // Arrange
        Context context = new Context();

        // Act & Assert
        assertDoesNotThrow(() -> 
            stubEmailService.sendEmail("", "", "", context)
        );
    }

    @Test
    @DisplayName("Should handle null context")
    void testSendEmail_NullContext() {
        // Arrange
        String address = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate.html";

        // Act & Assert
        assertDoesNotThrow(() -> 
            stubEmailService.sendEmail(address, subject, templateName, null)
        );
    }

    @Test
    @DisplayName("Should handle multiple calls")
    void testSendEmail_MultipleCalls() {
        // Arrange
        Context context = new Context();

        // Act & Assert
        assertDoesNotThrow(() -> {
            stubEmailService.sendEmail("user1@example.com", "Subject 1", "template1.html", context);
            stubEmailService.sendEmail("user2@example.com", "Subject 2", "template2.html", context);
            stubEmailService.sendEmail("user3@example.com", "Subject 3", "template3.html", context);
        });
    }

    @Test
    @DisplayName("Should be usable as EmailService implementation")
    void testStubEmailService_ImplementsInterface() {
        // Assert
        assertTrue(stubEmailService instanceof EmailService);
    }
}
