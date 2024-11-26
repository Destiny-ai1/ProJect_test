package com.example.demo.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 상품 이미지 엔티티
@Getter
@AllArgsConstructor
public class ItemImage {
    private Long imageNo;        // imageNo는 자동 생성되므로, 객체 생성 시 반드시 포함될 필요 없음
    private Long itemNo;         // 아이템 번호 (Long 타입으로 수정)
    private String imageName;    // 이미지 파일 이름 (imageName -> filename으로 수정)
}
