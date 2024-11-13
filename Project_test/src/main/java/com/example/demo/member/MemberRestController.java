package com.example.demo.member;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@RestController
@Validated
public class MemberRestController {
	@Autowired
	private MemberService memberService;
	
	//아이디 사용가능 한지 여부 확인
	@GetMapping("/members/username/available")
	public ResponseEntity<?> Id_사용가능한지(@Valid @NotEmpty String username) {
		boolean result = memberService.Id_Available(username); 
	    if (result == true) {
	        return ResponseEntity.ok(true);
	    }
	    return ResponseEntity.status(409).body(false);
	}
	
	//이메일을 통해서 아이디 찾기
	@GetMapping("/members/username")
	public ResponseEntity<?> Find_Id(@RequestParam String name, @RequestParam String email) {
		Optional<String> result = memberService.Idfind(name,email);
		if(result.isEmpty())
			return ResponseEntity.status(409).body("일치하는 아이디가 없습니다. 다시 확인해주세요");
		return ResponseEntity.ok("회원님의 아이디는"+result.get()+"입니다");
	}
	
	@PatchMapping("/members/reset-password")
	public ResponseEntity<?> 비밀번호리셋(
			@RequestParam @Valid @NotEmpty String username,
			@RequestParam @Valid @NotEmpty String name,
			@RequestParam @Valid @NotEmpty String email) {
		boolean result = memberService.비밀번호찾기로_임시비밀번호_발급(username, name, email);
		if(result==true)
			return ResponseEntity.ok("임시비밀번호를 전송했습니다. 이메일을 확인해주세요");
		return ResponseEntity.status(409).body("입력하신 정보가 일치하지 않습니다.");
	}
	
	// 예외를 처리하는 컨트롤러
	@ExceptionHandler(ConstraintViolationException.class) 
	public ResponseEntity<?> CVEHandler(ConstraintViolationException e) {
		System.out.println(e.getMessage());
		return ResponseEntity.status(409).body(e.getMessage());
	}
}
