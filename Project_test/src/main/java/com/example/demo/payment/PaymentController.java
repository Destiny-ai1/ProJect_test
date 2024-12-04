package com.example.demo.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.exception.*;

import jakarta.validation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Controller
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private KakaoPayService kakaoPayService; // 카카오페이 서비스

    // KakaoPay 결제 준비 요청 처리 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/payment/ready")
    public ModelAndView readyPayment(@Valid PaymentDto.Create dto, BindingResult br) {
        logger.info("PaymentDto.Create: " + dto);

        if (br.hasErrors()) {
            logger.error("Validation errors: " + br.getAllErrors());
            throw new IllegalArgumentException("입력 데이터에 오류가 있습니다: " + br.getAllErrors().toString());
        }

        try {
            String redirectUrl = kakaoPayService.kakaoPayReady(dto); // 카카오페이 결제 준비
            logger.info("카카오페이 결제 URL: " + redirectUrl);
            return new ModelAndView("redirect:" + redirectUrl); // 결제 페이지로 리다이렉트
        } catch (FailException e) {
            return new ModelAndView("redirect:/payment/fail").addObject("error", e.getMessage());
        }
    }

    // 결제 승인 요청 처리 메소드 (KakaoPay)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/approve")
    public ModelAndView approvePayment(@RequestParam String pg_token, @RequestParam Long orderNo) {
        try {
            kakaoPayService.kakaoPayApprove(pg_token, orderNo);
            logger.info("결제 승인 완료 - OrderNo: {}", orderNo);
            return new ModelAndView("redirect:/payment/success").addObject("orderNo", orderNo);
        } catch (FailException e) {
            return new ModelAndView("redirect:/payment/fail").addObject("error", e.getMessage());
        }
    }

    // 결제 성공 페이지 라우트
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/success")
    public ModelAndView paymentSuccess(@RequestParam Long orderNo) {
        logger.info("결제 성공 - OrderNo: {}", orderNo);
        ModelAndView modelAndView = new ModelAndView("payment/success");
        modelAndView.addObject("message", "결제가 성공적으로 완료되었습니다!");
        modelAndView.addObject("orderNo", orderNo);
        return modelAndView;
    }

    // 결제 실패 페이지 라우트
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/fail")
    public ModelAndView paymentFail(@RequestParam(required = false) String error) {
        logger.error("결제 실패: {}", error);
        ModelAndView modelAndView = new ModelAndView("payment/fail");
        modelAndView.addObject("message", "결제가 실패하였습니다.");
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    // 예외 처리 핸들러
    @ExceptionHandler(FailException.class)
    public ModelAndView handleFailException(FailException e) {
        return new ModelAndView("error/error").addObject("message", e.getMessage()); // 에러 메시지를 에러 페이지에 전달
    }
}
