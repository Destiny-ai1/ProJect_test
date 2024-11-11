package com.example.demo.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.FailException;
import com.example.demo.image.ImageService;
import com.example.demo.item.ItemService;

import java.util.List;
import java.util.Optional;

@Service
public class CartService { // 장바구니 비즈니스 로직을 처리하는 서비스 클래스

    @Autowired
    private CartDao cartDao; // 장바구니 데이터 접근 객체 (DAO)

    @Autowired
    private ItemService itemService; // 상품 정보를 가져오기 위한 서비스

    @Autowired
    private ImageService imageService; // 상품 이미지 URL을 가져오기 위한 서비스

    @Transactional
    public void addToCart(CartDto.Create dto) { // 장바구니에 상품을 추가하는 메소드
        Optional<CartDto.Read> existingCartItem = cartDao.findByItemNoAndUsername(dto.getItemNo(), dto.getUsername());
        if (existingCartItem.isPresent()) {
            // 이미 장바구니에 존재하는 경우 수량만 업데이트
            CartDto.Read existingItem = existingCartItem.get();
            CartDto.Update updateDto = new CartDto.Update(existingItem.getItemNo(), existingItem.getUsername(), existingItem.getCartEa() + dto.getCartEa());
            updateCartItem(updateDto);
        } else {
            // 장바구니에 존재하지 않는 경우 새로 추가
        	
            Cart cart = dto.toEntity(); // DTO를 엔티티로 변환
            cartDao.save(cart); // 장바구니 항목 저장
        }
    }

    public List<CartDto.Read> getCartItems(String username) { // 특정 사용자의 장바구니 항목을 조회하는 메소드
        List<CartDto.Read> cartItems = cartDao.findAllByUsername(username); // 사용자의 모든 장바구니 항목 조회
        for (CartDto.Read cartItem : cartItems) { // 각 장바구니 항목에 대해 상품 이름과 이미지를 설정
            cartItem.setItemName(itemService.getItemNameById(cartItem.getItemNo())); // ItemService를 통해 상품 이름 설정
            cartItem.setItemImageUrl(imageService.getImageUrlByItemId(cartItem.getItemNo())); // ImageService를 통해 해당 상품 번호에 대한 이미지 URL을 설정
        } // 오류 아직 작업중
        
        return cartItems; // 장바구니 항목 리스트 반환
    }

    @Transactional
    public void updateCartItem(CartDto.Update dto) { // 장바구니 항목을 업데이트하는 메소드
        Cart cart = dto.toEntity(); // DTO를 엔티티로 변환
        int updatedRows = cartDao.update(cart); // 장바구니 항목 업데이트
        if (updatedRows == 0) { // 업데이트된 항목이 없는 경우 예외 발생
            throw new FailException("장바구니 항목을 찾을 수 없습니다");
        }
    }

    @Transactional
    public void removeCartItem(Long itemNo, String username) { // 장바구니에서 특정 항목을 삭제하는 메소드
        int deletedRows = cartDao.delete(itemNo, username); // 장바구니 항목 삭제
        if (deletedRows == 0) { // 삭제된 항목이 없는 경우 예외 발생
            throw new FailException("장바구니 항목을 찾을 수 없습니다");
        }
    }
}