package com.example.demo.member;

import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;



@Mapper
public interface MemberDao {
	//회원가입		
	public int save(Member member);
		
	//아이디사용여부
	int existsById(String username);
		
	//아이디 찾기
	Optional<Member> findByIdUsernameByEmail(Map<String, Object> params);
	
	//	비밀번호 찾기 이메일로 인증
	Optional<Member> findBypasswordUsernameByEmail(Map<String, Object> params);
		
	//회원 탈퇴
	public int delete(Member member);
		
	//로그인실패시 5회가 되면 계정이 블럭
	public int Member_login_FailandBlock(String username);
	
	//로그인 에 성공시 실패카운트 초기화
	public int login_success_Reset(String username);
		
	//포인트조회
	public int FindPointByusername(String username);
		
	//포인트 적립
	public void Update_Point(String username, int totalpurchase);
	
	//회원 조회
	public Optional<Member> findById(String username);
	
}
