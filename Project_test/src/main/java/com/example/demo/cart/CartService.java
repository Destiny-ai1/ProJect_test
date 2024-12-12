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
import java.util.stream.Collectors;
@Service
@Transactional
public class CartService {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private ItemDao itemDao;
    
    // 상품 추가 (장바구니에 추가) - 상품이 추가되거나 수량을 증가시키는 메서드
    public List<CartDto.Read> addToCart(Long itemNo, String username, String itemSize, String imageUrl) {
        try {
            // 장바구니에 동일한 상품(상품번호 + 사이즈)이 있는지 확인
            if (!cartDao.SearchCartByUsernameAndItemNoAndSize(username, itemNo, itemSize)) {
                // 상품이 없으면 가격을 조회
                Integer price = itemDao.findPriceByItemNo(itemNo);

                // 재고 확인 (item_size 테이블에서 해당 상품과 사이즈의 재고 조회)
                Integer stockQuantity = cartDao.getStockQuantity(itemNo, itemSize);  // 재고 조회

                if (stockQuantity == null || stockQuantity < 1) {
                    throw new FailException("구매하시려는 상품의 재고 수량을 초과합니다.");
                }

                // 장바구니에 상품을 추가 (선택한 사이즈 포함)
                cartDao.save(new Cart(itemNo, username, 1L, price, price, itemSize));  // itemSize 추가
            } else {
                // 장바구니에서 해당 상품의 개수 조회 (itemSize도 함께 고려)
                Integer cartEa = cartDao.findCartEaByUsernameAndItemNoAndSize(username, itemNo, itemSize)
                                         .orElseThrow(() -> new FailException("상품을 찾을 수 없습니다"));

                // 장바구니에 증가시킬 수량
                Integer requestedEa = cartEa + 1;  // 현재 수량 + 1 증가할 수량

                // 재고 확인 (item_size 테이블에서 해당 상품과 사이즈의 재고 조회)
                Integer stockQuantity = cartDao.getStockQuantity(itemNo, itemSize);  // 재고 조회

                if (stockQuantity == null || stockQuantity < requestedEa) {
                    throw new FailException("구매하시려는 상품의 재고 수량을 초과합니다.");
                }

                // 상품이 이미 있으면 수량을 증가시킴 (동일한 사이즈일 경우)
                cartDao.increase(username, itemNo, itemSize);  // itemSize 포함
            }

            // 장바구니 조회 시 상품 이미지 URL을 설정
            return cartDao.findByUsername(username, imageUrl);
        } catch (Exception e) {
            throw new FailException("장바구니에 상품 추가 중 오류 발생: " + e.getMessage());
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
                List<ItemImage> itemImages = itemDao.findByItemNo(cartItem.getItemNo());
                
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
                List<ItemImage> itemImages = itemDao.findByItemNo(cartItem.getItemNo());

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

    @Transactional
    public List<CartDto.Read> updateCartEa(String username, List<CartDto.Read> cartUpdates) {
        for (CartDto.Read cartDto : cartUpdates) {
            System.out.println("Updating cart: " + cartDto.getItemNo() + " with size " + cartDto.getItemSize() + " and quantity " + cartDto.getCartEa());

            // itemNo, itemSize 확인
            if (cartDto.getItemSize() == null) {
                throw new FailException("Item size is required");
            }

            // 장바구니에 해당 상품이 존재하는지 확인
            Boolean itemExists = cartDao.SearchCartByUsernameAndItemNoAndSize(username, cartDto.getItemNo(), cartDto.getItemSize());
            if (!itemExists) {
                throw new FailException("해당 상품이 장바구니에 없습니다.");
            }

            // 현재 장바구니에 있는 상품 수량을 확인 (itemSize 포함)
            Integer currentCartEa = cartDao.findCartEaByUsernameAndItemNoAndSize(username, cartDto.getItemNo(), cartDto.getItemSize())
                                            .orElseThrow(() -> new FailException("장바구니에서 해당 상품과 사이즈를 찾을 수 없습니다."));

            // 재고 수량 조회
            Integer stockQuantity = cartDao.getStockQuantity(cartDto.getItemNo(), cartDto.getItemSize());
            if (stockQuantity == null) {
                throw new FailException("해당 사이즈의 재고를 찾을 수 없습니다.");
            }

            // 재고가 장바구니 수량보다 적으면 예외 처리
            if (cartDto.getCartEa() > stockQuantity) {
            	throw new FailException("구매하시려는 상품이 재고수량을 초과합니다.");
            }

            // 장바구니 수량 업데이트
            Integer rowsUpdate = cartDao.updateCartEa(username, cartDto.getItemNo(), cartDto.getItemSize(), cartDto.getCartEa());
            if (rowsUpdate > 0) {
                System.out.println("수량이 변경되었습니다. itemNo: " + cartDto.getItemNo());
            } else {
                throw new FailException("수량 변경에 실패했습니다.");
            }
        }

        // 장바구니 목록 반환
        return cartDao.findByUsername(username, ItemImageSaveLoad.IMAGE_URL);
    }

    // 장바구니에서 여러개의 상품을 삭제하는 부분
    @Transactional
    public void deleteCartItems(List<ItemDto.ItemDeleteDTO> items, String username) {
        if (items == null || items.isEmpty() || items.stream().anyMatch(item -> item.getItemNo() == null || item.getItemSize() == null)) {
            throw new IllegalArgumentException("삭제할 상품 번호 또는 사이즈가 없거나 유효하지 않은 값이 포함되었습니다.");
        }

        // 한 번의 DB 호출로 여러 항목 삭제
        int deletedRows = cartDao.deleteCartItems(items, username);  // 삭제된 행 수는 int로 반환됨

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
