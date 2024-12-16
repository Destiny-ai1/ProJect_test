package com.example.demo.admin;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminDao adminDao;

    // 모든 사용자 정보 가져오기
    public List<AdminDto.User> getAllUser() {
        List<AdminDto.User> users = adminDao.getAllUser();
        for (AdminDto.User user : users) {
            user.setStatus(user.isEnabled() ? "active" : "inactive");
        }
        return users;
    }

    // 특정 사용자 상태 변경
    public void toggleUserStatus(String username) {
        AdminDto.User user = adminDao.findUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        boolean newStatus = !user.isEnabled();
        adminDao.updateUserStatus(username, newStatus);
    }

    // 검색 및 필터링
    public List<AdminDto.User> searchMembers(String search, String status) {
        // status 값 매핑: active -> 1, inactive -> 0
        String numericStatus = null;
        if ("active".equalsIgnoreCase(status)) {
            numericStatus = "1";
        } else if ("inactive".equalsIgnoreCase(status)) {
            numericStatus = "0";
        }

        // AdminDao의 findUsersByCriteria 호출
        return adminDao.findUsersByCriteria(search, numericStatus);
    }
    public List<AdminDto.Order> getAllOrders() {
        List<AdminDto.Order> orders = adminDao.getAllOrders();
        return (orders != null) ? orders : new ArrayList<>();
    }
    
    public List<AdminDto.User> getPagedUsers(int page, int pageSize) {
        int offset = (page - 1) * pageSize; // 페이지 번호에 따른 OFFSET 계산
        return adminDao.getPagedUsers(offset, pageSize);
    }


    // 주문 상태 업데이트
    public void updateOrderStatus(Long orderId, String status) {
        adminDao.updateOrderStatus(orderId, status);
    }

    // 주문 검색 및 필터링
    public List<AdminDto.Order> searchOrders(String search, String status) {
        return adminDao.findOrdersByCriteria(search, status);
    }
   
    }
