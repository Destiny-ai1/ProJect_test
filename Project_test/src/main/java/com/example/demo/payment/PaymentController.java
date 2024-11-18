package com.example.demo.payment;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import com.example.demo.exception.FailException;

@Controller
public class PaymentController { // 결제 관련 요청을 처리하는 컨트롤러 클래스

    @Autowired
    private PaymentService paymentService; // 결제 서비스

    // 결제 추가 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/add")
    public ModelAndView addPaymentForm() {
        return new ModelAndView("payment/add"); // 결제 추가 폼 뷰 반환
    }

    // 결제 추가 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/payment/add")
    public ModelAndView addPayment(@Valid PaymentDto.Create dto, BindingResult br) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return new ModelAndView("payment/add").addObject("errors", br.getAllErrors()); // 오류 메시지를 포함한 뷰 반환
        }
        paymentService.addPayment(dto); // 결제 정보 추가
        return new ModelAndView("redirect:/list"); // 결제 목록으로 리디렉션
    }

    // 결제 목록 조회 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/list")
    public ModelAndView listPayments() {
        List<PaymentDto.Read> payments = paymentService.getAllPayments(); // 모든 결제 정보 조회
        return new ModelAndView("payment/list").addObject("result", payments); // 결제 목록 뷰에 데이터 전달
    }

    // 결제 정보 수정 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/update")
    public ModelAndView updatePaymentForm(Long orderNo) {
        PaymentDto.Read payment = paymentService.getPayment(orderNo); // 주문 번호로 결제 정보 조회
        return new ModelAndView("payment/update").addObject("result", payment); // 수정 폼 뷰 반환
    }

    // 결제 정보 수정 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/payment/update")
    public ModelAndView updatePayment(@Valid PaymentDto.Update dto, BindingResult br) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return new ModelAndView("payment/update").addObject("errors", br.getAllErrors()); // 오류 메시지를 포함한 뷰 반환
        }
        paymentService.updatePayment(dto); // 결제 정보 업데이트
        return new ModelAndView("redirect:/list"); // 결제 목록으로 리디렉션
    }

    // 결제 정보 삭제 요청 처리 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/delete")
    public ModelAndView deletePayment(@RequestParam Long orderNo) {
        paymentService.removePayment(orderNo); // 결제 정보 삭제
        return new ModelAndView("redirect:/list"); // 결제 목록으로 리디렉션
    }

    // 결제 확인 페이지를 반환하는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/confirm")
    public String paymentPage() {
        return "payment/confirm"; // 결제 페이지 뷰 반환
    }

    // 결제 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/payment/confirm")
    public String confirmPayment(@Valid PaymentDto.Create dto, BindingResult br, Model model) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return "payment/confirm"; // 오류 발생 시 다시 해당 폼을 반환
        }
        paymentService.addPayment(dto); // 결제 정보 추가

        // 결제 완료 정보를 모델에 추가
        model.addAttribute("orderNo", dto.getOrderNo());
        model.addAttribute("totalAmount", dto.getPointAdd()); // 예시로 포인트 사용 금액을 추가함

        return "payment/confirm"; // 결제 완료 페이지로 이동
    }

    // 예외 처리 핸들러
    @ExceptionHandler(FailException.class)
    public ModelAndView handleFailException(FailException e) {
        return new ModelAndView("error/error").addObject("message", e.getMessage()); // 에러 메시지를 에러 페이지에 전달
    }
}
