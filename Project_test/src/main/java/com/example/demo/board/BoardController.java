package com.example.demo.board;

import java.security.Principal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.exception.FailException;

import com.example.demo.member.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
public class BoardController {
	@Autowired
	public BoardService boardService;

	@Autowired
	public MemberService memberService;
	
	//메인 페이지로 보내기
	@GetMapping("/")
	public ModelAndView Main() {
		return new ModelAndView("index");
	}
	
	//게시글 작성
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/board/write")
	public ModelAndView 글작성GET(@RequestParam Integer cno) { // 추가: cno를 요청 파라미터로 받음
		ModelAndView mav = new ModelAndView("board/write");
        String boardType = getBoardType(cno); // 공지사항 또는 Q&A 구분
        mav.addObject("boardType", boardType);
        mav.addObject("cno", cno); // 카테고리 번호 전달
        return mav;
	}

	// 카테고리 번호를 기반으로 글 종류(공지사항, Q&A) 반환
	private String getBoardType(Integer cno) {
	    switch (cno) {
	        case 1001:
	            return "공지사항";
	        case 1002:
	            return "QnA";
	        default:
	            throw new IllegalArgumentException("잘못된 카테고리 번호입니다: " + cno);
	    }
	}
	
	//글의 유효성을 검사하고 게시글을 데이터로 저장하고, 게시글을쓴데로 보낸다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/board/write")
	public ModelAndView 글작성POST(@Valid BoardDto.Create dto, BindingResult br, Principal principal, HttpServletRequest request) {
	    if (br.hasErrors()) {
	        ModelAndView mav = new ModelAndView("board/write");
	        mav.addObject("errors", br.getAllErrors());
	        mav.addObject("cno", dto.getCno());
	        return mav;
	    }
	    
	    if (dto.isSecretBoard()) {
	        if (dto.getPassword() == null || !dto.getPassword().matches("\\d{1,4}")) {
	            ModelAndView mav = new ModelAndView("board/write");
	            mav.addObject("errorMessage", "비밀글은 4자리까지 숫자 비밀번호를 입력해야 합니다.");
	            mav.addObject("cno", dto.getCno());
	            return mav;
	        }
	    }

	    Long bno = boardService.write(dto, principal.getName());
	    return new ModelAndView("redirect:/board/read?bno=" + bno);
	}
	
	//게시글을 읽을때 get
	@GetMapping("/board/read")
    public ModelAndView read(Long bno, @RequestParam(required = false) String password, Principal principal) {
		String loginId = (principal != null) ? principal.getName() : null;
	    try {
	        // 서비스에서 게시글 데이터를 가져옴
	        BoardDto.BoardRead dto = boardService.Board_senter(bno, loginId, password);
	        // 로그인 사용자의 이름 가져오기
	        String userName = memberService.findNameUsingUserDetails(loginId).orElse(null);
	        // 작성자 확인: 로그인 사용자 이름과 게시글 작성자 이름 비교
	        boolean isWriter = dto.getWriter().equals(userName);
	        
	        // 뷰에 전달
	        return new ModelAndView("board/read")
	                .addObject("result", dto)
	                .addObject("userName", userName) // 로그인 사용자 이름
	                .addObject("isWriter", isWriter); // 비교 결과
	    } catch (FailException e) {
	        // 비밀번호 검증 실패 시 비밀번호 입력 창으로 리다이렉트
	        ModelAndView mav = new ModelAndView("board/password");
	        mav.addObject("bno", bno); // 게시글 번호 전달
	        mav.addObject("message", e.getMessage()); // 실패 메시지 전달
	        return mav;
	    }
	}
	
	//게시글 읽을때 post
	@PostMapping("board/read")
	public ModelAndView readPost(@RequestParam Long bno, @RequestParam(required = false) String password, Principal principal) {
	    String loginId = (principal != null) ? principal.getName() : null;
	    try {
	        BoardDto.BoardRead dto = boardService.Board_senter(bno, loginId, password);
	        return new ModelAndView("board/read").addObject("result", dto);
	    } catch (FailException e) {
	        ModelAndView mav = new ModelAndView("board/password");
	        mav.addObject("bno", bno);
	        mav.addObject("message", e.getMessage());
	        return mav;
	    }
	}
	
	//비밀글에대한 비밀번호 확인창
	@PostMapping("/board/password")
    public ModelAndView passwordCheck(@RequestParam Long bno, @RequestParam String password, Principal principal) {
		return readPost(bno, password, principal);
    }
	
	//게시글목록 페이지로 이동하며 해당페이지의 번호에 맞는 게시글인 확인하고 데이터를 전달
	@GetMapping("/board/list")
	public ModelAndView list(@RequestParam(defaultValue="1") Integer pageno, String cno) {
		BoardDto.BoardPage result = boardService.findall(pageno, cno);
		
		ModelAndView mav = new ModelAndView("board/list");
	    mav.addObject("result", result);  		// <<수정: notices와 qas를 나누지 않고 일관된 객체를 추가>>
	    mav.addObject("cno", cno);  			// <<수정: cno 값을 추가하여 뷰에서 사용할 수 있도록 함>>
	    return mav;
	}
	
	//게시글을 수정할때 기존 데이터를 그대로 가져온다
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/board/update/{bno}")
    public ModelAndView updateForm(@PathVariable Long bno, Principal principal) {
        String loginId = (principal != null) ? principal.getName() : null;
        
        // 로그인 사용자의 이름 가져오기
        String userName = memberService.findNameUsingUserDetails(loginId).orElse(null);
        
        // 게시글 정보 가져오기
        BoardDto.BoardRead dto = boardService.Board_senter(bno, loginId, null);

        return new ModelAndView("board/update")
            .addObject("result", dto) // 게시글 정보 전달
            .addObject("userName", userName); // 로그인 사용자 이름 전달
    }
	
	//게시글을 수정하고 새롭게 업데이트하고난후 그 게시글로 가게한다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/board/update")
	public ModelAndView update(@Valid BoardDto.update dto, BindingResult br, Principal principal) {
		boardService.Board_update(dto, principal.getName());
		return new ModelAndView("redirect:/board/read?bno="+dto.getBno());
	}
	
	//게시글을 삭제하고 기존에 있던게시글 목록 페이지로 보낸다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/board/delete")
	public ModelAndView delete(long bno, Principal principal) {
		boardService.Board_delete(bno, principal.getName());
		return new ModelAndView("redirect:/");
	}
	
	@ExceptionHandler(FailException.class)
	public ModelAndView jobFailExceptionHandler(FailException e) {
		return new ModelAndView("error/error").addObject("message", e.getMessage());
	}

}