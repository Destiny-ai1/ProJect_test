package com.example.demo.order;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.cart.*;
import com.example.demo.exception.FailException;
import com.example.demo.item.ItemService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private CartDao cartDao;

    @Autowired
    private ItemService itemService;

    // 주문 생성 로직
    @Transactional
    public Long createOrder(OrderDto.Create dto) {
        log.info("Creating order for user: {}", dto.getMemberUsername());
        Order order = dto.toEntity(); // DTO를 엔티티로 변환
        orderDao.save(order); // 데이터베이스에 저장하면 orderNo가 자동으로 설정됨

        Long orderNo = order.getOrderNo(); // 저장 후 자동 생성된 orderNo를 사용

        // 주문 상세 정보 처리 로직
        if (dto.getOrderDetails() != null) {
            for (OrderDetailDto detailDto : dto.getOrderDetails()) {
                log.info("Processing order detail for item: {}", detailDto.getItemNo());
                OrderDetail detail = detailDto.toEntity(); // OrderDetailDto를 OrderDetail로 변환
                detail.setOrderNo(orderNo); // 주문 번호 설정
                orderDetailDao.save(detail); // 각 주문 상세를 데이터베이스에 저장
            }
        }

        log.info("Order created successfully with orderNo: {}", orderNo);
        return orderNo; // 생성된 주문 번호 반환
    }

    // 선택된 장바구니 항목으로 주문 생성 로직
    @Transactional
    public Long createOrderFromCart(List<Long> selectedItems, String username) throws FailException {
        log.info("Creating order from cart for user: {}, with selected items: {}", username, selectedItems);

        // 중복 항목 합치기
        Map<Long, Integer> itemCountMap = new HashMap<>();
        for (Long itemNo : selectedItems) {
            itemCountMap.put(itemNo, itemCountMap.getOrDefault(itemNo, 0) + 1);
        }

        Order order = new Order();
        order.setMemberUsername(username);
        orderDao.save(order);

        Long orderNo = order.getOrderNo(); // 저장 후 자동 생성된 orderNo를 사용

        for (Map.Entry<Long, Integer> entry : itemCountMap.entrySet()) {
            Long itemNo = entry.getKey();
            int totalQuantity = entry.getValue();

            log.info("Processing cart item: {} for user: {} with total quantity: {}", itemNo, username, totalQuantity);

            // 장바구니 항목에서 정보 조회
            var cartItem = cartDao.findByItemNoAndUsername(itemNo, username)
                    .orElseThrow(() -> new FailException("장바구니 항목을 찾을 수 없습니다."));

            // 재고 확인 및 업데이트
            int jango = itemService.getItemJango(itemNo);
            if (totalQuantity > jango) {
                log.error("Order quantity exceeds stock for item: {}", itemNo);
                throw new FailException("주문 수량이 재고 수량을 초과할 수 없습니다.");
            }
            itemService.updateItemJango(itemNo, jango - totalQuantity);
            log.info("Stock updated for item: {}, remaining stock: {}", itemNo, jango - totalQuantity);

            // 주문 상세 항목 추가
            OrderDetail detail = OrderDetail.builder()
                    .orderNo(orderNo)
                    .itemNo(cartItem.getItemNo())
                    .itemName(cartItem.getItemIrum())
                    .detailEa(totalQuantity)
                    .price(cartItem.getCartPrice())
                    .build();
            orderDetailDao.save(detail);
            log.info("Order detail saved for item: {} in order: {}", itemNo, orderNo);

            // 장바구니에서 항목 삭제
            cartDao.delete(itemNo, username);
            log.info("Cart item deleted for item: {} and user: {}", itemNo, username);
        }

        log.info("Order from cart created successfully with orderNo: {}", orderNo);
        return orderNo;
    }

    // 주문 조회 로직
    public OrderDto.Read getOrder(Long orderNo) {
        log.info("Fetching order with orderNo: {}", orderNo);
        OrderDto.Read order = orderDao.findById(orderNo)
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));

        // 주문 상세 정보 조회 추가
        List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(orderNo);

        // OrderDetail을 OrderDetailDto로 변환
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        for (OrderDetail detail : orderDetails) {
            log.info("Processing order detail for orderNo: {}, itemNo: {}", orderNo, detail.getItemNo());
            OrderDetailDto detailDto = OrderDetailDto.builder()
                    .orderNo(detail.getOrderNo()) // 주문 번호가 있을 때 설정
                    .itemNo(detail.getItemNo())
                    .itemName(detail.getItemName())
                    .image(detail.getImage())
                    .detailEa(detail.getDetailEa())
                    .price(detail.getPrice())
                    .reviewWritten(detail.getReviewWritten())
                    .build();
            orderDetailDtos.add(detailDto);
        }

        order.setOrderDetails(orderDetailDtos); // OrderDetailDto 리스트 설정
        log.info("Order fetched successfully with orderNo: {}", orderNo);
        return order;
    }

    // 전체 주문 목록 조회 로직
    public List<OrderDto.OrderList> getAllOrders() {
        log.info("Fetching all orders");
        List<OrderDto.OrderList> orders = orderDao.findAll();

        // 각 주문의 주문 상세 정보 조회 로직 추가 (선택 사항)
        for (OrderDto.OrderList order : orders) {
            log.info("Fetching order details for orderNo: {}", order.getOrderNo());
            List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(order.getOrderNo());

            // OrderDetail을 OrderDetailDto로 변환
            List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
            for (OrderDetail detail : orderDetails) {
                log.info("Processing order detail for orderNo: {}, itemNo: {}", order.getOrderNo(), detail.getItemNo());
                OrderDetailDto detailDto = OrderDetailDto.builder()
                        .orderNo(detail.getOrderNo()) // 주문 번호가 있을 때 설정
                        .itemNo(detail.getItemNo())
                        .itemName(detail.getItemName())
                        .image(detail.getImage())
                        .detailEa(detail.getDetailEa())
                        .price(detail.getPrice())
                        .reviewWritten(detail.getReviewWritten())
                        .build();
                orderDetailDtos.add(detailDto);
            }

            order.setOrderDetails(orderDetailDtos); // OrderDetailDto 리스트 설정
        }

        log.info("All orders fetched successfully");
        return orders;
    }

    // 전체 주문 목록과 결제 정보 조회 로직
    public List<OrderDto.OrderListWithPayment> getAllOrdersWithPayments() {
        log.info("Fetching all orders with payments");
        List<OrderDto.OrderListWithPayment> ordersWithPayments = orderDao.findAllWithPayments();

        for (OrderDto.OrderListWithPayment order : ordersWithPayments) {
            log.info("Fetching order details for orderNo: {}", order.getOrderNo());
            List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(order.getOrderNo());

            List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
            for (OrderDetail detail : orderDetails) {
                log.info("Processing order detail for orderNo: {}, itemNo: {}", order.getOrderNo(), detail.getItemNo());
                OrderDetailDto detailDto = OrderDetailDto.builder()
                        .orderNo(detail.getOrderNo()) // 주문 번호가 있을 때 설정
                        .itemNo(detail.getItemNo())
                        .itemName(detail.getItemName())
                        .image(detail.getImage())
                        .detailEa(detail.getDetailEa())
                        .price(detail.getPrice())
                        .reviewWritten(detail.getReviewWritten())
                        .build();
                orderDetailDtos.add(detailDto);
            }

            order.setOrderDetails(orderDetailDtos);
        }

        log.info("All orders with payments fetched successfully");
        return ordersWithPayments;
    }

    // 주문 업데이트 로직
    @Transactional
    public void updateOrder(OrderDto.Update dto) {
        log.info("Updating order with orderNo: {}", dto.getOrderNo());
        OrderDto.Read order = orderDao.findById(dto.getOrderNo())
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));
        orderDao.update(dto.toEntity());

        // 주문 상세 정보 업데이트 로직 추가
        if (dto.getOrderDetails() != null) {
            for (OrderDetailDto detailDto : dto.getOrderDetails()) {
                log.info("Updating order detail for orderNo: {}, itemNo: {}", dto.getOrderNo(), detailDto.getItemNo());
                // OrderDetailDto를 OrderDetail로 변환
                OrderDetail detail = detailDto.toEntity();
                detail.setOrderNo(dto.getOrderNo()); // 주문 번호 설정

                // 주문 상세 정보 업데이트
                orderDetailDao.update(detail);
            }
        }
        log.info("Order updated successfully with orderNo: {}", dto.getOrderNo());
    }

    // 주문 삭제 로직
    @Transactional
    public void deleteOrder(Long orderNo) {
        log.info("Deleting order with orderNo: {}", orderNo);
        OrderDto.Read order = orderDao.findById(orderNo)
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));
        // 관련된 주문 상세 항목 삭제 로직 추가
        orderDetailDao.deleteByOrderNo(orderNo);
        orderDao.delete(orderNo);
        log.info("Order deleted successfully with orderNo: {}", orderNo);
    }

    // 장바구니에서 선택된 항목의 총 가격 계산 로직 추가
    public int calculateTotalPriceFromCart(List<Long> selectedItems, String username) throws FailException {
        log.info("Calculating total price for selected items: {}, user: {}", selectedItems, username);
        int totalPrice = 0;
        for (Long itemNo : selectedItems) {
            var cartItem = cartDao.findByItemNoAndUsername(itemNo, username)
                    .orElseThrow(() -> new FailException("장바구니 항목을 찾을 수 없습니다."));
            totalPrice += cartItem.getCartEa() * cartItem.getCartPrice();
        }
        log.info("Total price calculated: {} for user: {}", totalPrice, username);
        return totalPrice;
    }

    // 주문을 취소하고 장바구니로 복구하는 로직
    @Transactional
    public void cancelOrderAndRestoreToCart(Long orderNo) throws FailException {
        log.info("Cancelling order with orderNo: {} and restoring items to cart", orderNo);
        OrderDto.Read order = orderDao.findById(orderNo)
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));

        // 주문 상세 정보 조회 추가
        List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(orderNo);
        String username = order.getMemberUsername();

        for (OrderDetail detail : orderDetails) {
            // 장바구니에 항목 추가
            cartDao.save(detail.getItemNo(), username, detail.getDetailEa(), detail.getPrice());
            log.info("Restored item: {} to cart for user: {}", detail.getItemNo(), username);

            // 재고 복구
            int jango = itemService.getItemJango(detail.getItemNo());
            itemService.updateItemJango(detail.getItemNo(), jango + detail.getDetailEa());
            log.info("Restored stock for item: {}, new stock: {}", detail.getItemNo(), jango + detail.getDetailEa());
        }

        // 주문 삭제
        deleteOrder(orderNo);
        log.info("Order cancelled and items restored to cart successfully for orderNo: {}", orderNo);
    }
    
    @Transactional
    public void restoreOrderToCart(Order order) {
        List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(order.getOrderNo());
        for (OrderDetail detail : orderDetails) {
            Cart cart = new Cart();
            cart.setItemNo(detail.getItemNo());
            cart.setUsername(order.getMemberUsername());
            cart.setCartEa(detail.getDetailEa());
            cart.setCartPrice(detail.getPrice());
            cart.setCartTotalPrice(detail.getDetailEa() * detail.getPrice());
            cartDao.save(cart);
        }
        log.info("Order restored to cart for orderNo: {}", order.getOrderNo());
    }

    public Order findById(Long orderId) {
        return null;
    }

    public void saveOrder(Order order) {
        orderDao.save(order);
    }

	public void completeOrder(Long orderId) {
		// TODO Auto-generated method stub
		
	}
}
