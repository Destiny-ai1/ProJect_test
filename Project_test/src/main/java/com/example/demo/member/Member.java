package com.example.demo.member;

import java.time.*;


import org.hibernate.annotations.DynamicUpdate;

import com.example.demo.enums.*;
import com.example.demo.enums.Role;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
public class Member {
	@Id
	private String username;
	private String password;
	private String name;
	private String email;
	private String phone_number;
	private LocalDateTime joinday;
	private int totalpurchase;
	@Enumerated(EnumType.STRING)
	private Grade grade;
	private Integer loginFailCount;
	private Boolean enabled;
	@Enumerated(EnumType.STRING)
	private Role role;
	private int point;
	
	//바꿀 비밀번호
	public void changePassword(String password) {
		this.password = password;
	}
	
	//회원정보를 읽어오기
	public MemberDto.Member_Read MyDetail(String loginId){
		return new MemberDto.Member_Read(name, email, phone_number, joinday.toLocalDate(), totalpurchase, grade.name(), role.name(), point);
	}
	
	//계정 블록걸때
	public void Member_Block() {
		this.enabled= false;
		this.loginFailCount= 5;
	}
	
	//로그인 실패횟수 올라가는 카운트
	public void Member_login_Fail_Count() {
		this.loginFailCount=0;
	}
	
	public void update(String email, String phone_number, String encodedPassword) {
	    this.email = email;
	    this.phone_number = phone_number;
	    this.password = encodedPassword; 
	}
}
