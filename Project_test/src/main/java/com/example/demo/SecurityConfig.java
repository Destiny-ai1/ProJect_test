package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.security.LoginFailureHandler;
import com.example.demo.security.LoginSuccessHandler;
import com.example.demo.security.MyEntryPoint;


@Configuration
public class SecurityConfig {

    @Autowired
    private LoginSuccessHandler successHandler;

    @Autowired
    private LoginFailureHandler failureHandler;

    @Autowired
    private MyEntryPoint myEntryPoint;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.ignoringRequestMatchers("/api/boards/image"));
        
        http.formLogin(c -> c.loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                .successHandler(successHandler)
                .failureHandler(failureHandler));
                
        http.logout(c -> c.logoutUrl("/member/logout").logoutSuccessUrl("/"));
        http.exceptionHandling(c -> c.authenticationEntryPoint(myEntryPoint));
        
        return http.build();
    }
}
