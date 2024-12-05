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
    	// 카카오페이 설정
    	http.authorizeHttpRequests(c -> c.requestMatchers("/payment/ready", "/payment/approve").authenticated() // 인증된 사용자만 접근 가능
				.anyRequest().permitAll()); // 나머지 모든 요청에 대해 접근 허용
    	
    	// 로그인 설정
        http.formLogin(c -> c.loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                .successHandler(successHandler)
                .failureHandler(failureHandler));
                
        // 로그아웃 설정
        http.logout(c -> c.logoutUrl("/member/logout").logoutSuccessUrl("/"));
        
        // 인증 실패 처리
        http.exceptionHandling(c -> c.authenticationEntryPoint(myEntryPoint));
        
        // 세션 관리 설정
        http.sessionManagement(c -> c
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) 		// 항상 세션 생성
                .maximumSessions(1)                                      	// 한 사용자당 최대 1개 세션 허용
                .maxSessionsPreventsLogin(false));                       	// 새로운 로그인이 기존 세션을 대체
        
        return http.build();
    }
}
