package com.secretsanta.api.security;

import jakarta.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.secretsanta.api.dao.mapper.UserDetailsMapper;
import com.secretsanta.api.model.SessionContext;

@Service("userDetailsService")
public class SantaUserDetailsService implements UserDetailsService {
    
    @Resource
    private JdbcTemplate jdbcTemplate;
    
    @Resource
    private SessionContext sessionContext;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        String SQL =  "SELECT user_name, password, password_expired " +
                        "FROM " + sessionContext.getSchema() + ".santa_user " +
                       "WHERE upper(user_name) = upper(?)";

        SantaUserDetails userDetails = (SantaUserDetails) jdbcTemplate.queryForObject(SQL, new UserDetailsMapper(), username);

        if (username.equalsIgnoreCase("jamie")) {
            SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ROLE_ADMIN");
            userDetails.getAuthorities().add(admin);
            
        }
        return userDetails;
    
    }
}