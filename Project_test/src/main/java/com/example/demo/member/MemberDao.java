package com.example.demo.member;

import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.member.MemberDto.Member_Read;



@Mapper
public interface MemberDao {
	//회원가입		
	public int save(Member member);
		
	//아이디사용여부
	int existsById(String username);
		
	//아이디 찾기
	Optional<String> findByIdUsernameByEmail(Map<String, Object> params);
	
	//	비밀번호 찾기 이메일로 인증
	Optional<Member> findBypasswordUsernameByEmail(Map<String, Object> params);
	
	//임시비밀번호를 발급받아 업데이트된 비밀번호
	public void update(Member member);
	
	//회원에 대해 조회할때
	public Member_Read UserDetails(String loginId);
	
	//회원에대해 조회할때 password 입력받기
	public String UserDetailspassword(String loginId);
	
	//비밀번호 변경하기
	int PasswordChange(@Param("newPassword") String newPassword,@Param("username") String username);
	
	//DB에 있는 암호화된 비밀번호 가져오기
	public String PasswordDB(String username);
		
	//포인트조회
	public int userpoint(String loginId);	
		
	//포인트 업데이트
	public void Update_Point(@Param("username")String username,@Param("points") int points);
	
	
	//로그인실패시 5회가 되면 계정이 블럭
	public int Member_login_FailandBlock(String username);
		
	//로그인에 성공시 실패카운트 초기화
	public int login_success_Reset(String username);
	
	//회원 탈퇴
	public int delete(Member member);

	public int totalPurchase(String loginId);



}
