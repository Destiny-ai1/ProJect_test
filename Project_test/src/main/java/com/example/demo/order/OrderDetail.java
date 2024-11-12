package com.example.demo.order;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Component
public class OrderDetail {
    private Long orderNo;
    private Long itemNo;
    private String itemName;
    private String image;
    private int detailEa;
    private int price;
    private char reviewWritten;
   
}