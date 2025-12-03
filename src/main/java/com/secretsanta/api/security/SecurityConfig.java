package com.secretsanta.api.security;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.secretsanta.api.filter.SessionContextFilter;

@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private AuthenticationProvider authenticationProvider;

    @Resource
    private SecurityHandler loginHandler;

    @Resource
    SessionContextFilter sessionContextFilter;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/app/**").permitAll()  // Allow access to Angular app
                .antMatchers("/api/status").permitAll()  // Allow status endpoint
                .antMatchers("/api/health").permitAll()  // Allow health endpoint
                .antMatchers("/api/auth/login").permitAll()  // Allow login endpoint
                .antMatchers("/api/auth/logout").permitAll()  // Allow logout endpoint
                .antMatchers("/api/**").authenticated()  // Require authentication for other API endpoints
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
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(sessionContextFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Autowired
    public void configureAuthManager(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
    }

}