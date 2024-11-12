package com.example.demo.member;



import java.util.Optional;


import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.enums.PasswordChange;
import com.example.demo.exception.FailException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class MemberService {
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private MemberMailUtil mailUtil;
	
	
	//회원가입
	public void join(MemberDto.Member_Create dto) {
		// 1. 비밀번호를 암호화
		String encodedPassword = encoder.encode(dto.getPassword());
		
		// 2. dto를 멤버로 변환
		Member member = dto.toEntity(encodedPassword);
		
		// 3. Member를 DB에도 저장
		memberDao.save(member);  
	}
	
	//아이디 사용가능 
	public boolean Id_Available(String username) {
		return !memberDao.existsById(username);
	}
	
	//아이디 찾기
	public Optional<String> Idfind(String email) {
		return memberDao.findByIdUsernameByEmail(email);
	}
	
	@Transactional
	public boolean 비밀번호찾기로_임시비밀번호_발급(String username) {
		Optional<Member> result = memberDao.findById(username);
		if(result.isEmpty())
			return false;
		String newPassword = RandomStringUtils.randomAlphanumeric(20);
		String newEncodedPassword = encoder.encode(newPassword);
		Member member = result.get();
		member.changePassword(newEncodedPassword);
		mailUtil.MailSend(member.getEmail(), "임시비밀번호 입니다", "임시비밀번호  : " + newPassword);
		return true;
	}
		
	// 로그인을 한 상태에서 내정보 누르면 비밀번호 확인
	public boolean 비밀번호확인(String password, String loginId) {
		String encodedPassword = memberDao.findById(loginId).get().getPassword();
		return encoder.matches(password, encodedPassword);	
	}
	//내정보 불러올때
	public MemberDto.Member_Read 내정보보기(String loginId) {
		Member member = memberDao.findById(loginId).get();
		return member.MyDetail(loginId);
	}
	
	//회원 탈퇴할때
	public void delete(String loginId) {
		Member member = memberDao.findById(loginId).get();
		memberDao.delete(member);
	}

	@Transactional
	public PasswordChange updatePassword(MemberDto.Member_update dto, String loginId) {
		if(dto.getOldPassword().equals(dto.getNewPassword())==true)
			return PasswordChange.Old_Password;
		Member member = memberDao.findById(loginId).get();
		boolean result = encoder.matches(dto.getOldPassword(), member.getPassword());
		if(result==false)
			return PasswordChange.Password_Check_Fail;
		String newEncodedPassword = encoder.encode(dto.getNewPassword());
		member.changePassword(newEncodedPassword);
		return PasswordChange.Success;
	}
	//회원간 포인트 조회 및 적립
	public int MemberPoint(String username,int totalpurchase) {
		//회원별 포인트 조회
		int MemberPoint=memberDao.FindPointByusername(username);
		//포인트 적립(구매금액의 5%)
		int PointAdd = (int)(totalpurchase* 0.05);
		//적립된 point 를 업데이트
		memberDao.Update_Point(username,totalpurchase);
		//최종 포인트
		return MemberPoint+PointAdd;
	}
	
	//회원정보에서 업데이트 처리
	public void update(@Valid MemberDto.Member_update dto, String loginId) {
		Member member = memberDao.findById(loginId).orElseThrow(()-> new FailException("Member not found"));
	    // encoder를 사용해서 비밀번호를 암호화
	    String encodedPassword = encoder.encode(dto.getNewPassword());
	    // 이메일, 전화번호, 암호화된 비밀번호를 업데이트
	    member.update(dto.getEmail(), dto.getPhone_number(), encodedPassword);
	}
}
