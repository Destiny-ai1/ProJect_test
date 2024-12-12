package com.example.demo.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.*;


import java.util.List;

@Mapper
public interface AdminDao {
    List<AdminDto.User> getAllUser();
    AdminDto.User findUserByUsername(String username);
    void updateUserStatus(@Param("username") String username, @Param("enabled") boolean enabled);
    List<AdminDto.User> findUsersByCriteria(@Param("search") String search, @Param("status") String status);

    List<AdminDto.Order> getAllOrders();
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    List<AdminDto.Order> findOrdersByCriteria(@Param("search") String search, @Param("status") String status);
}

