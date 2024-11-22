package com.example.demo.member;



import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.enums.Grade;
import com.example.demo.enums.PasswordChange;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class MemberService {
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private MemberMailUtil mailSend;

	
	
	//회원가입
	public void join(MemberDto.Member_Create dto) {
		try {
	        // 1. 비밀번호를 암호화
	        String encodedPassword = encoder.encode(dto.getPassword());
	        System.out.println("비밀번호 암호화 완료: " + encodedPassword);
	        
	        // 2. dto를 멤버로 변환
	        System.out.println("DTO 데이터: " + dto);
	        Member member = dto.toEntity(encodedPassword);
	        System.out.println("Member 객체로 변환: " + member);
	        
	        // 3. Member를 DB에 저장
	        memberDao.save(member);
	        System.out.println("DB 저장 완료: " + member.getUsername());
	    } catch(Exception e) {
	        // 예외 출력
	        e.printStackTrace();
	        System.out.println("회원가입 중 오류 발생: " + e.getMessage());
	    }
	}
	
	//중복인지 아닌지 확인후 아이디 사용가능 
	public boolean Id_Available(String username) {
		int count = memberDao.existsById(username);
	    return count == 0;
	}
	
	//이메일을 통해서 아이디 찾기
	public Optional<String> Idfind(String name,String email) {
		Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        Optional<String> username = memberDao.findByIdUsernameByEmail(params);
        username.ifPresent(user->{
        	String subject = "아이디 찾기 결과";
            String content = "회원님의 아이디는 " + user + " 입니다.";
            mailSend.MailSend(email, subject, content);
        });
        return null;
	}
	
	@Transactional
	public boolean 비밀번호찾기로_임시비밀번호_발급(String username,String name, String email) {
		Map<String, Object> params = new HashMap<>();
		params.put("username", username);
	    params.put("name", name);
	    params.put("email", email);
	    
	    Optional<Member> optionalMember = memberDao.findBypasswordUsernameByEmail(params);
        if (optionalMember.isEmpty()) {
            return false;
        }
      
        Member member = optionalMember.get();
		String newPassword = RandomStringUtils.randomAlphanumeric(20);
		String newEncodedPassword = encoder.encode(newPassword);
		
		member.changePassword(newEncodedPassword);
		memberDao.update(member);
		
		String subject = "임시비밀번호 발급";
	    String content = "임시 비밀번호: " + newPassword;
	    mailSend.MailSend(email, subject, content);
		return true;
	}
	
	//내정보 불러올때
	public MemberDto.Member_Read 내정보보기(String loginId) {
        return memberDao.UserDetails(loginId);
	}
	
	//비밀번호 확인
	public boolean 비밀번호확인(String password, String loginId) {
		String encodedPassword = memberDao.UserDetailspassword(loginId);
		return encoder.matches(password, encodedPassword);
	}

	
	//비밀번호 바꾸기
	@Transactional
	public PasswordChange updatePassword(MemberDto.Member_update dto, String loginId) {
		if(dto.getOldPassword().equals(dto.getNewPassword())==true) {
			return PasswordChange.Old_Password;
		}
		
		String encodedPassword = memberDao.PasswordDB(loginId);
		boolean result = encoder.matches(dto.getOldPassword(),encodedPassword);
		if(result==false) {
			return PasswordChange.Password_Check_Fail;
		}
		String newPassword = encoder.encode(dto.getNewPassword());
		memberDao.PasswordChange(newPassword,loginId);
		return PasswordChange.Success;
	}
	
	//회원이 물건구매후 구매금액의 5%포인트적립 및 등급 상승 및 총구매금액 업데이트
	@Transactional
    public int updateMemberPurchase(String loginId, int purchaseAmount) {
        // 현재 포인트, 총 구매금액 조회
		Integer currentTotalPurchase = memberDao.gettotalPurchase(loginId);			//기존 회원의 총구매금액 조회
		if (currentTotalPurchase == null) {
	        currentTotalPurchase = 0;
	    }
		
        Integer currentPoints = memberDao.userpoint(loginId);						//기존포인트 조회
        if (currentPoints == null) {
            currentPoints = 0;
        }
        
        int pointsToAdd = (int) (purchaseAmount * 0.05);							// 포인트 계산 (구매금액의 5%)
        int updatedPoints = currentPoints + pointsToAdd;							//기존포인트+구매금액 계산된 포인트

        // 총 구매 금액 업데이트
        int updatedTotalPurchase = currentTotalPurchase + purchaseAmount;		//기존구매금액+구매금액

        Grade newGrade = updateGrade(updatedTotalPurchase);						// 등급 계산

        // 데이터베이스 업데이트 (포인트, 총 구매금액, 등급)
        memberDao.Update_Point(loginId, updatedPoints, updatedTotalPurchase, newGrade);

        return updatedPoints;  // 업데이트된 포인트 반환
    }

    private Grade updateGrade(int totalPurchase) {
        if (totalPurchase >= 1_000_000) {
            return Grade.Diamond;
        } else if (totalPurchase >= 500_000) {
            return Grade.Gold;
        } else if (totalPurchase >= 300_000) {
            return Grade.Silver;
        } else {
            return Grade.Bronze;
        }											//최종 등급
	}
	
	//회원정보에서 업데이트 처리
	public void member_update(@Valid MemberDto.Member_update dto,String loginId ) {
	    // 이메일, 전화번호,
		if (dto.getEmail() != null || dto.getPhone() != null) {
	        memberDao.Member_update(loginId,dto.getEmail(), dto.getPhone());
	    }	
	}
	
	//회원 탈퇴할때
	@Transactional
	public void delete(String loginId) {
		memberDao.Memberdelete(loginId);
	}

}
