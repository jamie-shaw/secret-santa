package com.secretsanta.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
        
        String SQL =  "SELECT UserName, Password " +
                        "FROM User " +
                       "WHERE UserName = ?";

        UserDetails userDetails = (UserDetails) jdbcTemplate.queryForObject(SQL, new Object[]{username}, new UserDetailsMapper());

        return userDetails;
    
    }
}