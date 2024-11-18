package com.example.demo.member;

import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.enums.Grade;
import com.example.demo.member.MemberDto.Member_Read;



@Mapper
public interface MemberDao {
	//회원가입		
	public int save(Member member);
		
	//아이디사용여부
	int existsById(String username);
		
	//아이디 찾기
	Optional<String> findByIdUsernameByEmail(Map<String, Object> params);
	
	//비밀번호 찾기 이메일로 인증
	Optional<Member> findBypasswordUsernameByEmail(Map<String, Object> params);
	
	//임시비밀번호를 발급받아 업데이트된 비밀번호
	public void update(Member member);
	
	//회원정보에 대해 조회할때
	public Member_Read UserDetails(String loginId);
	
	//회원에대해 조회할때 password 입력받기
	public String UserDetailspassword(String loginId);
	
	//비밀번호 변경하기
	int PasswordChange(@Param("newPassword") String newPassword,@Param("username") String username);
	
	//DB에 있는 암호화된 비밀번호 가져오기
	public String PasswordDB(String username);
		
	//포인트조회
	public Integer userpoint(String username);	
	
	//기존 회원의 총구매금액 조회
	public Integer gettotalPurchase(String username);
	
	//기존 회원의 등급 조회
	public Grade getGrade(String username);
		
	//총구매금액, 포인트, 등급 업데이트
	public void Update_Point(String username,@Param("point")int Point,@Param("totalPurchase") int totalPurchase,@Param("Grade") Grade Grade);
	
	//로그인 실패핸들러에쓰이는 회원에대해 아이디만조회할때
	public Member findById(String username);
	
	//로그인실패시 5회가 되면 계정이 블럭
	public Integer memberLoginFailAndBlock(String username);
		
	//로그인에 성공시 실패카운트 초기화
	public int loginSuccessReset(String username);
	
	//회원이 내정보에서 업데이트가능한것들
	public void Member_update(String username,String email, String phone);

	public String  Memberdelete(String loginId);
	
}
