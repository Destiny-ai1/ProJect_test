package com.example.demo.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.cart.CartDao;
import com.example.demo.cart.CartDto;
import com.example.demo.cart.CartService;
import com.example.demo.exception.*;
import com.example.demo.item.ItemDto;
import com.example.demo.order.OrderService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Controller
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private KakaoPayService kakaoPayService; // 카카오페이 서비스
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

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

  /// 결제 승인 요청 처리 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payment/approve")
    public ModelAndView approvePayment(@RequestParam String pg_token, @RequestParam Long orderNo,
            Principal principal, HttpSession session, @RequestParam(required = false) String imageUrl) {
        try {
            // 결제 승인 처리
            kakaoPayService.kakaoPayApprove(pg_token, orderNo);

            // 로그인한 사용자 확인
            String username = principal.getName();

            // 기본 이미지 경로 설정 (imageUrl이 null 또는 비어있을 경우)
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = "/default/images/";
            }

            // 장바구니 항목 조회
            List<CartDto.Read> cartItems = cartService.getCartList(username, imageUrl, principal);
            if (cartItems == null || cartItems.isEmpty()) {
                throw new FailException("장바구니에 삭제할 항목이 없습니다.");
            }

            // 삭제할 항목 필터링 (예: 모든 항목 삭제)
            List<ItemDto.ItemDeleteDTO> itemsToDelete = cartItems.stream()
                    .map(item -> new ItemDto.ItemDeleteDTO(item.getItemNo(), item.getItemSize()))
                    .collect(Collectors.toList());

            if (!itemsToDelete.isEmpty()) {
                // 장바구니 항목 삭제
                cartService.deleteCartItems(itemsToDelete, username);
            }
            
            // 결제 완료 후 주문 및 결제 정보 업데이트
            Long actPayment = 10000L; // 예시: 결제 총액 (실제 값은 카카오페이로부터 받아야 함)
            Long usedPoint = 0L; // 예시: 사용된 포인트 (실제 값은 카카오페이로부터 받아야 함)

            // 주문 정보 업데이트
            orderService.updateOrderPayment(orderNo, actPayment, usedPoint, session);

            // 로깅
            logger.info("결제 승인 완료 - OrderNo: {}", orderNo);

            // 결제 성공 페이지로 리다이렉트
            return new ModelAndView("redirect:/payment/success").addObject("orderNo", orderNo);
        } catch (FailException e) {
            // 결제 실패 페이지로 리다이렉트
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
