package com.secretsanta.api.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.model.Recipient;

@ExtendWith(MockitoExtension.class)
@DisplayName("PickService Tests")
class PickServiceTest {

    @Mock
    private RecipientDao recipientDao;

    @InjectMocks
    private PickService pickService;

    private List<Recipient> allRecipients;

    @BeforeEach
    void setUp() {
        allRecipients = Arrays.asList(
            new Recipient("alice", "2026", null, false, false),
            new Recipient("bob", "2026", null, false, false),
            new Recipient("charlie", "2026", null, false, false),
            new Recipient("david", "2026", null, false, false)
        );
    }

    @Test
    @DisplayName("Should successfully pick recipients when no self-assignments occur")
    void testPickRecipientsSuccess() {
        // Arrange
        when(recipientDao.getAllRecipients()).thenReturn(allRecipients);
        
        // Mock unassigned recipients for each user
        when(recipientDao.getUnassignedRecipients("alice")).thenReturn(
            Arrays.asList(
                new Recipient("bob", "2026", null, false, false),
                new Recipient("charlie", "2026", null, false, false),
                new Recipient("david", "2026", null, false, false)
            )
        );
        when(recipientDao.getUnassignedRecipients("bob")).thenReturn(
            Arrays.asList(
                new Recipient("charlie", "2026", null, false, false),
                new Recipient("david", "2026", null, false, false)
            )
        );
        when(recipientDao.getUnassignedRecipients("charlie")).thenReturn(
            Arrays.asList(
                new Recipient("david", "2026", null, false, false)
            )
        );
        when(recipientDao.getUnassignedRecipients("david")).thenReturn(
            Arrays.asList(
                new Recipient("alice", "2026", null, false, false)
            )
        );
        
        when(recipientDao.getSelfAssignedRecipients()).thenReturn(0);

        // Act
        boolean result = pickService.pickRecipients();

        // Assert
        assertTrue(result);
        verify(recipientDao, times(1)).getAllRecipients();
        verify(recipientDao, times(4)).assignRecipient(anyString(), any(Recipient.class));
        verify(recipientDao, times(1)).getSelfAssignedRecipients();
        verify(recipientDao, never()).clearPicks();
    }

    @Test
    @DisplayName("Should return false and clear picks when self-assignments occur")
    void testPickRecipientsWithSelfAssignment() {
        // Arrange
        when(recipientDao.getAllRecipients()).thenReturn(allRecipients);
        
        when(recipientDao.getUnassignedRecipients(anyString())).thenReturn(
            Arrays.asList(new Recipient("bob", "2026", null, false, false))
        );
        
        when(recipientDao.getSelfAssignedRecipients()).thenReturn(1);

        // Act
        boolean result = pickService.pickRecipients();

        // Assert
        assertFalse(result);
        verify(recipientDao, times(1)).clearPicks();
    }

    @Test
    @DisplayName("Should handle empty recipient list")
    void testPickRecipientsWithEmptyList() {
        // Arrange
        when(recipientDao.getAllRecipients()).thenReturn(new ArrayList<>());
        when(recipientDao.getSelfAssignedRecipients()).thenReturn(0);

        // Act
        boolean result = pickService.pickRecipients();

        // Assert
        assertTrue(result);
        verify(recipientDao, times(1)).getAllRecipients();
        verify(recipientDao, never()).assignRecipient(anyString(), any(Recipient.class));
    }

    @Test
    @DisplayName("Should handle case with no unassigned recipients")
    void testPickRecipientsWithNoUnassigned() {
        // Arrange
        when(recipientDao.getAllRecipients()).thenReturn(allRecipients);
        when(recipientDao.getUnassignedRecipients(anyString())).thenReturn(new ArrayList<>());
        when(recipientDao.getSelfAssignedRecipients()).thenReturn(0);

        // Act
        boolean result = pickService.pickRecipients();

        // Assert
        assertTrue(result);
        verify(recipientDao, never()).assignRecipient(anyString(), any(Recipient.class));
    }

    @Test
    @DisplayName("Should handle two remaining recipients correctly")
    void testPickRecipientsWithTwoRemaining() {
        // Arrange
        List<Recipient> twoRecipients = Arrays.asList(
            new Recipient("alice", "2026", null, false, false),
            new Recipient("bob", "2026", null, false, false)
        );
        
        when(recipientDao.getAllRecipients()).thenReturn(twoRecipients);
        
        Recipient bobRecipient = new Recipient("bob", "2026", null, false, false);
        when(recipientDao.getUnassignedRecipients("alice")).thenReturn(
            Arrays.asList(bobRecipient)
        );
        
        Recipient aliceRecipient = new Recipient("alice", "2026", null, false, false);
        when(recipientDao.getUnassignedRecipients("bob")).thenReturn(
            Arrays.asList(aliceRecipient)
        );
        
        when(recipientDao.getSelfAssignedRecipients()).thenReturn(0);

        // Act
        boolean result = pickService.pickRecipients();

        // Assert
        assertTrue(result);
        verify(recipientDao, times(2)).assignRecipient(anyString(), any(Recipient.class));
    }

    @Test
    @DisplayName("Should handle special case where last user would get themselves")
    void testPickRecipientsAvoidLastUserSelfAssignment() {
        // Arrange
        when(recipientDao.getAllRecipients()).thenReturn(allRecipients);
        
        when(recipientDao.getUnassignedRecipients(anyString())).thenReturn(
            Arrays.asList(new Recipient("bob", "2026", null, false, false))
        );
        
        when(recipientDao.getSelfAssignedRecipients()).thenReturn(0);

        // Act
        boolean result = pickService.pickRecipients();

        // Assert
        assertTrue(result);
        verify(recipientDao, times(4)).assignRecipient(anyString(), any(Recipient.class));
    }

    @Test
    @DisplayName("Should handle more than two unassigned recipients using random selection")
    void testPickRecipientsWithMultipleUnassigned() {
        // Arrange
        when(recipientDao.getAllRecipients()).thenReturn(allRecipients);
        
        List<Recipient> manyUnassigned = Arrays.asList(
            new Recipient("bob", "2026", null, false, false),
            new Recipient("charlie", "2026", null, false, false),
            new Recipient("david", "2026", null, false, false),
            new Recipient("eve", "2026", null, false, false)
        );
        
        when(recipientDao.getUnassignedRecipients("alice")).thenReturn(manyUnassigned);
        when(recipientDao.getUnassignedRecipients("bob")).thenReturn(new ArrayList<>());
        when(recipientDao.getUnassignedRecipients("charlie")).thenReturn(new ArrayList<>());
        when(recipientDao.getUnassignedRecipients("david")).thenReturn(new ArrayList<>());
        
        when(recipientDao.getSelfAssignedRecipients()).thenReturn(0);

        // Act
        boolean result = pickService.pickRecipients();

        // Assert
        assertTrue(result);
        // Alice should get a recipient assigned (random from the 4 available)
        verify(recipientDao, times(1)).assignRecipient(eq("alice"), any(Recipient.class));
    }
}
