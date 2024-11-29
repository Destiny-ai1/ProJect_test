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
    private int detailEa; // 수량
    @NotNull
    private int price; // 가격
    @NotNull
    private String reviewWritten; // 리뷰 작성 여부 (Y/N)

    // DTO -> Entity 변환 메소드 추가
    public OrderDetail toEntity() {
        return OrderDetail.builder()
                .orderNo(orderNo) // 주문 번호 설정 추가 (없을 수도 있음)
                .itemNo(itemNo)
                .itemName(itemName)
                .image(image)
                .detailEa(detailEa)
                .price(price) // 가격 설정 추가
                .reviewWritten(reviewWritten) // 리뷰 작성 여부 추가
                .build();
    }
}
