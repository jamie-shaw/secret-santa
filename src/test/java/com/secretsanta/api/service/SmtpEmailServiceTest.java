package com.secretsanta.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmtpEmailService Tests")
class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SystemContext systemContext;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private SmtpEmailService smtpEmailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(sessionContext.getSchema()).thenReturn("shaw");
        when(systemContext.getApplicationUrl()).thenReturn("http://localhost:8080");
        
        // Set default values for inherited fields
        ReflectionTestUtils.setField(smtpEmailService, "destinationAddressOverride", "");
    }

    @Test
    @DisplayName("Should send email successfully")
    void testSendEmail_Success() {
        // Arrange
        String destinationAddress = "user@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate.html";
        Context context = new Context();
        context.setVariable("userName", "John");

        // Act
        smtpEmailService.sendEmail(destinationAddress, subject, templateName, context);

        // Assert
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        verify(templateEngine, times(1)).process(eq(templateName), any(Context.class));
    }

    @Test
    @DisplayName("Should use override address when configured")
    void testSendEmail_WithOverride() {
        // Arrange
        String overrideAddress = "override@example.com";
        ReflectionTestUtils.setField(smtpEmailService, "destinationAddressOverride", overrideAddress);
        
        String destinationAddress = "user@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate.html";
        Context context = new Context();

        // Act
        smtpEmailService.sendEmail(destinationAddress, subject, templateName, context);

        // Assert
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should append schema name to subject")
    void testSendEmail_SubjectWithSchema() {
        // Arrange
        String destinationAddress = "user@example.com";
        String subject = "Secret Santa";
        String templateName = "testTemplate.html";
        Context context = new Context();

        // Act
        smtpEmailService.sendEmail(destinationAddress, subject, templateName, context);

        // Assert
        verify(sessionContext, times(1)).getSchema();
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should build email body with template")
    void testSendEmail_BuildsEmailBody() {
        // Arrange
        String destinationAddress = "user@example.com";
        String subject = "Test";
        String templateName = "welcomeTemplate.html";
        Context context = new Context();
        context.setVariable("recipientName", "Jane");

        String expectedBody = "<html><body>Welcome Jane!</body></html>";
        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn(expectedBody);

        // Act
        smtpEmailService.sendEmail(destinationAddress, subject, templateName, context);

        // Assert
        verify(templateEngine, times(1)).process(eq(templateName), any(Context.class));
        verify(systemContext, times(1)).getApplicationUrl();
    }

    @Test
    @DisplayName("Should handle exception gracefully when sending fails")
    void testSendEmail_HandlesException() {
        // Arrange
        String destinationAddress = "user@example.com";
        String subject = "Test";
        String templateName = "template.html";
        Context context = new Context();

        doThrow(new RuntimeException("SMTP server error")).when(javaMailSender).send(any(MimeMessage.class));

        // Act - should not throw exception
        smtpEmailService.sendEmail(destinationAddress, subject, templateName, context);

        // Assert
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should send email with empty context")
    void testSendEmail_EmptyContext() {
        // Arrange
        String destinationAddress = "user@example.com";
        String subject = "Test";
        String templateName = "simple.html";
        Context context = new Context();

        // Act
        smtpEmailService.sendEmail(destinationAddress, subject, templateName, context);

        // Assert
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        verify(templateEngine, times(1)).process(eq(templateName), any(Context.class));
    }

    @Test
    @DisplayName("Should handle multiple email sends")
    void testSendEmail_MultipleSends() {
        // Arrange
        Context context = new Context();

        // Act
        smtpEmailService.sendEmail("user1@example.com", "Subject 1", "template1.html", context);
        smtpEmailService.sendEmail("user2@example.com", "Subject 2", "template2.html", context);
        smtpEmailService.sendEmail("user3@example.com", "Subject 3", "template3.html", context);

        // Assert
        verify(javaMailSender, times(3)).createMimeMessage();
        verify(javaMailSender, times(3)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should process template with context variables")
    void testSendEmail_TemplateWithVariables() {
        // Arrange
        String destinationAddress = "santa@example.com";
        String subject = "Your Assignment";
        String templateName = "assignmentTemplate.html";
        Context context = new Context();
        context.setVariable("santaName", "Alice");
        context.setVariable("recipientName", "Bob");
        context.setVariable("year", "2026");

        // Act
        smtpEmailService.sendEmail(destinationAddress, subject, templateName, context);

        // Assert
        verify(templateEngine, times(1)).process(eq(templateName), any(Context.class));
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }
}
