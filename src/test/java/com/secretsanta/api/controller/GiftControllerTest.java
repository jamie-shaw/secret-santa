package com.secretsanta.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.thymeleaf.context.Context;

import com.secretsanta.api.dao.UserDao.FilterColumn;
import com.secretsanta.api.model.Gift;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.User;

class GiftControllerTest extends BaseControllerTest {

    private Gift testGift;
    private List<Gift> testGifts;
    private User testUser;
    private Recipient testRecipient;

    @BeforeEach
    void setUp() {
        testGift = new Gift("1", "john", "A nice watch", "http://example.com/watch", "2026");
        
        testGifts = Arrays.asList(
            new Gift("1", "john", "A nice watch", "http://example.com/watch", "2026"),
            new Gift("2", "john", "A book", "http://example.com/book", "2026")
        );
        
        testUser = new User("john");
        testUser.setEmail("john@example.com");
        testUser.setDisplayName("John Doe");
        
        testRecipient = new Recipient("john", "2026", "jane", true, true);
    }

    @Test
    @WithMockUser(username = "john")
    void testGetIdeasForSanta() throws Exception {
        when(giftDao.getIdeasForSanta("john")).thenReturn(testGifts);

        mockMvc.perform(get("/api/gift/summary")
                .sessionAttr("CURRENT_USER", "john"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("A nice watch")))
                .andExpect(jsonPath("$[1].description", is("A book")));

        verify(giftDao, times(1)).getIdeasForSanta("john");
    }

    @Test
    @WithMockUser(username = "john")
    void testGetIdeasFromRecipient() throws Exception {
        when(giftDao.getIdeasFromSanta("jane")).thenReturn(testGifts);

        mockMvc.perform(get("/api/idea/summary")
                .sessionAttr("RECIPIENT", testRecipient))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(giftDao, times(1)).getIdeasFromSanta("jane");
    }

    @Test
    @WithMockUser(username = "john")
    void testShowGiftDetail() throws Exception {
        when(giftDao.getGiftDetail(1)).thenReturn(testGift);

        mockMvc.perform(get("/api/gift/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.description", is("A nice watch")))
                .andExpect(jsonPath("$.link", is("http://example.com/watch")))
                .andExpect(jsonPath("$.userName", is("john")));

        verify(giftDao, times(1)).getGiftDetail(1);
    }

    @Test
    @WithMockUser(username = "john")
    void testCreateGift() throws Exception {
        Gift newGift = new Gift(null, "john", "New gift idea", "http://example.com/newgift", "2026");
        
        when(userDao.getUser("john", FilterColumn.RECIPIENT)).thenReturn(testUser);
        doNothing().when(giftDao).createGift(anyString(), anyString(), anyString());
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), any(Context.class));

        mockMvc.perform(post("/api/gift")
                .sessionAttr("CURRENT_USER", "john")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newGift)))
                .andExpect(status().isOk());

        verify(giftDao, times(1)).createGift("john", "New gift idea", "http://example.com/newgift");
        verify(userDao, times(1)).getUser("john", FilterColumn.RECIPIENT);
        verify(emailService, times(1)).sendEmail(
            eq("john@example.com"), 
            eq("Your Secret Santa recipient just gave you an idea"),
            eq("createGiftTemplate.html"),
            any(Context.class)
        );
    }

    @Test
    @WithMockUser(username = "john")
    void testUpdateGift() throws Exception {
        Gift updatedGift = new Gift("1", "john", "Updated description", "http://example.com/updated", "2026");
        
        when(userDao.getUser("john", FilterColumn.RECIPIENT)).thenReturn(testUser);
        doNothing().when(giftDao).updateGift(anyInt(), anyString(), anyString());
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), any(Context.class));

        mockMvc.perform(put("/api/gift/1")
                .sessionAttr("CURRENT_USER", "john")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedGift)))
                .andExpect(status().isOk());

        verify(giftDao, times(1)).updateGift(1, "Updated description", "http://example.com/updated");
        verify(userDao, times(1)).getUser("john", FilterColumn.RECIPIENT);
        verify(emailService, times(1)).sendEmail(
            eq("john@example.com"), 
            eq("Your Secret Santa recipient just updated an idea"),
            eq("updateGiftTemplate.html"),
            any(Context.class)
        );
    }

    @Test
    @WithMockUser(username = "john")
    void testDeleteGift() throws Exception {
        when(giftDao.getGiftDetail(1)).thenReturn(testGift);
        when(userDao.getUser("john", FilterColumn.RECIPIENT)).thenReturn(testUser);
        doNothing().when(giftDao).deleteGift(anyInt());
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), any(Context.class));

        mockMvc.perform(delete("/api/gift/1")
                .sessionAttr("CURRENT_USER", "john"))
                .andExpect(status().isOk());

        verify(giftDao, times(1)).getGiftDetail(1);
        verify(giftDao, times(1)).deleteGift(1);
        verify(userDao, times(1)).getUser("john", FilterColumn.RECIPIENT);
        verify(emailService, times(1)).sendEmail(
            eq("john@example.com"), 
            eq("Your Secret Santa recipient just deleted a suggestion"),
            eq("deleteGiftTemplate.html"),
            any(Context.class)
        );
    }

    @Test
    @WithMockUser(username = "john")
    void testGetIdeasForSantaEmptyList() throws Exception {
        when(giftDao.getIdeasForSanta("john")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/gift/summary")
                .sessionAttr("CURRENT_USER", "john"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(giftDao, times(1)).getIdeasForSanta("john");
    }
}
