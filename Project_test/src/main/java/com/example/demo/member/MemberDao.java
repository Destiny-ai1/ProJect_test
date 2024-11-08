package com.example.demo.member;

import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberDao {
	//회원가입
	@Insert("insert into member (username, password, name, email, phone_number, joinday, grade, role, point_money) "
	        + "values (#{username}, #{password}, #{name}, #{email}, #{phone_number}, #{joinday}, #{grade}, #{role}, #{point_money})")
	public int save(Member member);
	
	//아이디사용여부
	@Select("select count(username) from member where username=#{username}")
	public boolean existsById(String username);
	
	//아이디 찾기 이메일로 인증
	@Select("select username from member where email=#{email}")
	public Optional<String> findByIdUsernameByEmail(String email);


}
