package com.example.demo.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.cart.CartDto.Read;
import com.example.demo.exception.FailException;
import com.example.demo.image.ItemImage;
import com.example.demo.image.ItemImageSaveLoad;
import com.example.demo.item.ItemDao;
import com.example.demo.item.ItemDto;

import java.util.List;
@Service
@Transactional
public class CartService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private ItemDao itemDao;
    
 // 상품 추가 (장바구니에 추가) - 상품이 추가되거나 수량을 증가시키는 메서드
    public List<CartDto.Read> addToCart(Long itemNo, String username, String imageUrl) {
        try {
            if (cartDao.SearchCartByUsernameAndItemNo(username, itemNo) == false) {
                Integer price = itemDao.findPriceByItemNo(itemNo);
                cartDao.save(new Cart(itemNo, username, 1L, price, price));
            } else {
                cartDao.increase(username, itemNo);
            }

            // 장바구니 조회 시 상품 이미지 URL을 설정
            return cartDao.findByUsername(username, imageUrl);
        } catch (Exception e) {
            // 예외 발생 시 FailException을 던지도록 수정
            throw new FailException("1: 장바구니에 상품 추가 중 오류 발생: " + e.getMessage());
        }
    }
    
    // 장바구니 리스트 불러오기
    public List<CartDto.Read> getCartList(String username) {
        try {
            // 장바구니 목록을 조회
            List<CartDto.Read> cartItems = cartDao.findByUsername(username, ItemImageSaveLoad.IMAGE_URL);
            
            if (cartItems.isEmpty()) {
                throw new FailException("장바구니가 비어있거나 사용자가 존재하지 않습니다.");
            }

            // 각 장바구니 항목에 대해 이미지 URL을 설정
            for (CartDto.Read cartItem : cartItems) {
                // 상품 번호를 기준으로 이미지를 조회해서 이미지 URL을 설정
                List<ItemImage> itemImages = itemDao.findItemImagesByItemNo(cartItem.getItemNo());
                
                if (itemImages != null && !itemImages.isEmpty()) {
                    // 상품 이미지가 있으면 첫 번째 이미지를 사용 (여러 이미지가 있을 경우 첫 번째 이미지 사용)
                    cartItem.setItemImage("/api/images?imagename=" + itemImages.get(0).getImageName());
                } else {
                    // 상품에 이미지가 없다면 기본 이미지 URL 설정
                    cartItem.setItemImage("/api/images?imagename=default-image.jpg");
                }
            }
            
            return cartItems;
        } catch (Exception e) {
            throw new FailException("장바구니 목록을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 상품 읽어오기 - 장바구니에서 상품을 읽어오는 메서드
    public List<CartDto.Read> read(String username, String imageUrl) {
        try {
            if (username == null || username.isEmpty()) {
                username = "winter_shop";  // 하드코딩된 username (테스트용)
            }

            // 장바구니 항목 조회
            List<CartDto.Read> cartItems = cartDao.findByUsername(username, imageUrl);
            if (cartItems.isEmpty()) {
                // 기존의 예외 던지기 대신, 빈 리스트 또는 null을 반환
                return null; // 또는 빈 리스트: return new ArrayList<>();
            }

            // 각 장바구니 항목에 대해 이미지 URL을 설정
            for (CartDto.Read cartItem : cartItems) {
                // 상품 번호를 기준으로 이미지를 조회해서 이미지 URL을 설정
                List<ItemImage> itemImages = itemDao.findItemImagesByItemNo(cartItem.getItemNo());
                
                if (itemImages != null && !itemImages.isEmpty()) {
                    // 상품 이미지가 있으면 첫 번째 이미지를 사용 (여러 이미지가 있을 경우 첫 번째 이미지 사용)
                    cartItem.setItemImage("/api/images?imagename=" + itemImages.get(0).getImageName());
                } else {
                    // 상품에 이미지가 없다면 기본 이미지 URL 설정
                    cartItem.setItemImage("/api/images?imagename=default-image.jpg");
                }
            }
            
            return cartItems;
        } catch (Exception e) {
            throw new FailException("2: 장바구니 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    // 장바구니 수량 업데이트 후 갱신된 장바구니 목록 반환
    @Transactional
    public List<CartDto.Read> updateCartEa(String username, List<CartDto.Read> cartUpdates) {
        for (CartDto.Read cartDto : cartUpdates) {
            try {
                if (cartDto.getItemNo() == null || cartDto.getCartEa() == null) {
                    throw new FailException("ItemNo and CartEa cannot be null");
                }

                // 장바구니에 이미 있는 상품인지 확인
                Boolean itemExists = cartDao.SearchCartByUsernameAndItemNo(username, cartDto.getItemNo());
                if (!itemExists) {
                    throw new FailException("해당 상품이 장바구니에 없습니다.");
                }

                // 수량이 10개를 넘는지 체크
                if (cartDto.getCartEa() > 10) {
                    throw new FailException("장바구니 상품 수량은 10개를 넘을 수 없습니다.");
                }

                // 재고 수량 확인
                Integer availableStock = itemDao.getAvailableJango(cartDto.getItemNo());
                if (cartDto.getCartEa() > availableStock) {
                    throw new FailException("선택한 수량은 재고 수량을 초과할 수 없습니다. 현재 재고: " + availableStock);
                }

                // 수량 변경 처리
                Integer rowsUpdate = cartDao.updateCartEa(username, cartDto.getItemNo(), cartDto.getCartEa());
                if (rowsUpdate > 0) {
                    System.out.println("수량이 변경되었습니다. itemNo: " + cartDto.getItemNo());
                } else {
                    throw new FailException("수량 변경에 실패했습니다.");
                }
            } catch (FailException e) {
                throw e;  // FailException은 호출자에게 전달
            } catch (Exception e) {
                throw new FailException("장바구니 수량 변경 중 오류 발생: " + e.getMessage());
            }
        }

        // 수량 변경 후 장바구니 목록 반환
        List<CartDto.Read> updatedCart = cartDao.findByUsername(username, ItemImageSaveLoad.IMAGE_URL);

        // 이미지 URL을 다시 /api/images?imagename= 형식으로 변환
        for (CartDto.Read cartItem : updatedCart) {
            if (cartItem.getItemImage() != null && !cartItem.getItemImage().startsWith("/api/images?imagename=")) {
                cartItem.setItemImage("/api/images?imagename=" + cartItem.getItemImage());
            }
        }

        // 수량 변경 메시지 반환
        return updatedCart;
    }

    
    // 장바구니에 겹치는 상품의 개수 증가
    public List<CartDto.Read> increase(Long itemNo, String username, String imageUrl) {
        try {
            // 상품 개수 조회
            Integer cartEa = cartDao.findCartEaByUsernameAndItemNo(username, itemNo)
                                     .orElseThrow(() -> new FailException("상품을 찾을 수 없습니다"));
            // 재고 확인
            boolean canOrder = itemDao.availableToOrder(itemNo, cartEa); 
            if (canOrder) {
                // 장바구니 상품 개수 증가
                cartDao.increase(username, itemNo);  // 상품 수량 증가 (DB 업데이트)
            } else {
                // 재고 부족 시 오류
                throw new FailException("재고 수보다 많습니다");
            }
            // 장바구니 목록 리턴
            return cartDao.findByUsername(username, ItemImageSaveLoad.IMAGE_URL);
        } catch (Exception e) {
            // 예외 처리: 예외 발생 시 FailException을 던지거나 특정 응답 반환
            throw new RuntimeException("장바구니 상품 수 증가 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 장바구니에서 여러개의 상품을 삭제하는 부분
    @Transactional
    public void deleteCartItems(List<Long> itemNos, String username) {
        if (itemNos == null || itemNos.isEmpty() || itemNos.stream().anyMatch(itemNo -> itemNo == null)) {
            throw new IllegalArgumentException("삭제할 상품 번호가 없거나 유효하지 않은 값이 포함되었습니다.");
        }
        // 한 번의 DB 호출로 여러 항목 삭제
        int deletedRows = cartDao.deleteCartItems(itemNos, username);  // 삭제된 행 수는 int로 반환됨

        if (deletedRows == 0) {  // 삭제된 항목이 없는 경우 예외 발생
            throw new FailException("장바구니 항목을 찾을 수 없습니다.");
        }
    }

    /*
    public Map<String, Object> orderCheck(ItemDto.Inos dto, String username) {
    	return null;
    }
    */
}
