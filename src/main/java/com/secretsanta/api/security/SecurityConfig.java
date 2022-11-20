package com.secretsanta.api.security;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.secretsanta.api.filter.SessionContextFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private AuthenticationProvider authenticationProvider;

    @Resource
    private SecurityHandler loginHandler;
    
    @Resource
    PasswordEncoder passwordEncoder;
    
    @Resource
    SessionContextFilter sessionContextFilter;
    
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/changePassword").permitAll()
                .antMatchers("/login/error").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(loginHandler)
                .failureHandler(loginHandler)
                .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .and()
            .csrf()
                .disable()
            .headers()
                .frameOptions()
                    .sameOrigin()
                .and()
            .addFilterBefore(sessionContextFilter, UsernamePasswordAuthenticationFilter.class);
    }
    
    @Autowired
    public void configureAuthManager(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
    }

}