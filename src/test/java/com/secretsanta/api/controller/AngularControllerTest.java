package com.secretsanta.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AngularController.class)
class AngularControllerTest extends BaseControllerTest {

    @Test
    @WithMockUser
    void testAngularAppRedirect() throws Exception {
        mockMvc.perform(get("/ng"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/"));
    }

    @Test
    @WithMockUser
    void testAngularAppRedirectMultipleTimes() throws Exception {
        // Test that the redirect is consistent across multiple calls
        mockMvc.perform(get("/ng"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/"));

        mockMvc.perform(get("/ng"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/"));
    }
}
