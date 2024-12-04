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
    	
        String username = request.getParameter("username");
        Member member = memberDao.findById(username);
        HttpSession session = request.getSession();

        if (member == null) {
            // 가입되지 않은 사용자 처리
            session.setAttribute("message", "가입되어 있지 않은 사용자입니다.");
        } else {
            if (!member.getEnabled()) {
                // 블록된 계정 처리
                session.setAttribute("message", "블록된 계정입니다. 관리자에게 연락하세요.");
            } else {
                int failCount = member.getLoginFailCount() + 1; // 실패 횟수 증가

                if (failCount >= 5) {
                    // 5회 실패 시 계정 블록
                    memberDao.memberLoginFailAndBlock(username); // DAO에 있는 메서드 이름 유지
                    session.setAttribute("message", "로그인 5회 실패로 계정이 블록되었습니다. 관리자에게 연락하세요.");
                } else {
                    // 실패 횟수 업데이트 및 메시지 설정
                    memberDao.memberLoginFailAndBlock(username); // 동일 메서드 사용
                    if (failCount == 4) {
                        session.setAttribute("message", "로그인 5회 실패 시 계정이 블록됩니다.");
                    } else {
                        session.setAttribute("message", "로그인에 " + failCount + "회 실패했습니다.");
                    }
                }
            }
        }

        // 로그인 페이지로 리다이렉트
        response.sendRedirect("/member/login");
    }
}















