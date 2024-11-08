package com.example.demo.order;

import java.security.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.exception.FailException;

import jakarta.validation.Valid;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 주문 생성 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/create")
    public ModelAndView createOrderForm() {
        return new ModelAndView("order/create");
    }

    // 주문 생성 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/create")
    public ModelAndView createOrder(@Valid OrderDto.Create dto, BindingResult br, Principal principal) {
        if (br.hasErrors()) {
            return new ModelAndView("order/create").addObject("errors", br.getAllErrors());
        }
        Long orderNo = orderService.createOrder(dto); // 주문 생성
        return new ModelAndView("redirect:/order/read?orderNo=" + orderNo); // 생성된 주문의 상세 페이지로 리디렉션
    }

    // 특정 주문을 조회하는 메소드
    @GetMapping("/order/read")
    public ModelAndView readOrder(Long orderNo) {
        OrderDto.Read dto = orderService.getOrder(orderNo);
        return new ModelAndView("order/read").addObject("result", dto); // 조회한 주문 정보를 뷰에 전달
    }

    // 모든 주문 목록을 조회하는 메소드
    @GetMapping("/order/list")
    public ModelAndView listOrders() {
        return new ModelAndView("order/list").addObject("result", orderService.getAllOrders());
    }

    // 주문 수정 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/update")
    public ModelAndView updateOrderForm(Long orderNo) {
        OrderDto.Read dto = orderService.getOrder(orderNo);
        return new ModelAndView("order/update").addObject("result", dto);
    }

    // 주문 수정 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/update")
    public ModelAndView updateOrder(@Valid OrderDto.Update dto, BindingResult br) {
        if (br.hasErrors()) {
            return new ModelAndView("order/update").addObject("errors", br.getAllErrors());
        }
        orderService.updateOrder(dto); // 주문 업데이트
        return new ModelAndView("redirect:/order/read?orderNo=" + dto.getOrderNo()); // 수정된 주문의 상세 페이지로 리디렉션
    }

    // 특정 주문을 삭제하는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/delete")
    public ModelAndView deleteOrder(Long orderNo) {
        orderService.deleteOrder(orderNo); // 주문 삭제
        return new ModelAndView("redirect:/order/list"); // 주문 목록 페이지로 리디렉션
    }

    // 예외 처리 핸들러
    @ExceptionHandler(FailException.class)
    public ModelAndView failExceptionHandler(FailException e) {
        return new ModelAndView("error/error").addObject("message", e.getMessage()); // 오류 메시지를 에러 페이지에 전달
    }
}
