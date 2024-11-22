package com.example.demo.member;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.enums.PasswordChange;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
public class MemberController {
	@Autowired
	private MemberService memberService;
	
	//회원가입 페이지로 이동
	@PreAuthorize("isAnonymous()")
	@GetMapping("/member/join")
	public ModelAndView join() 	{
		return new ModelAndView("member/join");
	}
	
	//회원가입처리
	@PreAuthorize("isAnonymous()")
	@PostMapping("/member/join")
	public ModelAndView join(@ModelAttribute @Valid MemberDto.Member_Create dto, BindingResult br) {
		memberService.join(dto);
		return new ModelAndView("redirect:/member/login");
	}
	
	//회원찾기 아이디,비밀번호 찾기페이지로 이동
	@PreAuthorize("isAnonymous()")
	@GetMapping("/member/find")
	public ModelAndView find() {
		return new ModelAndView("member/find");
	}
	
	//로그인 페이지로 이동
	@PreAuthorize("isAnonymous()")
	@GetMapping("/member/login")
	public ModelAndView login() {
		return new ModelAndView("member/login");
	}
	
	//임시 비밀번호 발급후 ,임시비밀번호로 로그인했으면 비밀번호 변경 페이지로 보낸다
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/member/update-password")
	public ModelAndView updatePassword(HttpSession session) {
		// 1회성 메시지를 출력하는 세션
		if(session.getAttribute("message")!=null) {
			String message = (String)session.getAttribute("message");
			session.removeAttribute("message");
			return new ModelAndView("member/update-password").addObject("message", message);
		}
		return new ModelAndView("member/update-password");
	}
	
	//비밀번호 변경 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/member/update-password")
	public ModelAndView updatePassword(@Valid MemberDto.Member_update dto, BindingResult br, 
			Principal principal, RedirectAttributes ra) {
		PasswordChange result = memberService.updatePassword(dto, principal.getName());
		if(result==PasswordChange.Success)
			return new ModelAndView("redirect:/");
		else if(result==PasswordChange.Password_Check_Fail) {
			ra.addFlashAttribute("message", "비밀번호를 확인하지 못했습니다");
			return new ModelAndView("redirect:/member/update-password");
		} else {
			ra.addFlashAttribute("message", "새비밀번호와 기존비밀번호는 달라야 합니다");
			return new ModelAndView("redirect:/member/update-password");
		}
	}
	
	//내정보 보기위해 비밀번호를 확인하는 페이지로이동
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/member/check-password")
	public ModelAndView checkPassword(HttpSession session) {
	    if (session.getAttribute("checkPassword") != null)
	        return new ModelAndView("redirect:/member/readme");
	    return new ModelAndView("member/check-password");
	}
	
	//비밀번호 확인하는 페이지에서 비밀번호 확인하기
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/member/check-password")
	public ModelAndView checkPassword(String password, Principal principal, HttpSession session, RedirectAttributes ra) {
		// 비밀번호 확인
	    boolean result = memberService.비밀번호확인(password, principal.getName());
	    if (result) {
	        session.setAttribute("checkPassword", true);
	        return new ModelAndView("redirect:/member/readme");
	    }

	    // 실패 시 메시지 추가
	    ra.addFlashAttribute("message", "비밀번호를 틀렸습니다.");
	    return new ModelAndView("redirect:/member/check-password");
	}

	
	//비밀번호 확인이 됬으면 내정보페이지로 보내기
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/member/readme")
	public ModelAndView readme(HttpSession session,Principal principal) {
		//세션에 저장된값확인
		if (session.getAttribute("checkPassword") == null) {
	        return new ModelAndView("redirect:/member/check-password");
	    }
	    // 서비스에서 비밀번호 확인 후 정보 조회
		MemberDto.Member_Read dto = memberService.내정보보기(principal.getName()); 		// 비밀번호 입력받은 값
	    return new ModelAndView("member/readme").addObject("result", dto);
	}
	
	//탈퇴시 탈퇴 처리하는 페이지로 보낸다
	@GetMapping("/member/delete")
	public ModelAndView delete_page() {
		return new ModelAndView("member/delete");
	}
	
	//탈퇴하는 세션을 가져와서 탈퇴시킨다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/member/withdraw")
	public ModelAndView Member_delete(HttpSession session, Principal principal) {
		memberService.delete(principal.getName());
		session.invalidate();
		return new ModelAndView("redirect:/");
	}
	
	//회원 정보 수정 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/member/update")
	public ModelAndView update(@Valid MemberDto.Member_update dto, BindingResult br, Principal principal) {
		String loginId = principal.getName();
		memberService.member_update(dto,loginId);
		return new ModelAndView("redirect:/member/readme");
	}
	
	//유효성 검사 실패시 예외처리
	@ExceptionHandler(ConstraintViolationException.class)
	public ModelAndView cveHandler() {
		return new ModelAndView("error/error").addObject("message", "잘못된 작업입니다.");
	}
}
