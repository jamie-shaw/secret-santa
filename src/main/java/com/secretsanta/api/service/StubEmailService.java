package com.secretsanta.api.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StubEmailService implements EmailService{
    
    @Override
    public void sendEmail(String address, String subject, String message) {
        
        log.debug(address + ", " + subject);
        
    }

}
