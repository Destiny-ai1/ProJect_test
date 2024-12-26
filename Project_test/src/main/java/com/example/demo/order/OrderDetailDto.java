package com.example.demo.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDto {
    private Long orderNo; // 주문 번호 (null을 허용하도록 변경)
    @NotNull
    private Long itemNo;  // 상품 번호
    @NotNull
    private String itemName; // 상품 이름
    @NotNull
    private String image; // 이미지 URL
    @NotNull
    private Long detailEa; // 수량
    @NotNull
    private Long price; // 가격
    @NotNull
    private String itemSize; // 상품 사이즈
    @NotNull
    private String reviewWritten; // 리뷰 작성 여부 (Y/N)

    // DTO -> Entity 변환 메소드 추가
    public OrderDetail toEntity() {
        return OrderDetail.builder()
                .orderNo(orderNo != null ? orderNo : null) // 주문 번호가 null일 경우 처리
                .itemNo(itemNo)
                .itemName(itemName)
                .image(image)
                .detailEa(detailEa)
                .price(price) // 가격 설정 추가
                .itemSize(itemSize != null ? itemSize : "DEFAULT_SIZE") // itemSize가 null일 경우 기본값 처리
                .reviewWritten(reviewWritten) // 리뷰 작성 여부 추가
                .build();
    }
}
