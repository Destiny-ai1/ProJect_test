package com.example.demo.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    @NotNull
    private Long itemNo;
    @NotNull
    private String itemName;
    @NotNull
    private String image;
    @NotNull
    private int detailEa;

    // DTO -> Entity 변환 메소드 추가
    public OrderDetail toEntity() {
        return OrderDetail.builder()
                .itemNo(itemNo)
                .itemName(itemName)
                .image(image)
                .detailEa(detailEa)
                .build();
    }
}