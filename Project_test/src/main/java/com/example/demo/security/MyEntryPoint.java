package com.example.demo.security;

import java.io.*;

import org.springframework.security.core.*;
import org.springframework.security.web.*;
import org.springframework.stereotype.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

// 보안이라고 하면 인증(로그인 여부) + 인가(권한)
// 로그인이 필요한 경우 적절한 메시지를 출력 -> 401오류 처리
// 권한이 모자란 경우 적절한 메시지를 출력 -> 403 오류 처리
@Component
public class MyEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// 폼 요청인 경우 로그인 페이지로 이동, ajax 요청인 경우 401 오류 메시지로 응답
		// 요청 객체, 응답 객체를 편지에 비유해보자
		//	- 편지에 편지 봉투와 편지지가 있는 것 처럼 요청, 응답도 편지봉투(header)와 편지지(body)로 구성된다
		//  - 헤더를 까보면 ajax요청인지 판별이 가능
		
		// ajax 요청인 경우 "XMLHttpRequest"란 값을 가지고, ajax 요청이 아닌 경우 null
		String ajaxHeader = request.getHeader("X-Requested-With");
		boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
		
		if(isAjax==true) {
			response.setStatus(401);
		} else {
			response.sendRedirect("/member/login");
		}
	}
}








