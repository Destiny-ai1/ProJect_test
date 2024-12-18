package com.example.demo.order;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.cart.*;
import com.example.demo.exception.FailException;
import com.example.demo.image.ItemImageSaveLoad;
import com.example.demo.item.ItemDao;
import com.example.demo.item.ItemDto;
import com.example.demo.item.ItemService;

import jakarta.transaction.Transactional;
import java.security.Principal;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private CartDao cartDao;

    @Autowired
    private ItemService itemService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ItemDao itemDao;

    // 주문 생성 로직
    @Transactional
    public Long createOrder(OrderDto.Create create) {
        // 랜덤 주문번호 생성 (수정된 부분)
        Long randomOrderNo = ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L);
        create.setOrderNo(randomOrderNo);

        // 주문 저장
        orderDao.save(create);
        return randomOrderNo; // 생성된 주문번호 반환
    }


    // 선택된 장바구니 항목으로 주문 생성 로직
    @Transactional
    public Long createOrderFromCart(
            List<Long> selectedItems,
            String imageUrl,
            Principal principal) throws FailException {
        String username = principal.getName();

        if (selectedItems == null || selectedItems.isEmpty()) {
            throw new FailException("선택된 항목이 없습니다.");
        }

        // imageUrl이 null이거나 비어 있을 경우 기본값 설정
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "/default/images/";
        }

        // 장바구니 항목 조회
        List<CartDto.Read> cartItems = cartDao.findByUsernameAndInos(username, imageUrl, selectedItems);
        if (cartItems.isEmpty()) {
            throw new FailException("장바구니에 유효한 항목이 없습니다.");
        }

        // 주문 생성
        Order order = new Order();
        order.setUsername(username);
        order.setOrderStatus("PENDING"); // 초기 상태 설정
        orderDao.save(order);

        Long orderNo = order.getOrderNo();

        // 주문 상세 정보 생성
        for (CartDto.Read cartItem : cartItems) {
            int stock = itemDao.getStockByItemSize(cartItem.getItemNo(), cartItem.getItemSize());
            if (stock < cartItem.getCartEa()) {
                throw new FailException("주문 수량이 재고를 초과합니다.");
            }

            OrderDetail detail = OrderDetail.builder()
                    .orderNo(orderNo)
                    .itemNo(cartItem.getItemNo())
                    .itemName(cartItem.getItemIrum())
                    .detailEa(cartItem.getCartEa().intValue())
                    .price(cartItem.getCartTotalPrice())
                    .build();
            orderDetailDao.save(detail);
        }

        // 장바구니 항목 삭제 (주문 성공 후에만 삭제)
        cartService.deleteCartItems(
                cartItems.stream()
                        .map(item -> new ItemDto.ItemDeleteDTO(item.getItemNo(), item.getItemSize()))
                        .collect(Collectors.toList()), 
                username);

        return orderNo;
    }


    // 주문 정보 조회
    public OrderDto.Read getOrder(Long orderNo) {
        OrderDto.Read order = orderDao.findById(orderNo)
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));
        System.out.println("Service - 주문 정보: " + order);

        // 주문 상세 정보 조회
        List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(orderNo);
        System.out.println("Service - 주문 상세 정보 (OrderDetail): " + orderDetails);

        // DTO로 변환
        List<OrderDetailDto> orderDetailDtos = orderDetails.stream()
                .map(detail -> OrderDetailDto.builder()
                        .orderNo(detail.getOrderNo())
                        .itemNo(detail.getItemNo())
                        .itemName(detail.getItemName())
                        .image(detail.getImage())
                        .detailEa(detail.getDetailEa())
                        .price(detail.getPrice())
                        .build())
                .toList();
        System.out.println("Service - 변환된 주문 상세 정보 (OrderDetailDto): " + orderDetailDtos);

        order.setOrderDetails(orderDetailDtos);
        return order;
    }

    // 전체 주문 목록 조회 로직
    public List<OrderDto.OrderList> getAllOrders() {
        List<OrderDto.OrderList> orders = orderDao.findAll();

        // 각 주문의 주문 상세 정보 조회 로직 추가 (선택 사항)
        for (OrderDto.OrderList order : orders) {
            List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(order.getOrderNo());

            // OrderDetail을 OrderDetailDto로 변환
            List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
            for (OrderDetail detail : orderDetails) {
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

        return orders;
    }

    // 전체 주문 목록과 결제 정보 조회 로직
    public List<OrderDto.OrderListWithPayment> getAllOrdersWithPayments() {
        List<OrderDto.OrderListWithPayment> ordersWithPayments = orderDao.findAllWithPayments();

        for (OrderDto.OrderListWithPayment order : ordersWithPayments) {
            List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(order.getOrderNo());

            List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
            for (OrderDetail detail : orderDetails) {
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

        return ordersWithPayments;
    }

    // 주문 업데이트 로직
    @Transactional
    public void updateOrder(OrderDto.Update dto) {
        OrderDto.Read order = orderDao.findById(dto.getOrderNo())
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));
        orderDao.update(dto.toEntity());

        // 주문 상세 정보 업데이트 로직 추가
        if (dto.getOrderDetails() != null) {
            for (OrderDetailDto detailDto : dto.getOrderDetails()) {
                // OrderDetailDto를 OrderDetail로 변환
                OrderDetail detail = detailDto.toEntity();
                detail.setOrderNo(dto.getOrderNo()); // 주문 번호 설정

                // 주문 상세 정보 업데이트
                orderDetailDao.update(detail);
            }
        }
    }

    // 주문 삭제 로직
    @Transactional
    public void deleteOrder(Long orderNo) {
        OrderDto.Read order = orderDao.findById(orderNo)
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));
        // 관련된 주문 상세 항목 삭제 로직 추가
        orderDetailDao.deleteByOrderNo(orderNo);
        orderDao.delete(orderNo);
    }

    // 장바구니에서 선택된 항목의 총 가격 계산 로직 추가
    public int calculateTotalPriceFromCart(List<Long> selectedItems, Principal principal) throws FailException {
        String username = principal.getName();
        int totalPrice = 0;
        for (Long itemNo : selectedItems) {
            var cartItem = cartDao.findByItemNoAndUsername(itemNo, username)
                    .orElseThrow(() -> new FailException("장바구니 항목을 찾을 수 없습니다."));
            totalPrice += cartItem.getCartEa() * cartItem.getCartPrice();
        }
        return totalPrice;
    }

    // 주문을 취소하고 장바구니로 복구하는 로직
    @Transactional
    public void cancelOrderAndRestoreToCart(Long orderNo) throws FailException {
        OrderDto.Read order = orderDao.findById(orderNo)
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));

        // 주문 상세 정보 조회 추가
        List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(orderNo);
        String username = order.getUsername();

        for (OrderDetail detail : orderDetails) {
            // 장바구니에 항목 추가
            cartDao.save(detail.getItemNo(), username, detail.getDetailEa(), detail.getPrice());

            // 재고 복구
            int jango = itemService.getItemJango(detail.getItemNo());
            itemService.updateItemJango(detail.getItemNo(), jango + detail.getDetailEa());
        }

        // 주문 삭제
        deleteOrder(orderNo);
    }
    
    @Transactional
    public void restoreOrderToCart(Order order) {
        List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(order.getOrderNo());
        for (OrderDetail detail : orderDetails) {
            Cart cart = new Cart();
            cart.setItemNo(detail.getItemNo());
            cart.setUsername(order.getUsername());
            cart.setCartEa(detail.getDetailEa());
            cart.setCartPrice(detail.getPrice());
            cart.setCartTotalPrice(detail.getDetailEa() * detail.getPrice());
            cartDao.save(cart);
        }
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
