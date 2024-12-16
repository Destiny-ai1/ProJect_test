package com.example.demo.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface AdminDao {
    List<AdminDto.User> getAllUser(); // 모든 사용자 정보 가져오기

    AdminDto.User findUserByUsername(String username); // 특정 사용자 정보 가져오기

    void updateUserStatus(@Param("username") String username, @Param("enabled") boolean enabled); // 사용자 상태 업데이트

    List<AdminDto.User> findUsersByCriteria(@Param("search") String search, @Param("status") String status); // 검색 및 필터링
    
    List<AdminDto.Order> getAllOrders(); // 모든 주문 가져오기
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status); // 주문 상태 변경
    List<AdminDto.Order> findOrdersByCriteria(@Param("search") String search, @Param("status") String status); // 주문 검색 및 필터링
    List<AdminDto.User> getPagedUsers(@Param("offset") int offset, @Param("pageSize") int pageSize);

}
