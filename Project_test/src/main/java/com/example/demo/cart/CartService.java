package com.example.demo.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.FailException;
import com.example.demo.item.ItemService;
import com.example.demo.order.OrderDao;
import com.example.demo.order.OrderDetailDao;
import com.example.demo.order.Order;
import com.example.demo.order.OrderDetail;

import java.util.List;
import java.util.Optional;

@Service
public class CartService { // 장바구니 비즈니스 로직을 처리하는 서비스 클래스

    @Autowired
    private CartDao cartDao; // 장바구니 데이터 접근 객체 (DAO)

    @Autowired
    private ItemService itemService; // 상품 정보를 가져오기 위한 서비스

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Transactional
    public void addToCart(CartDto.Create dto, String username) { // 장바구니에 상품을 추가하는 메소드
        dto.setUsername(username); // 사용자 이름을 DTO에 설정
        Optional<CartDto.Read> existingCartItem = cartDao.findByItemNoAndUsername(dto.getItemNo(), dto.getUsername());
        if (existingCartItem.isPresent()) {
            // 이미 장바구니에 존재하는 경우 수량만 업데이트
            CartDto.Read existingItem = existingCartItem.get();
            CartDto.Update updateDto = new CartDto.Update(existingItem.getItemNo(), existingItem.getUsername(), existingItem.getCartEa() + dto.getCartEa());
            updateCartItem(updateDto, username);
        } else {
            // 장바구니에 존재하지 않는 경우 새로 추가
            Cart cart = dto.toEntity(); // DTO를 엔티티로 변환
            cartDao.save(cart); // 장바구니 항목 저장
        }
    }

    public List<CartDto.Read> getCartItems(String username) { // 특정 사용자의 장바구니 항목을 조회하는 메소드
        List<CartDto.Read> cartItems = cartDao.findAllByUsername(username); // 사용자의 모든 장바구니 항목 조회
        for (CartDto.Read cartItem : cartItems) { // 각 장바구니 항목에 대해 상품 이름과 이미지를 설정
            cartItem.setItemIrum(itemService.getItemIrumById(cartItem.getItemNo())); // ItemService를 통해 상품 이름 설정
            cartItem.setImageName(itemService.getImageNameById(cartItem.getItemNo())); // ItemService를 통해 해당 상품 번호에 대한 이미지 URL을 설정
        }
        return cartItems; // 장바구니 항목 리스트 반환
    }

    public Optional<CartDto.Read> getCartItemByUsernameAndItemNo(String username, Long itemNo) { // 특정 사용자의 장바구니 아이템 조회하는 메소드
        return cartDao.findByItemNoAndUsername(itemNo, username); // 사용자와 상품 번호에 대한 장바구니 항목 조회
    }

    @Transactional
    public void updateCartItem(CartDto.Update dto, String username) { // 장바구니 항목을 업데이트하는 메소드
        dto.setUsername(username); // 사용자 이름을 DTO에 설정
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

    @Transactional
    public Long createOrderFromCart(List<Long> selectedItems, String username) throws FailException {
        // 주문 생성 로직을 구현합니다.
        try {
            // 새로운 주문 생성
            Order order = new Order();
            order.setMemberUsername(username);
            orderDao.save(order);

            // 선택된 장바구니 항목을 주문 상세 항목으로 추가
            for (Long itemNo : selectedItems) {
                CartDto.Read cartItem = cartDao.findByItemNoAndUsername(itemNo, username)
                        .orElseThrow(() -> new FailException("장바구니 항목을 찾을 수 없습니다."));

                // 주문 상세 생성
                OrderDetail orderDetail = OrderDetail.builder()
                        .orderNo(order.getOrderNo())
                        .itemNo(cartItem.getItemNo())
                        .itemName(cartItem.getItemIrum())
                        .detailEa(cartItem.getCartEa())
                        .price(cartItem.getCartPrice())
                        .build();
                orderDetailDao.save(orderDetail);

                // 장바구니에서 항목 삭제
                cartDao.delete(itemNo, username);
            }
            return order.getOrderNo();
        } catch (Exception e) {
            throw new FailException("주문 생성 중 오류가 발생했습니다.");
        }
    }

    // 주문 생성 로직을 통해 선택된 장바구니 항목만 전송
    @Transactional
    public Long createOrder(List<Long> selectedItems, String username) throws FailException {
        return createOrderFromCart(selectedItems, username);
    }
}
