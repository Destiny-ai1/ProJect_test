package com.example.demo.member;

import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MemberDao {
	//회원가입
		@Insert("insert into member (username, password, name, email, phone_number, joinday, grade, role, point_money) "
		        + "values (#{username}, #{password}, #{name}, #{email}, #{phone_number}, #{joinday}, #{grade}, #{role}, #{point_money})")
		public int save(Member member);
		
		//아이디사용여부
		@Select("select count(username)>0 from member where username=#{username}")
		public boolean existsById(String username);
		
		//아이디 찾기 이메일로 인증
		@Select("select username from member where email=#{email}")
		public Optional<String> findByIdUsernameByEmail(String email);
		
		//내정보 불러오기
		@Select("select * from member where username = #{username}")
		public Optional<Member> findById(String username);
		
		//회원 탈퇴
		@Delete("delete * from member where username=#{username}")
		public int delete(Member member);
		
		//로그인실패시 5회가 되면 계정이 블럭
		@Update("update member set login_fail_Count = login_fail_Count + 1,"
				+ "enabled= case when login_fail_Count +1 >=5 then else enabled end where username=#{username}")
		public int Member_login_FailandBlock(String username);
		
		//로그인 에 성공시 실패카운트 초기화
		@Update("update member set login_fail_Count=0 where usesrname=#{username}")
		public int login_success_Reset(String username);
		
		//포인트조회
		@Select("select point_money from member where username=#{username}")
		public int FindPointByusername(String username);
		
		//포인트 적립
		@Update("update member set.point_money=point_money + #{point} where username=#{username}")
		public void Update_Point(String username, int totalpurchase);


}
