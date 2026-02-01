package com.secretsanta.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.secretsanta.api.model.Recipient;

@WebMvcTest(RecipientController.class)
class RecipientControllerTest extends BaseControllerTest {

    private Recipient testRecipient;
    private List<Recipient> testRecipients;
    private List<String> testYears;

    @BeforeEach
    void setUp() {
        testRecipient = new Recipient("john", "2026", "jane", true, true);
        
        testRecipients = Arrays.asList(
            new Recipient("john", "2026", "jane", true, true),
            new Recipient("jane", "2026", "bob", true, false),
            new Recipient("bob", "2026", "john", true, true)
        );
        
        testYears = Arrays.asList("2026", "2025", "2024");
    }

    @Test
    @WithMockUser(username = "john")
    void testGetRecipient() throws Exception {
        when(recipientDao.getRecipientForCurrentUser("john")).thenReturn(testRecipient);

        mockMvc.perform(get("/api/recipient")
                .sessionAttr("CURRENT_USER", "john"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName", is("john")))
                .andExpect(jsonPath("$.recipient", is("jane")))
                .andExpect(jsonPath("$.year", is("2026")))
                .andExpect(jsonPath("$.assigned", is(true)))
                .andExpect(jsonPath("$.viewed", is(true)));

        verify(recipientDao, times(1)).getRecipientForCurrentUser("john");
    }

    @Test
    @WithMockUser(username = "john")
    void testShowPickStatus() throws Exception {
        when(recipientDao.getAllRecipients()).thenReturn(testRecipients);

        mockMvc.perform(get("/api/pick/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userName", is("john")))
                .andExpect(jsonPath("$[1].userName", is("jane")))
                .andExpect(jsonPath("$[2].userName", is("bob")));

        verify(recipientDao, times(1)).getAllRecipients();
    }

    @Test
    @WithMockUser(username = "john")
    void testGetAvailableYears() throws Exception {
        when(recipientDao.getActiveYears()).thenReturn(testYears);

        mockMvc.perform(get("/api/history/years"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("2026")))
                .andExpect(jsonPath("$[1]", is("2025")))
                .andExpect(jsonPath("$[2]", is("2024")));

        verify(recipientDao, times(1)).getActiveYears();
    }

    @Test
    @WithMockUser(username = "john")
    void testGetPickHistory() throws Exception {
        when(recipientDao.getAllRecipientsForSelectedYear(2025)).thenReturn(testRecipients);

        mockMvc.perform(get("/api/history/2025"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userName", is("john")));

        verify(recipientDao, times(1)).getAllRecipientsForSelectedYear(2025);
    }

    @Test
    @WithMockUser(username = "john")
    void testGetPickHistoryForDifferentYear() throws Exception {
        List<Recipient> historicalRecipients = Arrays.asList(
            new Recipient("john", "2024", "alice", true, true),
            new Recipient("jane", "2024", "john", true, true)
        );
        
        when(recipientDao.getAllRecipientsForSelectedYear(2024)).thenReturn(historicalRecipients);

        mockMvc.perform(get("/api/history/2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].year", is("2024")));

        verify(recipientDao, times(1)).getAllRecipientsForSelectedYear(2024);
    }
}
