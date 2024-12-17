package com.example.demo.admin;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('admin')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 사용자 관리 페이지
    @GetMapping("/members")
    public String userManagementPage(Model model,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        List<AdminDto.User> users = adminService.getPagedUsers(page, pageSize);
        int totalUsers = adminService.getAllUser().size(); // 전체 사용자 수 계산
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        model.addAttribute("user", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "admin/members";
    }


    // 사용자 상태 활성화/비활성화
    @PatchMapping("/members/toggleStatus")
    @ResponseBody
    public String toggleUserStatus(@RequestParam String username) {
        try {
            adminService.toggleUserStatus(username);
            return "success";
        } catch (IllegalArgumentException e) {
            return "error: " + e.getMessage();
        }
    }

    // 사용자 검색 및 필터링
    @GetMapping("/members/search")
    @ResponseBody
    public List<AdminDto.User> getFilteredUsers(@RequestParam(required = false) String search,
                                                @RequestParam(required = false) String status) {
        return adminService.searchMembers(search, status);
    }
    @GetMapping("/orders")
    public String orderManagementPage(Model model,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        // 전체 주문 리스트
        List<AdminDto.Order> allOrders = adminService.getAllOrders();

        // 페이지 단위로 데이터 나누기
        int totalOrders = allOrders.size();
        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalOrders);

        List<AdminDto.Order> pagedOrders = allOrders.subList(start, end);

        // 모델에 데이터 추가
        model.addAttribute("ordersList", pagedOrders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/orders";
    }






    // 주문 상태 변경
    @PatchMapping("/updateStatus")
    @ResponseBody
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        try {
            adminService.updateOrderStatus(orderId, status);
            return "success";
        } catch (IllegalArgumentException e) {
            return "error: " + e.getMessage();
        }
    }

    // 주문 검색 및 필터링
    @GetMapping("/search")
    @ResponseBody
    public List<AdminDto.Order> getFilteredOrders(@RequestParam(required = false) String search,
                                                  @RequestParam(required = false) String status) {
        return adminService.searchOrders(search, status);
    }
    
}

