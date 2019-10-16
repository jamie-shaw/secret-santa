package com.secretsanta.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.secretsanta.api.mapper.UserDetailsMapper;

@Service("userDetailsService")
public class SantaUserDetailsService implements UserDetailsService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        String SQL =  "SELECT UserName, Password, PasswordExpired " +
                        "FROM User " +
                       "WHERE UserName = ?";

        SantaUserDetails userDetails = (SantaUserDetails) jdbcTemplate.queryForObject(SQL, new Object[]{username}, new UserDetailsMapper());

        if (username.equalsIgnoreCase("jamie")) {
            SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ROLE_ADMIN");
            userDetails.getAuthorities().add(admin);
            
        }
        return userDetails;
    
    }
}