package com.example.demo.order;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.FailException;

import jakarta.transaction.Transactional;


@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    // 주문 생성 로직
    @Transactional
    public Long createOrder(OrderDto.Create dto) {
        Order order = dto.toEntity(); // DTO를 엔티티로 변환
        orderDao.save(order); // 데이터베이스에 저장

        // 주문 상세 정보 처리 로직
        if (dto.getOrderDetails() != null) {
            for (OrderDetailDto detailDto : dto.getOrderDetails()) {
                OrderDetail detail = detailDto.toEntity(); // OrderDetailDto를 OrderDetail로 변환
                detail.setOrderNo(order.getOrderNo()); // 주문 번호 설정
                orderDetailDao.save(detail); // 각 주문 상세를 데이터베이스에 저장
            }
        }

        return order.getOrderNo(); // 생성된 주문 번호 반환
    }

    // 주문 조회 로직
    public OrderDto.Read getOrder(Long orderNo) {
        OrderDto.Read order = orderDao.findById(orderNo)
                .orElseThrow(() -> new FailException("주문을 찾을 수 없습니다"));
        
        // 주문 상세 정보 조회 추가
        List<OrderDetail> orderDetails = orderDetailDao.findByOrderNo(orderNo);
        
        // OrderDetail을 OrderDetailDto로 변환
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        for (OrderDetail detail : orderDetails) {
            OrderDetailDto detailDto = new OrderDetailDto(
                    detail.getItemNo(),
                    detail.getItemName(),
                    detail.getImage(),
                    detail.getDetailEa()
            );
            orderDetailDtos.add(detailDto);
        }
        
        order.setOrderDetails(orderDetailDtos); // OrderDetailDto 리스트 설정
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
                OrderDetailDto detailDto = new OrderDetailDto(
                        detail.getItemNo(),
                        detail.getItemName(),
                        detail.getImage(),
                        detail.getDetailEa()
                );
                orderDetailDtos.add(detailDto);
            }
            
            order.setOrderDetails(orderDetailDtos); // OrderDetailDto 리스트 설정
        }
        
        return orders;
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
}