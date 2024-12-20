package com.example.demo.order;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.cart.CartDao;
import com.example.demo.cart.CartDto;
import com.example.demo.exception.FailException;
import com.example.demo.member.Member;
import com.example.demo.member.MemberDao;
import com.example.demo.member.MemberService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;
 
    @Autowired
    private MemberDao memberDao;
    
    
    // 주문 생성 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/create")
    public ModelAndView showOrderPage(Principal principal, HttpSession session) {
        ModelAndView mav = new ModelAndView("order/create");

        try {
            // 세션에서 주문 항목과 합계 가져오기
            List<CartDto.Read> selectedCartItems = (List<CartDto.Read>) session.getAttribute("selectedItems");
            Integer totalAmount = (Integer) session.getAttribute("totalAmount");
            Long orderNo = (Long) session.getAttribute("orderNo");  // 주문 번호 추가

            // 세션 정보 확인
            System.out.println("Selected Items from session: " + selectedCartItems);
            System.out.println("Total Amount from session: " + totalAmount);
            System.out.println("Order No from session: " + orderNo);

            if (selectedCartItems == null || totalAmount == null || orderNo == null) {
                throw new Exception("주문 정보가 없습니다.");
            }

            // 데이터 모델에 추가
            mav.addObject("orders", selectedCartItems);
            mav.addObject("totalAmount", totalAmount);
            mav.addObject("orderNo", orderNo);

        } catch (Exception e) {
            e.printStackTrace(); // 예외 스택 트레이스를 콘솔에 출력
            mav.addObject("message", "주문 페이지 로딩 중 오류 발생: " + e.getMessage());
            mav.setViewName("error");
        }

        return mav;
    }

    // 주문 번호 생성 로직
    private Long generateOrderNo() {
        // 주문 번호를 고유하게 생성하는 로직, 예시로 현재 시간을 기반으로 생성
        return System.currentTimeMillis();
    }

    // POST: 입력된 주문 데이터 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/create")
    public ResponseEntity<Map<String, Object>> createOrderForm(@RequestBody List<CartDto.Read> selectedCartItems, Principal principal, HttpSession session) {
        String username = principal.getName();

        try {
            // 선택한 상품들의 총 가격 계산
            int totalAmount = selectedCartItems.stream()
                    .mapToInt(CartDto.Read::getCartTotalPrice)
                    .sum();

            // 선택한 상품들을 세션에 저장
            session.setAttribute("selectedItems", selectedCartItems);  // 세션에 selectedItems 저장
            session.setAttribute("totalAmount", totalAmount);  // 총 금액도 세션에 저장

            // OrderDto.Create 객체를 만들어서 필요한 정보를 설정
            OrderDto.Create orderCreate = new OrderDto.Create();
            orderCreate.setUsername(username); // 로그인된 사용자 정보
            orderCreate.setTotalPrice((long) totalAmount); // 총 금액
            orderCreate.setOrderStatus("pending"); // 주문 상태 (예: pending)

            // 주문 번호 생성
            Long orderNo = generateOrderNo();

            // 세션에 주문 번호 저장
            session.setAttribute("orderNo", orderNo);

            // 주문 정보 저장
            orderCreate.setOrderNo(orderNo);  // 생성된 주문 번호를 OrderDto에 설정
            
            // HttpSession을 추가로 넘겨주어야 합니다.
            orderService.createOrder(orderCreate, session);  // 세션을 함께 전달

            // 생성된 주문 번호를 응답으로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("orderNo", orderNo);

            return ResponseEntity.ok(response);  // 주문 번호 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "서버 오류"));
        }
    }

    
    // 주문 상세 정보를 조회하고 주문 읽기 뷰를 보여주는 메소드
    @GetMapping("/order/read")
    public ModelAndView readOrder(@RequestParam("orderNo") Long orderNo) {
        try {
            OrderDto.Read dto = orderService.getOrder(orderNo);
            if (dto == null) {
                return new ModelAndView("error/error").addObject("message", "주문 정보를 찾을 수 없습니다.");
            }
            log.info("조회된 주문 정보: " + dto);
            return new ModelAndView("order/read").addObject("result", dto);
        } catch (FailException e) {
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
    public ModelAndView viewOrderList(@RequestBody List<Long> selectedItems, Principal principal) {
        System.out.println("Selected Items for View: " + selectedItems);
        System.out.println("Logged-in Username: " + principal.getName());

        if (selectedItems == null || selectedItems.isEmpty()) {
            return new ModelAndView("cart/list").addObject("errorMessage", "최소 하나의 상품을 선택해야 합니다.");
        }

        try {
            Long orderNo = orderService.createOrderFromCart(selectedItems, null, principal);
            OrderDto.Read order = orderService.getOrder(orderNo);
            return new ModelAndView("order/list").addObject("orders", List.of(order));
        } catch (FailException e) {
            return new ModelAndView("cart/list").addObject("error", e.getMessage());
        }
    }


    // 기존 주문을 수정하기 위한 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/update")
    public ModelAndView updateOrderForm(@RequestParam("orderNo") Long orderNo) {
        try {
            OrderDto.Read dto = orderService.getOrder(orderNo);
            return new ModelAndView("order/update").addObject("result", dto);
        } catch (FailException e) {
            log.error("주문 수정 폼 로딩 실패", e);
            return new ModelAndView("error/error").addObject("message", e.getMessage());
        }
    }

    // 주문 수정 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/update")
    public ModelAndView updateOrder(@Valid OrderDto.Update dto, BindingResult br) {
        if (br.hasErrors()) {
            return new ModelAndView("order/update").addObject("errors", br.getAllErrors());
        }
        try {
            orderService.updateOrder(dto);
            return new ModelAndView("redirect:/order/read?orderNo=" + dto.getOrderNo());
        } catch (FailException e) {
            log.error("주문 수정 실패", e);
            return new ModelAndView("order/update").addObject("error", e.getMessage());
        }
    }

    // 주문 삭제 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/delete")
    public ModelAndView deleteOrder(@RequestParam("orderNo") Long orderNo) {
        try {
            orderService.deleteOrder(orderNo);
            return new ModelAndView("redirect:/order/list");
        } catch (FailException e) {
            log.error("주문 삭제 실패", e);
            return new ModelAndView("error/error").addObject("message", e.getMessage());
        }
    }

    // 주문 완료 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/order/complete")
    public String completeOrder(@RequestParam Long orderId) {
        try {
            orderService.completeOrder(orderId, null);
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
        log.error("예외 발생", e);
        return new ModelAndView("error/error").addObject("message", e.getMessage());
    }
}
