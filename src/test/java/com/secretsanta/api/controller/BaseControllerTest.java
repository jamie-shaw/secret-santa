package com.secretsanta.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secretsanta.api.dao.GiftDao;
import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.dao.SystemDao;
import com.secretsanta.api.dao.UserDao;
import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;
import com.secretsanta.api.security.JwtTokenProvider;
import com.secretsanta.api.service.EmailService;
import com.secretsanta.api.service.PickService;

@WebMvcTest(controllers = {
        AdminController.class,
        AngularController.class,
        ApiController.class,
        AuthController.class,
        EmailController.class,
        GiftController.class,
        RecipientController.class,
        SantaController.class
    })
public abstract class BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RecipientDao recipientDao;

    @MockitoBean
    UserDao userDao;
    
    @MockitoBean
    GiftDao giftDao;

    @MockitoBean
    SystemDao systemDao;

    @MockitoBean
    PickService pickService;

    @MockitoBean
    SystemContext systemContext;

    @MockitoBean
    SessionContext sessionContext;

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    EmailService emailService;

    @MockitoBean
    JdbcTemplate jdbcTemplate;

    @MockitoBean
    UserDetailsService userDetailsService;

    @MockitoBean
    AuthenticationProvider authenticationProvider;

    @MockitoBean
    AuthenticationManager authenticationManager;

    @MockitoBean
    JwtTokenProvider tokenProvider;
}
