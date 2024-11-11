package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.cart.Cart;
import com.example.demo.cart.CartDao;
import com.example.demo.cart.CartDto;

import java.util.Optional;

@SpringBootTest
public class CartDaoTest {

    @Autowired
    private CartDao cartDao;

    private Cart cart;

    @BeforeEach
    public void setUp() {
        cart = Cart.builder()
                .itemNo(1L)
                .username("testUser")
                .cartEa(2)
                .cartPrice(1000)
                .cartTotalPrice(2000)
                .build();
    }

    //@Test
    public void testSaveCart() {
        int result = cartDao.save(cart);
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testFindByItemNoAndUsername() {
        // 먼저 저장된 장바구니 항목이 있어야 함
        cartDao.save(cart);
        Optional<CartDto.Read> retrievedCart = cartDao.findByItemNoAndUsername(cart.getItemNo(), cart.getUsername());
        assertThat(retrievedCart).isPresent();
        assertThat(retrievedCart.get().getCartEa()).isEqualTo(cart.getCartEa());
    }

    //@Test
    public void testUpdateCart() {
        // 장바구니 항목 저장 후 업데이트 테스트
        cartDao.save(cart);
        Cart updatedCart = Cart.builder()
                .itemNo(cart.getItemNo())
                .username(cart.getUsername())
                .cartEa(3) // 수량 변경
                .cartPrice(cart.getCartPrice())
                .cartTotalPrice(3000) // 총 가격 변경
                .build();
        int updateResult = cartDao.update(updatedCart);
        assertThat(updateResult).isEqualTo(1);

        Optional<CartDto.Read> retrievedCart = cartDao.findByItemNoAndUsername(cart.getItemNo(), cart.getUsername());
        assertThat(retrievedCart).isPresent();
        assertThat(retrievedCart.get().getCartEa()).isEqualTo(3);
        assertThat(retrievedCart.get().getCartTotalPrice()).isEqualTo(3000);
    }

    //@Test
    public void testDeleteCart() {
        // 장바구니 항목 저장 후 삭제 테스트
        cartDao.save(cart);
        int deleteResult = cartDao.delete(cart.getItemNo(), cart.getUsername());
        assertThat(deleteResult).isEqualTo(1);

        Optional<CartDto.Read> retrievedCart = cartDao.findByItemNoAndUsername(cart.getItemNo(), cart.getUsername());
        assertThat(retrievedCart).isNotPresent();
    }
}
