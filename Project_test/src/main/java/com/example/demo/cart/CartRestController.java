package com.example.demo.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.demo.cart.CartDto.CartUpdateRequest;
import com.example.demo.exception.FailException;
import com.example.demo.item.ItemDto;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Validated
// @PreAuthorize("isAuthenticated()")  // 로그인된 사용자만 접근 가능
@RestController
public class CartRestController {

    @Autowired
    private CartService cartService;

    // 장바구니에 상품 추가
    @PostMapping("/api/cart")
    public ResponseEntity<List<CartDto.Read>> addToCart(Long itemNo) {
        // 현재는 "winter_shop"을 하드코딩하여 사용 (테스트 중)
        String username = "winter_shop";  // 테스트용, 로그인 기능이 구현되면 아래 코드를 사용
        // String username = principal.getName(); // 로그인된 사용자의 username을 가져오기
        String imageUrl = "/default/images/";  // 기본 이미지 URL 설정
        List<CartDto.Read> result = cartService.addToCart(itemNo, username, imageUrl);  // imageUrl도 함께 전달
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/api/updateCart")
    public ResponseEntity<List<CartDto.Read>> updateCart(@RequestBody List<CartDto.Read> cartUpdates) {
        // 받은 데이터 출력 (디버깅용)
        for (CartDto.Read cartDto : cartUpdates) {
            if (cartDto.getItemNo() == null || cartDto.getCartEa() == null) {
                return ResponseEntity.badRequest().body(null); // ItemNo and CartEa cannot be null
            }
            System.out.println("Received itemNo: " + cartDto.getItemNo() + ", cartEa: " + cartDto.getCartEa());
        }

        // 수량 변경 처리
        List<CartDto.Read> updatedCart = cartService.updateCartEa("winter_shop", cartUpdates);

        // 수량 변경 결과와 갱신된 장바구니 상태 반환
        return ResponseEntity.ok(updatedCart);
    }



    
    // 장바구니 상품이 겹치는 경우 개수 증가
    @PostMapping("/api/cart/increase")
    public ResponseEntity<List<CartDto.Read>> increaseItem(@RequestBody Map<String, Long> payload) {
        // 현재는 "winter_shop"을 하드코딩하여 사용 (테스트 중)
        String username = "winter_shop";  // 테스트용, 로그인 기능이 구현되면 아래 코드를 사용
        // String username = principal.getName(); // 로그인된 사용자의 username을 가져오기
        Long itemNo = payload.get("itemNo");  // JSON에서 itemNo 가져오기
        String imageUrl = "/default/images/";  // 기본 이미지 URL 설정
        try {
            return ResponseEntity.ok(cartService.increase(itemNo, username, imageUrl));  // imageUrl도 함께 전달
        } catch (FailException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);  // 재고 부족시 409 상태 코드와 메시지 전달
        }
    }

    // 예외처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> CVEHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().findFirst().get().getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
