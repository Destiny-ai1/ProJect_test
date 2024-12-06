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

import com.example.demo.item.ItemDto;

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
    public String deleteCartItems(@RequestBody List<ItemDto.ItemDeleteDTO> items) {
        // items에는 클라이언트에서 보낸 itemNo, itemSize가 담긴 리스트가 넘어옵니다.
        String username = "winter_shop";  // 하드코딩된 사용자는 나중에 로그인 사용자로 변경 예정

        try {
            // 삭제 메서드 호출
            cartService.deleteCartItems(items, username);  // 서비스 계층에서 처리
            // 삭제 성공 후 장바구니 갱신 결과 반환 (삭제된 항목 제외한 장바구니 데이터)
            return "redirect:/cart/list";  // 결과에 따라 장바구니 페이지로 리다이렉트
        } catch (Exception e) {
            // 예외 발생 시, 오류 메시지 반환
            return "redirect:/cart/list?error=" + e.getMessage();
        }
    }
    // 주문 확인
}

