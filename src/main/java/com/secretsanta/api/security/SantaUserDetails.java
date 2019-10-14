package com.secretsanta.api.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class SantaUserDetails implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    private Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    private String username;
    private String password;
    private boolean enabled = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    
    public SantaUserDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

}