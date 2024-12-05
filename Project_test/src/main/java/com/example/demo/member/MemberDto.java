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
	@Data
	public static class Member_Create{
		@NotEmpty
		private String username;
		@NotEmpty
		private String password;
		@NotEmpty
		private String name;
		@NotEmpty
		@Email
		private String email;
		@NotEmpty
		private String phone;
	
		public Member toEntity(String encodedPassword) {
			return new Member(username.toLowerCase(), encodedPassword, name, email, phone, LocalDate.now(), 0, Grade.Bronze, 0, true, Role.user, 0);
		}
	}
	
	//회원정보에서 바꿀수있는 정보
	@Data
	public static class Member_update{
		private String name;					//내정보내에서 변경가능
		private String oldPassword;				//내정보보기에서 비밀번호변경 버튼눌러서 다른페이지로 이동시킬것
		private String newPassword;				//내정보보기에서 비밀번호변경 버튼눌러서 다른페이지로 이동시킬것
		@Email
		private String email;					//내정보내에서 변경가능
		private String phone;					//내정보내에서 변경가능
		private int point;						//변경다른곳에서 처리함
	}
	
	//회원정보를 출력할때 나오는것들
	@Data
	@AllArgsConstructor
	public static class Member_Read{
		private String username;				//조회를위해서 있어야되는거고 html에서 출력안함
		private String name;					//고정값
		@Email
		private String email;					
		private String phone;
		private LocalDate joinday;				//변경불가능
		private Integer totalpurchase;			//변경다른곳에서 처리함	
		private String grade;					//변경다른곳에서 처리함	
		private String role;					//변경불가(html에서는 출력할때는 활성화라고 출력)
		private int point;						//변경다른곳에서 처리함
	}
}
