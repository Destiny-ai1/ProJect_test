package com.example.demo.newitem;

import jakarta.persistence.*;
import lombok.*;

// 신상품 및 인기상품 엔티티
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "item")
public class NewItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemNo; // 상품 고유 번호

    private String itemName; // 상품명

    @Lob
    private String itemDescription; // 상품 설명

    private Integer itemPrice; // 상품 가격

    private Integer itemStock; // 재고 수량

    private Integer itemSoldQty; // 판매된 수량

    private Integer reviewCount; // 리뷰 개수

    private Double averageRating; // 평균 평점

    @Column(name = "creation_date", updatable = false)
    private java.time.LocalDateTime creationDate; // 생성 날짜
}

