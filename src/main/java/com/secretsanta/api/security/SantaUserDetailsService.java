package com.secretsanta.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.secretsanta.api.mapper.UserDetailsMapper;
import com.secretsanta.api.util.SystemContextHolder;

@Service("userDetailsService")
public class SantaUserDetailsService implements UserDetailsService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        String SQL =  "SELECT user_name, password, password_expired " +
                        "FROM " + SystemContextHolder.getSchema() + ".santa_user " +
                       "WHERE user_name = ?";

        SantaUserDetails userDetails = (SantaUserDetails) jdbcTemplate.queryForObject(SQL, new Object[]{username}, new UserDetailsMapper());

        if (username.equalsIgnoreCase("jamie")) {
            SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ROLE_ADMIN");
            userDetails.getAuthorities().add(admin);
            
        }
        return userDetails;
    
    }
}