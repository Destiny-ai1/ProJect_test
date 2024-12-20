package com.example.demo.newitem;

import lombok.Data;

public class NewItemDto {

	@Data
    public static class ItemResponse {
        private Long itemNo;
        private String itemName;
        private String imageUrl;
        private Integer price;
        private Integer stock;
        private String link;
    }

    @Data
    public static class CreateRequest {
        private String itemName;    // 상품명
        private String itemInfo;    // 상품 정보
        private Integer itemPrice;  // 가격
        private Integer itemStock;  // 재고
        private String imageUrl;    // 이미지 URL
    }
}