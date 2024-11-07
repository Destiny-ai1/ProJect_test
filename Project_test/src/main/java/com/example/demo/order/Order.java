package com.example.demo.order;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Component
public class Order {
	private Long orderNo;
    private LocalDate orderDate;
    private int totalPrice;
    private String orderStatus;
    private int usedPoint;
    private int actPayment;
    
    private String memberUsername;
    private Long postNo;
}
