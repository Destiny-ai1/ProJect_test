package com.example.demo.member;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.MemberDao;
import com.example.demo.dto.MemberDto;
import com.example.demo.entity.Member;
import com.example.demo.enums.PasswordChangeResult;
import com.example.demo.util.MailUtil;

import jakarta.transaction.Transactional;

@Service
public class MemberService {
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private MailUtil mailUtil;
	@Value("${sample.board.profile.url}")
	private String url;
	@Value("${sample.board.image.folder}")
	private String imageSaveFolder;
	@Value("${sample.board.image.default}")
	private String defaultImage; 
	
	//회원가입
	public void join(MemberDto.Member_Create dto) {
		// 1. 비밀번호를 암호화
		String encodedPassword = encoder.encode(dto.getPassword());
		
		// 2. dto를 멤버로 변환
		Member member = dto.toEntity(encodedPassword);
		
		// 3. 멤버를 저장하고 성공했다면 프사를 저장
		memberDao.save(member);  
	}
	
	public boolean 아이디사용가능(String username) {
		return !memberDao.existsById(username);
	}
	
	public Optional<String> 아이디찾기(String email) {
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
		mailUtil.sendMail(member.getEmail(), "임시비밀번호 입니다", "임시비밀번호  : " + newPassword);
		return true;
	}
		
	// 로그인을 한 상태니까 내정보를 못 읽을 수가 없다
	public boolean 비밀번호확인(String password, String loginId) {
		String encodedPassword = memberDao.findById(loginId).get().getPassword();
		return encoder.matches(password, encodedPassword);	
	}
	
	public MemberDto.Member_Read 내정보보기(String loginId) {
		Member member = memberDao.findById(loginId).get();
		return member.toReadDto(url);
	}
		
	public void 탈퇴(String loginId) {
		Member member = memberDao.findById(loginId).get();
		File file = new File(imageSaveFolder, member.getProfile());
		if(file.exists())
			file.delete();
		memberDao.delete(member);
	}

	@Transactional
	public PasswordChangeResult updatePassword(MemberDto.UpdatePassword dto, String loginId) {
		if(dto.getOldPassword().equals(dto.getNewPassword())==true)
			return PasswordChangeResult.SAME_AS_OLD_PASSWORD;
		Member member = memberDao.findById(loginId).get();
		boolean result = encoder.matches(dto.getOldPassword(), member.getPassword());
		if(result==false)
			return PasswordChangeResult.PASSWORD_CHECK_FAIL;
		String newEncodedPassword = encoder.encode(dto.getNewPassword());
		member.changePassword(newEncodedPassword);
		return PasswordChangeResult.SUCCESS;
	}

	@Transactional
	public void update(MemberDto.Update dto, String loginId) {
		Member member = memberDao.findById(loginId).get();
		
		// 서버에서 프사를 변경하면 dto.getProfile()에 MultipartFile이 들어있고, 프사를 변경하지 않으면 비어있다
		// 프론트에 <input type='file' name='profile'>이 존재할 경우 MultipartFile profile이 절대 null이 되지 않는다
		if(dto.getProfile().isEmpty()==false) {
			// 새프사를 저장할 이름(사용자 아이디 + 확장자)을 계산
			MultipartFile newProfile = dto.getProfile();
			String 확장자 =  FilenameUtils.getExtension(newProfile.getOriginalFilename());
			String 새프로필이름 = loginId + "." + 확장자;
			
			// 기존 프사와 새 프사는 파일의 이름은 사용자 아이디와 같다. 확장자는 다를 수 있다
			// 기존 프사와 새 프사의 확장자가 다르다면 기존 프사를 삭제한다
			boolean 기존프사삭제여부 = (member.getProfile().equals(새프로필이름)==false);
			
			try {
				// 폴더명과 파일명으로 File 객체를 생성 -> 파일이 없으면 새로 만들고 있으면 덮어 쓴다
				File file = new File(imageSaveFolder, 새프로필이름);
				newProfile.transferTo(file);
				if(기존프사삭제여부==true) {
					File deleteProfile = new File(imageSaveFolder, member.getProfile());
					if(deleteProfile.exists())
						deleteProfile.delete();
				}
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			// 프사를 변경한 경우
			member.update(dto.getEmail(), dto.getBirthday(), 새프로필이름);
		} else {
			// 프사를 변경하지 않은 경우
			member.update(dto.getEmail(), dto.getBirthday(), null);
		}
	}
}
