package com.secretsanta.api.service;

import org.thymeleaf.context.Context;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StubEmailService implements EmailService{
    
    @Override
    public void sendEmail(String address, String subject, String templateName, Context context) {
        
        log.debug(address + ", " + subject);
        
    }

}
