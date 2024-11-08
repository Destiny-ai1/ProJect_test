package com.example.demo.cart;

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
public class CartController { // 장바구니 관련 요청을 처리하는 컨트롤러 클래스

    @Autowired
    private CartService cartService; // 장바구니 서비스

    // 장바구니에 상품 추가 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cart/add")
    public ModelAndView addToCartForm() {
        return new ModelAndView("cart/add"); // 장바구니 추가 폼 뷰 반환
    }

    // 장바구니에 상품 추가 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cart/add")
    public ModelAndView addToCart(@Valid CartDto.Create dto, BindingResult br, Principal principal) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return new ModelAndView("cart/add").addObject("errors", br.getAllErrors()); // 오류 메시지를 포함한 뷰 반환
        }
        cartService.addToCart(dto); // 장바구니에 상품 추가
        return new ModelAndView("redirect:/cart/list"); // 장바구니 목록으로 리디렉션
    }

    // 장바구니 조회 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cart/list")
    public ModelAndView listCartItems(Principal principal) {
        String username = principal.getName(); // 현재 사용자의 이름(아이디) 가져오기
        List<CartDto.Read> cartItems = cartService.getCartItems(username); // 사용자의 장바구니 항목 조회
        return new ModelAndView("cart/list").addObject("result", cartItems); // 장바구니 목록 뷰에 데이터 전달
    }

    // 장바구니 항목 수정 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cart/update")
    public ModelAndView updateCartItemForm(Long itemNo, Principal principal) {
        String username = principal.getName(); // 현재 사용자의 이름(아이디) 가져오기
        CartDto.Read cartItem = cartService.getCartItems(username).stream()
                .filter(item -> item.getItemNo().equals(itemNo))
                .findFirst().orElseThrow(() -> new FailException("장바구니 항목을 찾을 수 없습니다")); // 특정 항목 찾기
        return new ModelAndView("cart/update").addObject("result", cartItem); // 수정 폼 뷰 반환
    }

    // 장바구니 항목 수정 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cart/update")
    public ModelAndView updateCartItem(@Valid CartDto.Update dto, BindingResult br) {
        if (br.hasErrors()) { // 입력 데이터에 오류가 있는 경우
            return new ModelAndView("cart/update").addObject("errors", br.getAllErrors()); // 오류 메시지를 포함한 뷰 반환
        }
        cartService.updateCartItem(dto); // 장바구니 항목 업데이트
        return new ModelAndView("redirect:/cart/list"); // 장바구니 목록으로 리디렉션
    }

    // 장바구니 항목 삭제 요청 처리 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/cart/delete")
    public ModelAndView deleteCartItem(@RequestParam Long itemNo, Principal principal) {
        String username = principal.getName(); // 현재 사용자의 이름(아이디) 가져오기
        cartService.removeCartItem(itemNo, username); // 장바구니 항목 삭제
        return new ModelAndView("redirect:/cart/list"); // 장바구니 목록으로 리디렉션
    }

    // 예외 처리 핸들러
    @ExceptionHandler(FailException.class)
    public ModelAndView failExceptionHandler(FailException e) {
        return new ModelAndView("error/error").addObject("message", e.getMessage()); // 에러 메시지를 에러 페이지에 전달
    }
}
