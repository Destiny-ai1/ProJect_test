package com.example.demo.review;

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
public class ReviewController { // 리뷰 관련 요청을 처리하는 컨트롤러 클래스

    @Autowired
    private ReviewService reviewService; // 리뷰 서비스

    // 리뷰 추가 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/review/add")
    public ModelAndView addReviewForm(Long orderNo) {
        return new ModelAndView("review/add").addObject("orderNo", orderNo); // 리뷰 추가 폼 뷰 반환, 주문 번호 전달
    }

    // 리뷰 추가 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/review/add")
    public ModelAndView addReview(@Valid ReviewDto.Create dto, BindingResult br, Principal principal) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return new ModelAndView("review/add").addObject("errors", br.getAllErrors()); // 오류 메시지를 포함한 뷰 반환
        }
        dto.setUsername(principal.getName()); // 현재 사용자 이름 설정
        reviewService.addReview(dto); // 리뷰 추가
        return new ModelAndView("redirect:/order/detail?orderNo=" + dto.getOrderNo()); // 주문 상세 페이지로 리디렉션
    }

    // 특정 주문의 리뷰 목록 조회 메소드
    @GetMapping("/review/list")
    public ModelAndView listReviews(@RequestParam Long orderNo) {
        List<ReviewDto.Read> reviews = reviewService.getReviewsByOrderNo(orderNo); // 특정 주문의 모든 리뷰 조회
        return new ModelAndView("review/list").addObject("result", reviews).addObject("orderNo", orderNo); // 리뷰 목록 뷰에 데이터 전달
    }

    // 리뷰 수정 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/review/update")
    public ModelAndView updateReviewForm(Long reviewNo) {
        ReviewDto.Read review = reviewService.getReviewsByOrderNo(reviewNo).stream()
                .filter(r -> r.getReviewNo().equals(reviewNo))
                .findFirst().orElseThrow(() -> new FailException("리뷰 정보를 찾을 수 없습니다")); // 특정 리뷰 찾기
        return new ModelAndView("review/update").addObject("result", review); // 수정 폼 뷰 반환
    }

    // 리뷰 수정 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/review/update")
    public ModelAndView updateReview(@Valid ReviewDto.Update dto, BindingResult br) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return new ModelAndView("review/update").addObject("errors", br.getAllErrors()); // 오류 메시지를 포함한 뷰 반환
        }
        reviewService.updateReview(dto); // 리뷰 정보 업데이트
        return new ModelAndView("redirect:/order/detail?orderNo=" + dto.getReviewNo()); // 수정된 리뷰가 있는 주문 상세 페이지로 리디렉션
    }

    // 리뷰 삭제 요청 처리 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/review/delete")
    public ModelAndView deleteReview(@RequestParam Long reviewNo) {
        reviewService.removeReview(reviewNo); // 리뷰 삭제
        return new ModelAndView("redirect:/order/detail?orderNo=" + reviewNo); // 삭제된 리뷰가 있는 주문 상세 페이지로 리디렉션
    }

    // 예외 처리 핸들러
    @ExceptionHandler(FailException.class)
    public ModelAndView handleFailException(FailException e) {
        return new ModelAndView("error/error").addObject("message", e.getMessage()); // 에러 메시지를 에러 페이지에 전달
    }
}