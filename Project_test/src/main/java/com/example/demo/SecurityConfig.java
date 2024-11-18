package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.security.LoginFailureHandler;
import com.example.demo.security.LoginSuccessHandler;
import com.example.demo.security.MyEntryPoint;


@Configuration
@EnableWebSecurity 														// 웹 보안 설정 활성화
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true) 	// 메서드 수준 보안 활성화
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
        
        http.sessionManagement(c -> c
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 필요할 때만 세션 생성
                .maximumSessions(1)                                      // 한 사용자당 최대 1개 세션 허용
                .maxSessionsPreventsLogin(false));                       // 새로운 로그인이 기존 세션을 대체
            
            return http.build();
        }
}
