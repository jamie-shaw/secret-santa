package com.secretsanta.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;

@ExtendWith(MockitoExtension.class)
@DisplayName("BaseEmailService Tests")
class BaseEmailServiceTest {

    @Mock
    private SystemContext systemContext;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private BaseEmailService baseEmailService;


    @Test
    @DisplayName("Should return destination address when override is blank")
    void testGetFinalDestinationAddress_NoOverride() {
        // Arrange
        ReflectionTestUtils.setField(baseEmailService, "destinationAddressOverride", "");
        String originalAddress = "user@example.com";

        // Act
        String result = baseEmailService.getFinalDestinationAddress(originalAddress);

        // Assert
        assertEquals(originalAddress, result);
    }

    @Test
    @DisplayName("Should return override address when override is set")
    void testGetFinalDestinationAddress_WithOverride() {
        // Arrange
        String overrideAddress = "override@example.com";
        ReflectionTestUtils.setField(baseEmailService, "destinationAddressOverride", overrideAddress);
        String originalAddress = "user@example.com";
        
        // Act
        String result = baseEmailService.getFinalDestinationAddress(originalAddress);

        // Assert
        assertEquals(overrideAddress, result);
    }

    @Test
    @DisplayName("Should return destination address when override is null")
    void testGetFinalDestinationAddress_NullOverride() {
        // Arrange
        ReflectionTestUtils.setField(baseEmailService, "destinationAddressOverride", null);
        String originalAddress = "user@example.com";

        // Act
        String result = baseEmailService.getFinalDestinationAddress(originalAddress);

        // Assert
        assertEquals(originalAddress, result);
    }

    @Test
    @DisplayName("Should append schema name to subject")
    void testGetFinalSubject() {
        // Arrange
        when(sessionContext.getSchema()).thenReturn("shaw");
        String originalSubject = "Secret Santa Assignment";

        // Act
        String result = baseEmailService.getFinalSubject(originalSubject);

        // Assert
        assertEquals("Secret Santa Assignment (Shaw Edition)", result);
        verify(sessionContext, times(1)).getSchema();
    }

    @Test
    @DisplayName("Should capitalize schema name in subject")
    void testGetFinalSubject_LowercaseSchema() {
        // Arrange
        when(sessionContext.getSchema()).thenReturn("smith");
        String originalSubject = "Test Subject";

        // Act
        String result = baseEmailService.getFinalSubject(originalSubject);

        // Assert
        assertEquals("Test Subject (Smith Edition)", result);
    }

    @Test
    @DisplayName("Should build email body with template and context")
    void testBuildEmailBody() {
        // Arrange
        when(systemContext.getApplicationUrl()).thenReturn("http://localhost:8080");
        
        String templateName = "testTemplate.html";
        Context templateContext = new Context();
        templateContext.setVariable("testVar", "testValue");
        
        String expectedBody = "<html><body>Test Email</body></html>";
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(expectedBody);

        // Act
        String result = baseEmailService.buildEmailBody(templateName, templateContext);

        // Assert
        assertEquals(expectedBody, result);
        verify(templateEngine, times(1)).process(eq(templateName), any(Context.class));
        verify(systemContext, times(1)).getApplicationUrl();
        
        // Verify applicationUrl was added to context
        assertEquals("http://localhost:8080", templateContext.getVariable("applicationUrl"));
    }

    @Test
    @DisplayName("Should add applicationUrl to existing context variables")
    void testBuildEmailBody_PreservesExistingVariables() {
        // Arrange
        when(systemContext.getApplicationUrl()).thenReturn("http://localhost:8080");
        
        String templateName = "welcomeTemplate.html";
        Context templateContext = new Context();
        templateContext.setVariable("userName", "John Doe");
        templateContext.setVariable("recipient", "Jane Doe");
        
        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn("email body");

        // Act
        baseEmailService.buildEmailBody(templateName, templateContext);

        // Assert
        assertEquals("John Doe", templateContext.getVariable("userName"));
        assertEquals("Jane Doe", templateContext.getVariable("recipient"));
        assertEquals("http://localhost:8080", templateContext.getVariable("applicationUrl"));
    }

    @Test
    @DisplayName("Should handle empty subject gracefully")
    void testGetFinalSubject_EmptySubject() {
        // Arrange
        when(sessionContext.getSchema()).thenReturn("shaw");
        String emptySubject = "";

        // Act
        String result = baseEmailService.getFinalSubject(emptySubject);

        // Assert
        assertEquals(" (Shaw Edition)", result);
    }

    @Test
    @DisplayName("Should handle null applicationUrl")
    void testBuildEmailBody_NullApplicationUrl() {
        // Arrange
        when(systemContext.getApplicationUrl()).thenReturn(null);
        String templateName = "test.html";
        Context templateContext = new Context();
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("body");

        // Act
        baseEmailService.buildEmailBody(templateName, templateContext);

        // Assert
        assertNull(templateContext.getVariable("applicationUrl"));
    }
}
