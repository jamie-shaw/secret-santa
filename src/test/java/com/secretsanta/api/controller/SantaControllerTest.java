package com.secretsanta.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.secretsanta.api.model.User;

class SantaControllerTest extends BaseControllerTest {

    private User testUser;
    private List<User> testUsers;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User("john");
        testUser.setEmail("john@example.com");
        testUser.setDisplayName("John Doe");

        User jane = new User("jane");
        jane.setEmail("jane@example.com");
        jane.setDisplayName("Jane Smith");

        User bob = new User("bob");
        bob.setEmail("bob@example.com");
        bob.setDisplayName("Bob Johnson");

        testUsers = Arrays.asList(testUser, jane, bob);

        authentication = mock(Authentication.class);
    }

    @Test
    @WithMockUser(username = "john")
    void testShowPasswordChange() throws Exception {
        mockMvc.perform(get("/changePassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(request().sessionAttribute("ERROR_MESSAGE", "Please enter a new password"));
    }

    @Test
    @WithMockUser(username = "john")
    void testShowPasswordChangeWithExistingMessage() throws Exception {
        mockMvc.perform(get("/changePassword")
                .sessionAttr("ERROR_MESSAGE", "Existing error"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(request().sessionAttribute("ERROR_MESSAGE", "Existing error"));
    }

    @Test
    @WithMockUser(username = "john")
    void testProcessPasswordChangeSuccess() throws Exception {
        when(userDao.getUser("john")).thenReturn(testUser);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        doNothing().when(userDao).changePassword(anyString(), anyString());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("password", "newPassword123");
        params.add("confirmPassword", "newPassword123");

        mockMvc.perform(post("/changePassword")
                .sessionAttr("username", "john")
                .params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userDao, times(1)).changePassword("john", "newPassword123");
        verify(authenticationProvider, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @WithMockUser(username = "john")
    void testProcessPasswordChangeWithBlankPassword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("password", "");
        params.add("confirmPassword", "");

        mockMvc.perform(post("/changePassword")
                .sessionAttr("username", "john")
                .params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/changePassword"))
                .andExpect(request().sessionAttribute("ERROR_MESSAGE", "Password must be entered."));

        verify(userDao, never()).changePassword(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "john")
    void testProcessPasswordChangeWithMismatchedPasswords() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("password", "password123");
        params.add("confirmPassword", "differentPassword");

        mockMvc.perform(post("/changePassword")
                .sessionAttr("username", "john")
                .params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/changePassword"))
                .andExpect(request().sessionAttribute("ERROR_MESSAGE", "Passwords must match."));

        verify(userDao, never()).changePassword(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "john")
    void testProcessPasswordChangeWithSantaPassword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("password", "santa");
        params.add("confirmPassword", "santa");

        mockMvc.perform(post("/changePassword")
                .sessionAttr("username", "john")
                .params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/changePassword"))
                .andExpect(request().sessionAttribute("ERROR_MESSAGE", "Password can't be 'santa'."));

        verify(userDao, never()).changePassword(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowResetPassword() throws Exception {
        when(userDao.getAllUsers()).thenReturn(testUsers);

        mockMvc.perform(get("/resetPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attribute("USERS", testUsers));

        verify(userDao, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testProcessResetPassword() throws Exception {
        doNothing().when(userDao).resetPasswords(anyList());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "john");
        params.add("username", "jane");
        params.add("username", "bob");

        mockMvc.perform(post("/resetPassword")
                .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

        verify(userDao, times(1)).resetPasswords(Arrays.asList("john", "jane", "bob"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testProcessResetPasswordSingleUser() throws Exception {
        doNothing().when(userDao).resetPasswords(anyList());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "john");

        mockMvc.perform(post("/resetPassword")
                .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

        verify(userDao, times(1)).resetPasswords(Arrays.asList("john"));
    }

    @Test
    @WithMockUser(username = "jane")
    void testProcessPasswordChangeForDifferentUser() throws Exception {
        User janeUser = new User("jane");
        janeUser.setEmail("jane@example.com");
        
        when(userDao.getUser("jane")).thenReturn(janeUser);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        doNothing().when(userDao).changePassword(anyString(), anyString());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("password", "janePassword456");
        params.add("confirmPassword", "janePassword456");

        mockMvc.perform(post("/changePassword")
                .sessionAttr("username", "jane")
                .params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userDao, times(1)).changePassword("jane", "janePassword456");
    }
}
