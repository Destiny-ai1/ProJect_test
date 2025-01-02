package com.example.demo.order;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
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
    private Long detailEa;
    private Long price;
    private String itemSize;
    private Boolean reviewWritten; // Boolean 타입으로 유지
    private String memberUsername; // 사용자명 추가
}