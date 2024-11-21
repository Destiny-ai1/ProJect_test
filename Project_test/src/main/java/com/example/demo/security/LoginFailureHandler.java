package com.example.demo.security;

import java.io.*;


import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;

import com.example.demo.member.Member;
import com.example.demo.member.MemberDao;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.transaction.*;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
	@Autowired
	private MemberDao memberDao;
	
	@Transactional
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// 사용자 요청에서 아이디를 꺼낸다
		String username = request.getParameter("username");
		
		// 아이디를 가지고 사용자 정보를 가져온다(로그인 실패횟수 증가 + 계정 블록 을 확인할려고)
		Member member  = memberDao.findById(username);
		// 사용자 정보를 저장하는 세션을 꺼내자
		HttpSession session = request.getSession();
		
		if (member == null) {
			
            session.setAttribute("message", "가입되어 있지 않은 사용자입니다.");
        } 
		
		else {
		
            session.setAttribute("message", "비밀번호가 틀렸습니다.");
		
        if (!member.getEnabled()) {
                session.setAttribute("message", "블록된 계정입니다. 관리자에게 연락하세요.");
            } 
        
        else if (member.getLoginFailCount() == 4) {
        		memberDao.memberLoginFailAndBlock(username);
                session.setAttribute("message", "로그인 5회 실패로 계정이 블록되었습니다. 관리자에게 연락하세요.");
            } 
        
        else {
        		memberDao.memberLoginFailAndBlock(username);
                session.setAttribute("message", "로그인에 " + member.getLoginFailCount() + "회 실패했습니다.");
            }
        }
        response.sendRedirect("/member/login");
    }
}














