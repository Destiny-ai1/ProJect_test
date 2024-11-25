package com.example.demo.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;



@Component
public class MemberMailUtil {
	@Autowired
	private JavaMailSender mailSender;
	//발신자 이메일 주소
	private String Admin = "we8534@gmail.com";
	
	public void MailSend(String loginId, String title, String content) {  
		MimeMessage message = mailSender.createMimeMessage();
		
		try {
	        message.setFrom(Admin);  										// 발신자 설정
	        message.setRecipients(MimeMessage.RecipientType.TO, loginId);   // 수신자 설정
	        message.setSubject(title);  									// 제목 설정
	        message.setContent(content, "text/html; charset=utf-8");  		// 본문 설정, HTML 형식으로 설정

	        mailSender.send(message);  										// 메일 전송
	    } catch (MessagingException e) {
	        e.printStackTrace();  											// 예외 처리
	    }
	}
}
