package com.example.demo.order;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao {
    public int save(Order order);

    public Optional<OrderDto.Read> findById(Long orderNo);

    public List<OrderDto.OrderList> findAll();

    public void update(Order order);

    public void delete(Long orderNo);
}
