package com.secretsanta.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.support.ServletContextAttributeExporter;

import com.secretsanta.api.dao.SystemDao;
import com.secretsanta.api.model.SystemContext;

@SpringBootApplication
@ComponentScan(basePackages = "com.secretsanta.api")
public class SecretSantaApiApplication {

    @Resource
    SystemContext systemContext;
    
    @Resource
    SystemDao systemDao;
    
    public static void main(String[] args) {
        SpringApplication.run(SecretSantaApiApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
    
    @Bean
    ServletContextAttributeExporter servletContextAttributeExporter() {
        ServletContextAttributeExporter exporter = new ServletContextAttributeExporter();
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("systemContext", systemContext);
        exporter.setAttributes(attributes);
        
        return exporter;
    }
    
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        systemContext.setCurrentYear(systemDao.getCurrentYear());
    }
}
