package com.example.demo.security;

import java.io.*;

import org.springframework.security.access.*;
import org.springframework.security.web.access.*;
import org.springframework.stereotype.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		// SSR 요청이면 /error/403으로 이동
		// ajax 요청이면 403이라고 응답
		String ajaxHeader = request.getHeader("X-Requested-With");
		boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
		
		if(isAjax==true) {
			response.setStatus(403);
		} else {
			response.sendRedirect("/error/403");		
		}
	}
}
