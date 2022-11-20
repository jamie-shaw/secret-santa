package com.secretsanta.api.service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SmtpEmailService extends BaseEmailService implements EmailService {
    
    @Resource
    private JavaMailSender javaMailSender;
    
    RestTemplate restTemplate = new RestTemplate();

    /**
     * Sends email using well-known Spring props.  Username and password come from JVM args or 
     * Heroku config values
     */
    public void sendEmail(String destinationAddress, String subject, String templateName, Context templateContext) {
        
        // override destination address if necessary
        destinationAddress = getFinalDestinationAddress(destinationAddress);
        
        // override the subject
        subject = getFinalSubject(subject);
        
        // build the content
        String message = buildEmailBody(templateName, templateContext);
        
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            
            helper.setFrom("Secret Santa <jamie.e.shaw@gmail.com>");
            helper.setTo(destinationAddress);
            helper.setText(message, true);
            helper.setSubject(subject);
            
            javaMailSender.send(msg);
            
            log.info("Mail Sent Successfully...");
        }
 
        // Catch block to handle the exceptions
        catch (Exception e) {
            log.error("SMTP error", e);
        }
    }

}
