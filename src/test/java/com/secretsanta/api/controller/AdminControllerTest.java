package com.secretsanta.api.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

class AdminControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUp() {
        when(sessionContext.getSchema()).thenReturn("shaw");
        doNothing().when(sessionContext).setSchema(anyString());
        doNothing().when(systemContext).setCurrentYear(anyInt());
        doNothing().when(systemDao).setCurrentYear(anyInt());
        doNothing().when(userDao).resetAllPasswords();
        doNothing().when(recipientDao).resetAllRecipients();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRollSantaOver() throws Exception {
        when(pickService.pickRecipients()).thenReturn(true);

        mockMvc.perform(get("/rollOver"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));

        verify(systemDao, times(1)).setCurrentYear(anyInt());
        verify(userDao, times(2)).resetAllPasswords(); // once for each schema
        verify(recipientDao, times(2)).resetAllRecipients(); // once for each schema
        verify(pickService, times(2)).pickRecipients(); // once for each schema
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRollSantaOverWithPickRetries() throws Exception {
        // Simulate pickRecipients failing twice then succeeding for each schema
        when(pickService.pickRecipients())
            .thenReturn(false)
            .thenReturn(false)
            .thenReturn(true)
            .thenReturn(false)
            .thenReturn(true);

        mockMvc.perform(get("/rollOver"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));

        // Should call pickRecipients 5 times total (3 for shaw, 2 for fernald)
        verify(pickService, times(5)).pickRecipients();
        verify(userDao, times(2)).resetAllPasswords();
        verify(recipientDao, times(2)).resetAllRecipients();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRollSantaOverResetsToOriginalSchema() throws Exception {
        when(sessionContext.getSchema()).thenReturn("fernald");
        when(pickService.pickRecipients()).thenReturn(true);

        mockMvc.perform(get("/rollOver"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));

        // Verify that the schema is set back to the original
        verify(sessionContext, atLeastOnce()).setSchema("fernald");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSetSuccessMessage() throws Exception {
        when(pickService.pickRecipients()).thenReturn(true);

        mockMvc.perform(get("/rollOver"))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("SUCCESS_MESSAGE", "Rollover complete."));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRollSantaOverSetsCurrentYear() throws Exception {
        when(pickService.pickRecipients()).thenReturn(true);

        mockMvc.perform(get("/rollOver"))
                .andExpect(status().isOk());

        // Verify that the current year is set (will be 2026 based on the test setup)
        verify(systemDao, times(1)).setCurrentYear(2026);
        verify(systemContext, times(1)).setCurrentYear(2026);
    }
}
