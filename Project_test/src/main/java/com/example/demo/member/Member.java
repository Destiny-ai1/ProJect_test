package com.example.demo.member;

import java.time.*;



import com.example.demo.enums.*;
import com.example.demo.enums.Role;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Builder
public class Member {
	@Id
	private String username;
	private String password;
	private String name;
	private String email;
	private String phone;
	private LocalDate joinday;
	private Integer totalpurchase=0;
	private Grade grade;
	private Integer loginFailCount;
	private Boolean enabled=false;
	private Role role;
	private Integer point=0;
	
	//바꿀 비밀번호
	public void changePassword(String password) {
		this.password = password;
	}
	
	//회원정보를 읽어오기
	public MemberDto.Member_Read MyDetail(String loginId){
		return new MemberDto.Member_Read(username,name, email, phone, joinday, totalpurchase, grade.name(), role.name(), point);
	}
	
	//로그인 실패횟수 올라가는 카운트
	public void Member_login_Fail_Count() {
		this.loginFailCount=0;
	}
	
	//계정 블록걸때
	public void Member_Block() {
		this.enabled= false;
		this.loginFailCount= 5;
	}

	//내정보에서 업데이트되는것들
	public void update(String email, String phone_number, String encodedPassword) {
	    this.email = email;
	    this.phone = phone_number;
	    this.password = encodedPassword; 
	}
}
