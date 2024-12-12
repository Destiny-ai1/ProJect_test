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

    // 사용자 상태 변경
    public void toggleUserStatus(String username) {
        AdminDto.User user = adminDao.findUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        adminDao.updateUserStatus(username, !user.isEnabled());
    }

    // 사용자 검색
    public List<AdminDto.User> searchMembers(String search, String status) {
        String numericStatus = "active".equalsIgnoreCase(status) ? "1" : "0";
        return adminDao.findUsersByCriteria(search, numericStatus);
    }

    // 모든 주문 가져오기
    public List<AdminDto.Order> getAllOrders() {
        return adminDao.getAllOrders();
    }

    // 주문 상태 변경
    public void updateOrderStatus(Long orderId, String status) {
        adminDao.updateOrderStatus(orderId, status);
    }

    // 주문 검색
    public List<AdminDto.Order> searchOrders(String search, String status) {
        return adminDao.findOrdersByCriteria(search, status);
    }
}
