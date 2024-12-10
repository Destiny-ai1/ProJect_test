package com.example.demo.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 사용자 관리 페이지
    @GetMapping("/members")
    @PreAuthorize("hasRole('admin')")
    public String userManagementPage(Model model) {
        List<AdminDto.User> users = adminService.getAllUser();
        model.addAttribute("user", users);
        return "admin/members";
    }

    // 사용자 상태 활성화/비활성화
    @PatchMapping("/members/toggleStatus")
    @ResponseBody
    @PreAuthorize("hasRole('admin')")
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
    @PreAuthorize("hasRole('admin')")
    public List<AdminDto.User> getFilteredUsers(@RequestParam(required = false) String search,
                                                @RequestParam(required = false) String status) {
        return adminService.searchMembers(search, status);
    }
}

