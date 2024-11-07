package com.example.demo.order;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.JobFailException;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public Long createOrder(OrderDto.Create dto) {
        Order order = dto.toEntity();
        orderDao.save(order);
        return order.getOrderNo();
    }

    public OrderDto.Read getOrder(Long orderNo) {
        return orderDao.findById(orderNo)
                .orElseThrow(() -> new JobFailException("주문을 찾을 수 없습니다"));
    }

    public List<OrderDto.OrderList> getAllOrders() {
        return orderDao.findAll();
    }

    public void updateOrder(OrderDto.Update dto) {
        OrderDto.Read order = orderDao.findById(dto.getOrderNo())
                .orElseThrow(() -> new JobFailException("주문을 찾을 수 없습니다"));
        orderDao.update(dto.toEntity());
    }

    public void deleteOrder(Long orderNo) {
        OrderDto.Read order = orderDao.findById(orderNo)
                .orElseThrow(() -> new JobFailException("주문을 찾을 수 없습니다"));
        orderDao.delete(orderNo);
    }
}
