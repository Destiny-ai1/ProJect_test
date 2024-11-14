package com.example.demo.member;

import java.time.LocalDate;

import com.example.demo.enums.Grade;
import com.example.demo.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

public class MemberDto {
private MemberDto() {}
	
	//계정 생성할때 필수로 받을값
	@NotEmpty
	@Data
	public static class Member_Create{
		private String username;
		private String password;
		private String name;
		@Email
		private String email;
		private String phone;
	
		public Member toEntity(String encodedPassword) {
			return new Member(username.toLowerCase(), encodedPassword, name, email, phone, LocalDate.now(), 0, Grade.Bronze, null, null, Role.user, 0);
		}
	}
	//회원정보에서 바꿀수있는 정보
	@Data
	public static class Member_update{
		private String oldPassword;
		private String newPassword;
		@Email
		private String email;
		private String phone;
		private int point;
	}
	
	//회원정보를 출력할때 나오는것들
	@Data
	@AllArgsConstructor
	public static class Member_Read{
		private String name;
		@Email
		private String email;
		private String phone;
		private LocalDate joinday;
		private int totalpurchase;
		private String grade;
		private String role;
		private int point;
	}
}
