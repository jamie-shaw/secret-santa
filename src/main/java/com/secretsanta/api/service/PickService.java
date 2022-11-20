package com.secretsanta.api.service;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.model.Recipient;

@Service
public class PickService {
    
    @Resource
    private RecipientDao recipientDao;
    
    public boolean pickRecipients() {
        
        List<Recipient> allRecipients = recipientDao.getAllRecipients();
        
        for (Recipient currentUser : allRecipients) {
            List<Recipient> recipients = recipientDao.getUnassignedRecipients(currentUser.getUserName());
            
            if (recipients.size() > 0) {
                Recipient recipient = null;
                
                if (recipients.size() > 2) {
                    // generate random value between 0 and number of records returned.
                    Random random = new Random();
                    int recordNumber = random.nextInt(recipients.size());
                    
                    // locate record corresponding to random number
                    recipient = recipients.get(recordNumber);
                    
                } else {
                    // choose the first unassigned user as the recipient
                    recipient = recipients.get(0);
                    
                    if (recipients.size() == 2) {
                        // Check to ensure that last user won't get themselves
                        Recipient lastUser = recipients.get(1);
                        if (lastUser.getRecipient() == null && !lastUser.isAssigned()) {
                            recipient = lastUser;
                        }
                    }
                }
                
                // assign the recipient to the current user
                recipientDao.assignRecipient(currentUser.getUserName(), recipient);
            }
        }
        if (recipientDao.getSelfAssignedRecipients() != 0) {
            recipientDao.clearPicks();
            return false;
        }
        
        return true;
    }
}
