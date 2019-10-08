package com.secretsanta.api.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String displayName;
    private String email;
    private String password;
    private boolean passwordExpired;
    private boolean enabled = true;
  
    @Override
    public boolean isAccountNonExpired() {
        return passwordExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return passwordExpired;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return passwordExpired;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getUsername() {
        return userName;
    }
}
