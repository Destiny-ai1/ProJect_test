package com.example.demo.board;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.exception.FailException;

import jakarta.validation.Valid;


@Controller
public class BoardController {
	@Autowired
	public BoardService boardService;
	
	//메인 페이지로 보내기
	@GetMapping("/")
	public ModelAndView Main() {
		return new ModelAndView("index");
	}
	
	//글 작성하기위해 로그인한 사람만 글작성으로 간다
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/board/write")
	public ModelAndView ckEditor사용() {
		return new ModelAndView("board/write");
	}
	
	//글의 유효성을 검사하고 게시글을 데이터로 저장하고, 게시글을쓴데로 보낸다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/board/write")
	public ModelAndView ckEditor작성(@Valid BoardDto.Create dto, BindingResult br, Principal principal) {
		Long bno = boardService.write(dto, principal.getName());
		return new ModelAndView("redirect:/board/read?bno=" + bno);
	}
	
	//게시글을 읽을때
	@GetMapping("/board/read")
	public ModelAndView read(Long bno, Principal principal) {
		String loginId = principal==null? null : principal.getName();		//로그인하지않았으면 null로 인식
		BoardDto.BoardRead dto = boardService.Board_senter(bno, loginId);
		return new ModelAndView("board/read").addObject("result", dto);
	}
	
	//게시글목록 페이지로 이동하며 해당페이지의 번호에 맞는 게시글인 확인하고 데이터를 전달
	@GetMapping("/board/list")
	public ModelAndView list(@RequestParam(defaultValue="1") Integer pageno) {
		return new ModelAndView("board/list").addObject("result", boardService.findall(pageno));
	}
	
	//게시글을 수정할때 기존 데이터를 그대로 가져온다
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/board/update")
	public ModelAndView update(Long bno) {
		BoardDto.BoardRead dto = boardService.Board_senter(bno, null);
		return new ModelAndView("board/update").addObject("result", dto);
	}
	
	//게시글을 수정하고 새롭게 업데이트하고난후 그 게시글로 가게한다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/board/update")
	public ModelAndView update(@Valid BoardDto.update dto, BindingResult br, Principal principal) {
		System.out.println(dto);
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
