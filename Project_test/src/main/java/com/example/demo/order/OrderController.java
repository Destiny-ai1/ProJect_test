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
    public ModelAndView createOrderForm(OrderDto.Create dto,
                                        @RequestParam(value = "selectedItems", required = false) String selectedItemsJson,
                                        Principal principal, HttpSession session) {
        String username = principal.getName(); // 사용자 이름 가져오기
        ModelAndView mav = new ModelAndView("order/create");

        try {
            // 사용자 포인트 조회
            Integer userPoints = memberDao.userpoint(username);
            mav.addObject("userPoints", userPoints);

            // 세션에서 주문 항목과 합계 가져오기 (세션 데이터 우선 사용)
            List<CartDto.Read> selectedCartItems;
            Integer totalAmount;
            Long orderNo;
            String itemName = ""; // itemName 초기화

            if (session.getAttribute("selectedItems") != null && session.getAttribute("totalAmount") != null && session.getAttribute("orderNo") != null) {
                selectedCartItems = (List<CartDto.Read>) session.getAttribute("selectedItems");
                totalAmount = (Integer) session.getAttribute("totalAmount");
                orderNo = (Long) session.getAttribute("orderNo");

                // 첫 번째 아이템 이름 가져오기 (예: 여러 상품 중 첫 번째 상품)
                if (!selectedCartItems.isEmpty()) {
                    itemName = selectedCartItems.get(0).getItemIrum();
                }
            } else {
                // 세션 데이터가 없는 경우 JSON에서 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                selectedCartItems = objectMapper.readValue(selectedItemsJson,
                        new TypeReference<List<CartDto.Read>>() {});

                // 합계 계산
                totalAmount = selectedCartItems.stream()
                        .mapToInt(CartDto.Read::getCartTotalPrice)
                        .sum();

                // 주문 번호 생성 (임시)
                orderNo = System.currentTimeMillis(); // 예시로 현재 시간을 사용해 주문 번호 생성

                // 첫 번째 아이템 이름 가져오기
                if (!selectedCartItems.isEmpty()) {
                    itemName = selectedCartItems.get(0).getItemIrum();
                }

                // 세션에 저장
                session.setAttribute("selectedItems", selectedCartItems);
                session.setAttribute("totalAmount", totalAmount);
                session.setAttribute("orderNo", orderNo);
            }

            // 세션 정보 확인 로그
            System.out.println("Selected Items from session: " + selectedCartItems);
            System.out.println("Total Amount from session: " + totalAmount);
            System.out.println("Order No from session: " + orderNo);
            System.out.println("Username: " + username);
            System.out.println("Item Name: " + itemName);

            // 데이터 모델에 추가
            mav.addObject("orders", selectedCartItems);
            mav.addObject("totalAmount", totalAmount);
            mav.addObject("orderNo", orderNo);
            mav.addObject("username", username); // 사용자 이름 추가
            mav.addObject("itemName", itemName); // 아이템 이름 추가

        } catch (Exception e) {
            e.printStackTrace();
            mav.setViewName("error/error");
            mav.addObject("message", "주문 생성 중 오류 발생: " + e.getMessage());
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
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/order/read")
    public ModelAndView readOrder(@RequestParam("orderNo") Long orderNo) {
        try {
            OrderDto.Read dto = orderService.getOrder(orderNo);
            if (dto == null) {
                return new ModelAndView("error/error").addObject("message", "주문 정보를 찾을 수 없습니다.");
            }
         
            return new ModelAndView("order/read").addObject("result", dto);
        } catch (FailException e) {
         
            return new ModelAndView("error/error").addObject("message", e.getMessage());
        }
    }

    // 모든 주문 목록을 조회하고 주문 및 결제 내역 뷰를 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
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
    @PostMapping("/order/order_payment_summary")
    public ModelAndView viewOrderList(@RequestBody List<Long> orderNo, Principal principal) {
        if (orderNo == null || orderNo.isEmpty()) {
            return new ModelAndView("cart/list").addObject("errorMessage", "최소 하나의 상품을 선택해야 합니다.");
        }

        try {
            Long orderNo = orderService.createOrderFromCart(orderNo), null, principal);
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
        return new ModelAndView("error/error").addObject("message", e.getMessage());
    }
}
