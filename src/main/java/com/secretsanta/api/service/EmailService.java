package com.secretsanta.api.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmailService {
    
    private static final String URI = "http://www.pmshockey.com/wp-admin/admin-ajax.php?";
    
    RestTemplate restTemplate = new RestTemplate();
    
    public void sendEmail(String address, String subject, String message) {
        
        restTemplate.getForEntity(URI + "to=" + address + "&"
                                      + "subject=" + subject + "&"
                                      + "message=" + message + "&"
                                      + "action=send_email", String.class);
    }
}
