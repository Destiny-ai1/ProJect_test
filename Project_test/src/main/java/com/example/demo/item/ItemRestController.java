package com.example.demo.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemRestController {

    @Autowired
    private ItemService itemService;

    // 동적으로 상품 번호를 받아서 해당 상품 삭제
    @DeleteMapping("/item/delete/{itemNo}")
    public String deleteItem(@PathVariable Long itemNo) {
        boolean isDeleted = itemService.deleteItem(itemNo);

        if (isDeleted) {
            return "success";
        } else {
            return "fail";
        }
    }
    
    @PatchMapping("/item/{itemNo}/update-price")
    public ResponseEntity<String> updatePrice(@PathVariable Long itemNo, @RequestBody ItemDto.price_update dto) {
        // 1. 하드코딩된 username 사용 (테스트용)
        String username = "winter_shop";  // 하드코딩된 username
        
        // 2. 추후 동적으로 처리하려면 아래 코드 사용 (주석 처리)
        /*
        // 세션에서 username을 가져오기
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in");
        }
        */ 
        // 3. DTO에 itemNo 값 설정
        dto.setItemNo(itemNo);

        // 4. 서비스 호출
        boolean isUpdated = itemService.updatePrice(dto, username);

        // 5. 결과 확인 및 응답 반환
        if (isUpdated) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.badRequest().body("fail");
        }
    }

}
