package com.example.demo.card;

import java.security.Principal;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.exception.FailException;

import jakarta.validation.Valid;

@Controller
public class CardController { // 카드 관련 요청을 처리하는 컨트롤러 클래스

    @Autowired
    private CardService cardService; // 카드 서비스

    // 카드 추가 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/add")
    public ModelAndView addCardForm() {
        return new ModelAndView("card/add"); // 카드 추가 폼 뷰 반환
    }

    // 카드 추가 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/card/add")
    public ModelAndView addCard(@Valid CardDto.Create dto, BindingResult br, Principal principal) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return new ModelAndView("card/add").addObject("errors", br.getAllErrors()); // 오류 메시지를 포함한 뷰 반환
        }
        cardService.addCard(dto); // 카드 정보 추가
        return new ModelAndView("redirect:/card/list"); // 카드 목록으로 리디렉션
    }

    // 카드 목록 조회 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/list")
    public ModelAndView listCards(Principal principal) {
        String memberUsername = principal.getName(); // 현재 사용자의 이름(아이디) 가져오기
        List<CardDto.Read> cards = cardService.getCards(memberUsername); // 회원의 카드 정보 조회
        return new ModelAndView("card/list").addObject("result", cards); // 카드 목록 뷰에 데이터 전달
    }

    // 카드 정보 수정 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/update")
    public ModelAndView updateCardForm(Long cardNo, Principal principal) {
        String memberUsername = principal.getName(); // 현재 사용자의 이름(아이디) 가져오기
        CardDto.Read card = cardService.getCards(memberUsername).stream()
                .filter(c -> c.getCardNo().equals(cardNo))
                .findFirst().orElseThrow(() -> new FailException("카드 정보를 찾을 수 없습니다")); // 특정 카드 찾기
        return new ModelAndView("card/update").addObject("result", card); // 수정 폼 뷰 반환
    }

    // 카드 정보 수정 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/card/update")
    public ModelAndView updateCard(@Valid CardDto.Update dto, BindingResult br) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return new ModelAndView("card/update").addObject("errors", br.getAllErrors()); // 오류 메시지를 포함한 뷰 반환
        }
        cardService.updateCard(dto); // 카드 정보 업데이트
        return new ModelAndView("redirect:/card/list"); // 카드 목록으로 리디렉션
    }

    // 카드 정보 삭제 요청 처리 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/delete")
    public ModelAndView deleteCard(@RequestParam Long cardNo) {
        cardService.removeCard(cardNo); // 카드 정보 삭제
        return new ModelAndView("redirect:/card/list"); // 카드 목록으로 리디렉션
    }

    // 예외 처리 핸들러
    @ExceptionHandler(FailException.class)
    public ModelAndView handleFailException(FailException e) {
        return new ModelAndView("error/error").addObject("message", e.getMessage()); // 에러 메시지를 에러 페이지에 전달
    }
}