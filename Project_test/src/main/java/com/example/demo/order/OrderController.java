package com.example.demo.order;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.example.demo.exception.FailException;
import com.example.demo.item.ItemService;
import com.example.demo.member.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private MemberService memberService;

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
        // 유효성 검사 오류가 있는 경우, 폼 뷰와 오류 반환
        if (br.hasErrors()) {
            return new ModelAndView("order/create").addObject("errors", br.getAllErrors());
        }

        // 커스텀 유효성 검사 - 사용 포인트는 사용자의 보유 포인트를 초과할 수 없음
        int userPoints = memberService.getMemberPoint(principal.getName());
        if (dto.getUsedPoint() > userPoints) {
            br.rejectValue("usedPoint", "error.usedPoint", "사용 포인트가 보유 포인트보다 많을 수 없습니다.");
            return new ModelAndView("order/create").addObject("errors", br.getAllErrors());
        }

        // 각 주문 상세 항목에 대해 재고 수량 확인
        for (OrderDetailDto detail : dto.getOrderDetails()) {
            int jango = itemService.getItemJango(detail.getItemNo());
            if (detail.getDetailEa() > jango) {
                br.rejectValue("orderDetails", "error.detailEa", "주문 수량이 재고 수량을 초과할 수 없습니다.");
                return new ModelAndView("order/create").addObject("errors", br.getAllErrors());
            }
        }

        try {
            // 주문 생성 후 주문 상세 뷰로 리다이렉트
            Long orderNo = orderService.createOrder(dto);
            return new ModelAndView("redirect:/order/read?orderNo=" + orderNo);
        } catch (FailException e) {
            // 주문 생성 실패 시 로그 기록 및 오류 반환
            log.error("주문 생성 실패", e);
            return new ModelAndView("order/create").addObject("error", e.getMessage());
        }
    }

    // 주문 상세 정보를 조회하고 주문 읽기 뷰를 보여주는 메소드
    @GetMapping("/order/read")
    public ModelAndView readOrder(@RequestParam("orderNo") Long orderNo) {
        try {
            // 주문 상세 정보를 가져와 "order/read" 뷰와 함께 반환
            OrderDto.Read dto = orderService.getOrder(orderNo);
            if (dto == null) {
                return new ModelAndView("error/error").addObject("message", "주문 정보를 찾을 수 없습니다.");
            }
            log.info("조회된 주문 정보: " + dto);
            return new ModelAndView("order/read").addObject("result", dto);
        } catch (FailException e) {
            // 주문 조회 실패 시 로그 기록 및 오류 반환
            log.error("주문 조회 실패", e);
            return new ModelAndView("error/error").addObject("message", e.getMessage());
        }
    }

    // 모든 주문 목록을 조회하고 주문 및 결제 내역 뷰를 보여주는 메소드
    @GetMapping("/order/order_payment_summary")
    public ModelAndView listOrdersWithPayments() {
        var orders = orderService.getAllOrdersWithPayments();
        if (orders == null || orders.isEmpty()) {
            return new ModelAndView("order/order_payment_summary").addObject("message", "주문 정보가 없습니다.");
        }
        return new ModelAndView("order/order_payment_summary").addObject("orders", orders);
    }

    // 모든 주문 목록을 조회하고 주문 상세 및 결제 페이지를 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/list")
    public ModelAndView createOrderFromCart(@RequestParam(value = "selectedItems", required = false) List<Long> selectedItems, Principal principal) {
        log.info("Selected Items: {}", selectedItems);

        if (selectedItems == null || selectedItems.isEmpty()) {
            return new ModelAndView("cart/list").addObject("errorMessage", "최소 하나의 상품을 선택해야 합니다.");
        }
        String username = principal.getName();
        try {
            log.info("Start creating order for user: {}", username);
            // 선택된 장바구니 항목을 기반으로 주문 생성
            Long orderNo = orderService.createOrderFromCart(selectedItems, username);
            log.info("Order created successfully with orderNo: {}", orderNo);

            // 주문 번호로 주문 정보 가져오기
            OrderDto.Read order = orderService.getOrder(orderNo);
            if (order == null) {
                return new ModelAndView("error/error").addObject("message", "주문 정보를 찾을 수 없습니다.");
            }
            return new ModelAndView("order/list").addObject("orders", List.of(order));
        } catch (FailException e) {
            // 주문 생성 실패 시 로그 기록 및 오류 반환
            log.error("주문 생성 실패", e);
            return new ModelAndView("cart/list").addObject("error", e.getMessage());
        }
    }

    // 기존 주문을 수정하기 위한 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/update")
    public ModelAndView updateOrderForm(@RequestParam("orderNo") Long orderNo) {
        try {
            // 수정할 주문 정보를 가져와 "order/update" 뷰와 함께 반환
            OrderDto.Read dto = orderService.getOrder(orderNo);
            if (dto == null) {
                return new ModelAndView("error/error").addObject("message", "주문 정보를 찾을 수 없습니다.");
            }
            return new ModelAndView("order/update").addObject("result", dto);
        } catch (FailException e) {
            // 주문 수정 폼 로딩 실패 시 로그 기록 및 오류 반환
            log.error("주문 수정 폼 로딩 실패", e);
            return new ModelAndView("error/error").addObject("message", e.getMessage());
        }
    }

    // 주문 수정 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/update")
    public ModelAndView updateOrder(@Valid OrderDto.Update dto, BindingResult br) {
        // 유효성 검사 오류가 있는 경우, 폼 뷰와 오류 반환
        if (br.hasErrors()) {
            return new ModelAndView("order/update").addObject("errors", br.getAllErrors());
        }
        try {
            // 주문 업데이트 후 주문 상세 뷰로 리다이렉트
            orderService.updateOrder(dto);
            return new ModelAndView("redirect:/order/read?orderNo=" + dto.getOrderNo());
        } catch (FailException e) {
            // 주문 수정 실패 시 로그 기록 및 오류 반환
            log.error("주문 수정 실패", e);
            return new ModelAndView("order/update").addObject("error", e.getMessage());
        }
    }

    // 주문 삭제 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/delete")
    public ModelAndView deleteOrder(@RequestParam("orderNo") Long orderNo) {
        try {
            // 주문 삭제 후 주문 목록 뷰로 리디렉트
            orderService.deleteOrder(orderNo);
            return new ModelAndView("redirect:/order/list");
        } catch (FailException e) {
            // 주문 삭제 실패 시 로그 기록 및 오류 반환
            log.error("주문 삭제 실패", e);
            return new ModelAndView("error/error").addObject("message", e.getMessage());
        }
    }

    // 주문 완료 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/complete")
    public String completeOrder(@RequestParam Long orderId) {
        try {
            orderService.completeOrder(orderId);
            return "redirect:/order/success";
        } catch (FailException e) {
            log.error("주문 완료 처리 실패", e);
            return "redirect:/order/failure";
        }
    }

    // 주문 취소 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/cancel")
    public String cancelOrder(@RequestParam Long orderId) {
        try {
            orderService.cancelOrderAndRestoreToCart(orderId);
            return "redirect:/order/cancel_success";
        } catch (FailException e) {
            log.error("주문 취소 실패", e);
            return "redirect:/order/failure";
        }
    }

    // 자동 주문 취소 요청을 처리하는 메소드
    @PostMapping("/order/auto-cancel")
    public ResponseEntity<String> autoCancelOrder(@RequestBody Map<String, Long> request) {
        Long orderId = request.get("orderId");
        Order order = orderService.findById(orderId);
        if (order != null && "PENDING".equals(order.getOrderStatus())) {
            order.setOrderStatus("CANCELLED");
            orderService.saveOrder(order);
            orderService.restoreOrderToCart(order);
            return ResponseEntity.ok("주문이 성공적으로 취소되었습니다.");
        }
        return ResponseEntity.badRequest().body("주문을 찾을 수 없거나 이미 처리되었습니다.");
    }

    // 예외 처리 핸들러
    @ExceptionHandler(FailException.class)
    public ModelAndView failExceptionHandler(FailException e) {
        // 예외 발생 시 로그 기록 및 오류 뷰 반환
        log.error("예외 발생", e);
        return new ModelAndView("error/error").addObject("message", e.getMessage());
    }
}
