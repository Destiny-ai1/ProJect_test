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
    public ResponseEntity<List<CartDto.Read>> addToCart(
            @RequestParam Long itemNo,          // 상품 번호
            @RequestParam String itemSize) {    // 선택한 사이즈

        // 현재는 "winter_shop"을 하드코딩하여 사용 (테스트 중)
        String username = "winter_shop";  // 테스트용, 로그인 기능이 구현되면 아래 코드를 사용
        // String username = principal.getName(); // 로그인된 사용자의 username을 가져오기
        
        String imageUrl = "/default/images/";  // 기본 이미지 URL 설정

        // 장바구니에 상품 추가
        List<CartDto.Read> result = cartService.addToCart(itemNo, username, itemSize, imageUrl);

        return ResponseEntity.ok(result);
    }

    // 장바구니 상품 개수 변경
    @PostMapping("/api/updateCart")
    public ResponseEntity<List<CartDto.Read>> updateCart(@RequestBody List<CartDto.Read> cartUpdates) {
        // 수량 변경 요청이 정상적으로 들어왔는지 확인
        System.out.println("Received cartUpdates: " + cartUpdates);

        // 수량 업데이트 처리
        try {
            List<CartDto.Read> updatedCart = cartService.updateCartEa("winter_shop", cartUpdates);

            // 수량 변경 후 갱신된 장바구니 목록 반환
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            System.out.println("Error during cart update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // 장바구니 상품이 겹치는 경우 개수 증가
    @PostMapping("/api/cart/increase")
    public ResponseEntity<List<CartDto.Read>> increaseItem(@RequestBody Map<String, Object> payload) {
        // 현재는 "winter_shop"을 하드코딩하여 사용 (테스트 중)
        String username = "winter_shop";  // 테스트용, 로그인 기능이 구현되면 아래 코드를 사용
        // String username = principal.getName(); // 로그인된 사용자의 username을 가져오기
        Long itemNo = (Long) payload.get("itemNo");  // JSON에서 itemNo 가져오기
        String itemSize = (String) payload.get("itemSize");  // JSON에서 itemSize 가져오기
        String imageUrl = "/default/images/";  // 기본 이미지 URL 설정

        try {
            // 장바구니 상품 수 증가 서비스 호출
            List<CartDto.Read> updatedCart = cartService.increase(itemNo, username, imageUrl, itemSize);
            
            // 성공적으로 장바구니 업데이트 후, 변경된 장바구니 목록 반환
            return ResponseEntity.ok(updatedCart);
        } catch (FailException e) {
            // 재고 부족시 409 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body(null);  // 필요시 예외 메시지 반환 가능
        } catch (Exception e) {
            // 예기치 않은 오류 발생 시 500 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);  // 필요시 예외 메시지 반환 가능
        }
    }

    // 예외처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> CVEHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().findFirst().get().getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
