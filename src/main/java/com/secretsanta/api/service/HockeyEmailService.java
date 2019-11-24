package com.secretsanta.api.service;

import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;

public class HockeyEmailService extends BaseEmailService implements EmailService{
    
    private static final String URI = "http://www.pmshockey.com/wp-admin/admin-ajax.php?";
    
    RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public void sendEmail(String destinationAddress, String subject, String template, Context context) {
        
        // override destination address if necessary
        destinationAddress = getFinalDestinationAddress(destinationAddress);
        
        restTemplate.getForEntity(URI + "to=" + destinationAddress + "&"
                                      + "subject=" + subject + "&"
                                      + "message=" + template + "&"
                                      + "action=send_email", String.class);
        
    }
}
