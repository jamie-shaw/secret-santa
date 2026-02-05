package com.secretsanta.api.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@DisplayName("SantaUserDetails Tests")
class SantaUserDetailsTest {

    private SantaUserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new SantaUserDetails("testuser", "password123", false);
    }

    @Test
    @DisplayName("Should create user details with credentials not expired")
    void testConstructor_CredentialsNotExpired() {
        // Arrange & Act
        SantaUserDetails user = new SantaUserDetails("user1", "pass1", false);

        // Assert
        assertEquals("user1", user.getUsername());
        assertEquals("pass1", user.getPassword());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("Should create user details with credentials expired")
    void testConstructor_CredentialsExpired() {
        // Arrange & Act
        SantaUserDetails user = new SantaUserDetails("user2", "pass2", true);

        // Assert
        assertEquals("user2", user.getUsername());
        assertEquals("pass2", user.getPassword());
        assertFalse(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should get and set username")
    void testGetSetUsername() {
        // Act
        userDetails.setUsername("newuser");

        // Assert
        assertEquals("newuser", userDetails.getUsername());
    }

    @Test
    @DisplayName("Should get and set password")
    void testGetSetPassword() {
        // Act
        userDetails.setPassword("newpassword");

        // Assert
        assertEquals("newpassword", userDetails.getPassword());
    }

    @Test
    @DisplayName("Should get and set enabled status")
    void testGetSetEnabled() {
        // Act
        userDetails.setEnabled(false);

        // Assert
        assertFalse(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Should get and set credentials expired status")
    void testGetSetCredentialsNonExpired() {
        // Act
        userDetails.setCredentialsNonExpired(false);

        // Assert
        assertFalse(userDetails.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Should get and set account expired status")
    void testGetSetAccountNonExpired() {
        // Act
        userDetails.setAccountNonExpired(false);

        // Assert
        assertFalse(userDetails.isAccountNonExpired());
    }

    @Test
    @DisplayName("Should get and set account locked status")
    void testGetSetAccountNonLocked() {
        // Act
        userDetails.setAccountNonLocked(false);

        // Assert
        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should add and retrieve authorities")
    void testGetSetAuthorities() {
        // Arrange
        SimpleGrantedAuthority authority1 = new SimpleGrantedAuthority("ROLE_USER");
        SimpleGrantedAuthority authority2 = new SimpleGrantedAuthority("ROLE_ADMIN");

        // Act
        userDetails.getAuthorities().add(authority1);
        userDetails.getAuthorities().add(authority2);

        // Assert
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(authority1));
        assertTrue(userDetails.getAuthorities().contains(authority2));
    }

    @Test
    @DisplayName("Should set new authorities collection")
    void testSetAuthorities() {
        // Arrange
        ArrayList<SimpleGrantedAuthority> newAuthorities = new ArrayList<>();
        newAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Act
        userDetails.setAuthorities(newAuthorities);

        // Assert
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("Should have default values when created")
    void testDefaultValues() {
        // Assert
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertNotNull(userDetails.getAuthorities());
    }

    @Test
    @DisplayName("Should handle null username")
    void testNullUsername() {
        // Arrange & Act
        SantaUserDetails user = new SantaUserDetails(null, "password", false);

        // Assert
        assertNull(user.getUsername());
    }

    @Test
    @DisplayName("Should handle null password")
    void testNullPassword() {
        // Arrange & Act
        SantaUserDetails user = new SantaUserDetails("username", null, false);

        // Assert
        assertNull(user.getPassword());
    }

    @Test
    @DisplayName("Should handle empty username")
    void testEmptyUsername() {
        // Arrange & Act
        SantaUserDetails user = new SantaUserDetails("", "password", false);

        // Assert
        assertEquals("", user.getUsername());
    }

    @Test
    @DisplayName("Should handle empty password")
    void testEmptyPassword() {
        // Arrange & Act
        SantaUserDetails user = new SantaUserDetails("username", "", false);

        // Assert
        assertEquals("", user.getPassword());
    }
}
