package com.example.demo.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
 // 장바구니에 상품 추가
    @PostMapping("/api/cart/addToCart")
    public ResponseEntity<List<CartDto.Read>> addToCart(
            @RequestParam Long itemNo,          // 상품 번호
            @RequestParam String itemSize) {    // 선택한 사이즈

        String username = "winter_shop";  // 테스트용, 로그인 기능이 구현되면 아래 코드를 사용
        // String username = principal.getName(); // 로그인된 사용자의 username을 가져오기

        String imageUrl = "/default/images/";  // 기본 이미지 URL 설정

        try {
            // 장바구니에 상품 추가
            List<CartDto.Read> result = cartService.addToCart(itemNo, username, itemSize, imageUrl);
            return ResponseEntity.ok(result);
        } catch (FailException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);  // 409 상태 코드 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // 500 상태 코드 반환
        }
    }


    // 장바구니 상품 개수 변경
    // 로그인 구현 후 principal 추가하기
    @PostMapping("/api/cart/updateCart")
    public ResponseEntity<List<CartDto.Read>> updateCart(@RequestBody List<CartDto.Read> cartUpdates) {
        
    	// 테스트용 사용자명 (로그인 기능이 구현되면 실제 사용자 이름으로 변경)
        String username = "winter_shop";  // 임시로 하드코딩된 사용자 이름
        // 실제로는 로그인된 사용자의 username을 가져오는 방식으로 변경
        // String username = principal.getName();  // 로그인된 사용자의 username을 가져오기

        // 수량 변경 요청이 정상적으로 들어왔는지 확인
        System.out.println("Received cartUpdates for user " + username + ": " + cartUpdates);

        try {
            // 수량 업데이트 처리
            List<CartDto.Read> updatedCart = cartService.updateCartEa(username, cartUpdates);

            // 수량 변경 후 갱신된 장바구니 목록 반환
            return ResponseEntity.ok(updatedCart);
        } catch (FailException e) {
            // 재고 부족 등의 예외는 BAD_REQUEST로 처리
            System.out.println("Error during cart update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            // 일반적인 예외는 INTERNAL_SERVER_ERROR로 처리
            System.out.println("Error during cart update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 예외처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> CVEHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().findFirst().get().getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
