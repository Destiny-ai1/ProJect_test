package com.example.demo.cart;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

@Validated
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    // 장바구니 상품 목록
    // principal 추가하기
    // @PreAuthorize("isAuthenticated()")
    @GetMapping("/cart/list")
    public ModelAndView view() {
        String username = "winter_shop";
        String imageUrl = "/default/images/";  // 기본 이미지 URL 설정
        List<CartDto.Read> result = cartService.read(username, imageUrl);  // imageUrl도 함께 전달
        return new ModelAndView("cart/list").addObject("result", result);
    }
    
    // 장바구니 항목 삭제 요청 처리 메소드
    // @PreAuthorize("isAuthenticated()")
    @PostMapping("/cart/delete")
    public String deleteCartItems(@RequestParam("itemNos") String itemNos) {
        // itemNos 값 확인 (테스트용 출력)
        System.out.println("Received itemNos: " + itemNos);
        // itemNos를 쉼표(,) 기준으로 분리하여 List<Long>으로 변환
        List<Long> itemNoList = Arrays.stream(itemNos.split(","))
                                      .map(Long::parseLong)
                                      .collect(Collectors.toList());
        // 하드코딩된 username (나중에 로그인 기능에서 principal로 변경 예정)
        String username = "winter_shop";  // 현재 사용자의 username을 하드코딩 (후에 principal로 변경 예정)
        try {
            // 삭제 메서드 호출
            cartService.deleteCartItems(itemNoList, username);
            // 성공 메시지 후 /cart/list로 리다이렉트
            return "redirect:/cart/list";
        } catch (Exception e) {
            // 예외 발생 시, 오류 메시지를 모델에 추가하여 반환 (장바구니 목록 페이지로)
            return "redirect:/cart/list?error=" + e.getMessage();
        }
    }


    // 주문 확인
}

