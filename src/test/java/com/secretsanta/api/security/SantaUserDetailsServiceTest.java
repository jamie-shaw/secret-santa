package com.secretsanta.api.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import com.secretsanta.api.dao.mapper.UserDetailsMapper;
import com.secretsanta.api.model.SessionContext;

@ExtendWith(MockitoExtension.class)
@DisplayName("SantaUserDetailsService Tests")
class SantaUserDetailsServiceTest {

    @InjectMocks
    private SantaUserDetailsService userDetailsService;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private SessionContext sessionContext;

    private String schema;

    @BeforeEach
    void setUp() {
        schema = "shaw";
        when(sessionContext.getSchema()).thenReturn(schema);
        ReflectionTestUtils.setField(userDetailsService, "jdbcTemplate", jdbcTemplate);
        ReflectionTestUtils.setField(userDetailsService, "sessionContext", sessionContext);
    }

    @Test
    @DisplayName("Should load regular user by username")
    void testLoadUserByUsername_RegularUser() {
        // Arrange
        String username = "regularuser";
        SantaUserDetails expectedUserDetails = new SantaUserDetails(username, "password123", false);
        
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenReturn(expectedUserDetails);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.isCredentialsNonExpired());
        assertEquals(0, result.getAuthorities().size());
        
        verify(jdbcTemplate).queryForObject(
                eq("SELECT user_name, password, password_expired FROM " + schema + ".santa_user WHERE upper(user_name) = upper(?)"),
                any(UserDetailsMapper.class),
                eq(username));
    }

    @Test
    @DisplayName("Should load admin user (jamie) with ROLE_ADMIN")
    void testLoadUserByUsername_AdminUser() {
        // Arrange
        String username = "jamie";
        SantaUserDetails expectedUserDetails = new SantaUserDetails(username, "adminpass", false);
        
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenReturn(expectedUserDetails);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("adminpass", result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        
        verify(jdbcTemplate).queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username));
    }

    @Test
    @DisplayName("Should load admin user with case-insensitive username (Jamie)")
    void testLoadUserByUsername_AdminUserCaseInsensitive() {
        // Arrange
        String username = "Jamie";
        SantaUserDetails expectedUserDetails = new SantaUserDetails(username, "adminpass", false);
        
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenReturn(expectedUserDetails);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should load admin user with uppercase username (JAMIE)")
    void testLoadUserByUsername_AdminUserUppercase() {
        // Arrange
        String username = "JAMIE";
        SantaUserDetails expectedUserDetails = new SantaUserDetails(username, "adminpass", false);
        
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenReturn(expectedUserDetails);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String username = "nonexistent";
        
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    @DisplayName("Should use correct schema in SQL query")
    void testLoadUserByUsername_CorrectSchema() {
        // Arrange
        String username = "testuser";
        String customSchema = "custom_schema";
        SantaUserDetails expectedUserDetails = new SantaUserDetails(username, "password", false);
        
        when(sessionContext.getSchema()).thenReturn(customSchema);
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenReturn(expectedUserDetails);

        // Act
        userDetailsService.loadUserByUsername(username);

        // Assert
        verify(jdbcTemplate).queryForObject(
                eq("SELECT user_name, password, password_expired FROM " + customSchema + ".santa_user WHERE upper(user_name) = upper(?)"),
                any(UserDetailsMapper.class),
                eq(username));
    }

    @Test
    @DisplayName("Should load user with expired credentials")
    void testLoadUserByUsername_ExpiredCredentials() {
        // Arrange
        String username = "expireduser";
        SantaUserDetails expectedUserDetails = new SantaUserDetails(username, "oldpassword", true);
        
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenReturn(expectedUserDetails);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertFalse(result.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Should handle database exception")
    void testLoadUserByUsername_DatabaseException() {
        // Arrange
        String username = "testuser";
        
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    @DisplayName("Should not add ROLE_ADMIN to users that are not jamie")
    void testLoadUserByUsername_NonAdminUsers() {
        // Arrange
        String[] nonAdminUsernames = {"john", "mary", "jamiesmith", "jami"};
        
        for (String username : nonAdminUsernames) {
            SantaUserDetails expectedUserDetails = new SantaUserDetails(username, "password", false);
            
            when(jdbcTemplate.queryForObject(
                    anyString(),
                    any(UserDetailsMapper.class),
                    eq(username)))
                .thenReturn(expectedUserDetails);

            // Act
            UserDetails result = userDetailsService.loadUserByUsername(username);

            // Assert
            assertEquals(0, result.getAuthorities().size(), 
                "User " + username + " should not have ROLE_ADMIN");
        }
    }

    @Test
    @DisplayName("Should handle null username gracefully")
    void testLoadUserByUsername_NullUsername() {
        // Arrange
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(null)))
            .thenThrow(new IllegalArgumentException("Username cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userDetailsService.loadUserByUsername(null);
        });
    }

    @Test
    @DisplayName("Should handle empty username")
    void testLoadUserByUsername_EmptyUsername() {
        // Arrange
        String username = "";
        
        when(jdbcTemplate.queryForObject(
                anyString(),
                any(UserDetailsMapper.class),
                eq(username)))
            .thenThrow(new EmptyResultDataAccessException(1));

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
    }
}
