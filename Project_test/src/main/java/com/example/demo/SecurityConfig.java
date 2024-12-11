package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.security.LoginFailureHandler;
import com.example.demo.security.LoginSuccessHandler;
import com.example.demo.security.MyEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private LoginSuccessHandler successHandler;

    @Autowired
    private LoginFailureHandler failureHandler;

    @Autowired
    private MyEntryPoint myEntryPoint;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 요청 경로 매칭 순서 및 권한 설정
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/payment/ready", "/payment/approve").authenticated() // 인증된 사용자만 접근 가능
            .requestMatchers("/item/add").hasRole("admin")                          // 상품 추가는 ADMIN 권한 필요
            .requestMatchers(HttpMethod.POST, "/item/add").hasRole("admin")         // POST 요청도 동일한 권한 필요
            .requestMatchers(HttpMethod.PATCH, "/item/read").hasRole("admin") 		// PATCH 요청은 ADMIN 권한 필요
            .requestMatchers(HttpMethod.DELETE, "/item/delete/**").hasRole("admin") // DELETE 요청에 대해 admin만 허용
            .anyRequest().permitAll()                                              // 나머지 요청은 모두 허용
        );

        // 로그인 설정
        http.formLogin(c -> c.loginPage("/member/login")
            .loginProcessingUrl("/member/login")
            .successHandler(successHandler)
            .failureHandler(failureHandler)
        );

        // 로그아웃 설정
        http.logout(c -> c.logoutUrl("/member/logout").logoutSuccessUrl("/"));

        // 인증 실패 처리
        http.exceptionHandling(c -> c.authenticationEntryPoint(myEntryPoint));

        // 세션 관리 설정
        http.sessionManagement(c -> c
            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // 항상 세션 생성
            .maximumSessions(1)                                   // 최대 1개 세션 허용
            .maxSessionsPreventsLogin(false)                      // 새로운 로그인 시 기존 세션 대체
        );

        return http.build();
    }
}
