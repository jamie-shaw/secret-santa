package com.secretsanta.api.service;

import org.springframework.web.client.RestTemplate;

public class HockeyEmailService extends BaseEmailService implements EmailService{
    
    private static final String URI = "http://www.pmshockey.com/wp-admin/admin-ajax.php?";
    
    RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public void sendEmail(String destinationAddress, String subject, String message) {
        
        // override destination address if necessary
        destinationAddress = getDestinationAddress(destinationAddress);
        
        restTemplate.getForEntity(URI + "to=" + destinationAddress + "&"
                                      + "subject=" + subject + "&"
                                      + "message=" + message + "&"
                                      + "action=send_email", String.class);
        
    }
}
