package com.example.demo.item;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ItemDto {
    private ItemDto() { }

    // 상품 목록을 위한 DTO (카테고리 번호 포함)
    @Data
    public static class ItemList {
        private Long itemNo;      // 상품 번호
        private String itemIrum;  // 상품명
        private String itemInfo;  // 상품 정보
        private Integer itemPrice; // 가격
        private Integer itemJango;	// 상품 재고
        private Integer itemSellQty; // 상품 판매수
        private Long reviewEa;		// 리뷰 갯수
        private String itemImage; // 이미지 URL
        private Long cno;         // 카테고리 번호 (cno 추가)
        private Double avgRating;	// 리뷰 평균 평점
        // 재고 상태 메시지
     	private String stockMessage; 
    }

    // 상품 상세 정보를 위한 DTO (카테고리 번호 포함)
    @Data
    public static class Read {
        private Long itemNo;       // 상품 번호
        private String itemIrum;   // 상품명
        private String itemInfo;   // 상품 정보
        private Integer itemPrice; // 가격
        private Integer itemJango; // 잔고
        private Integer itemSellQty; // 판매 수량
        private String  itemSize; // 좋아요 수
        private Integer reviewEa;   // 리뷰 수
        private Integer cno;        // 카테고리 번호 (cno 추가)
        // 이미지를 담을 리스트
        private List<String> itemImages;
        // 재고 상태 메시지
		private String stockMessage; 
		private Double avgRating; // 평균 평점 추가
		
		// 상품 사이즈 목록을 추가하는 메소드
		private List<String> sizes = Arrays.asList("Small", "Middle", "Larg", "XLarge");
    }

    // 상품 생성/수정 요청 DTO
    @Data
    public static class Create {
        private Long itemNo;        // 상품 번호
        @NotEmpty(message="제품명을 입력하세요")
        private String itemIrum;    // 상품명
        @NotEmpty(message="제품 정보를 입력하세요")
        private String itemInfo;    // 상품 정보
        @DecimalMin(value="1000", message="가격은 1000원이상이어야합니다")
        private Integer itemPrice;  // 가격
        @DecimalMin(value="1", message="잔고는 1개이상이어야 합니다")
        private Integer itemJango;  // 잔고
        private Integer itemSellQty; // 판매 수량
        private String itemSize; // 좋아요 수
        private Integer reviewEa;   // 리뷰 수
        private Integer cno;        // 카테고리 번호 (cno 추가)

        private List<MultipartFile> itemImages; // 이미지 파일 리스트

        // Entity 객체로 변환
        public Item toEntity() {
            return new Item(null, itemIrum, itemInfo, itemPrice, itemJango, itemSellQty, itemSize, reviewEa, cno);
        }
    }

    // 상품 삭제를 위한 DTO
    @Data
    public static class Inos {
        @Size(min = 1, message = "상품을 하나 이상 선택해야 합니다.")
        private List<ItemDeleteDTO> items; // itemNo와 itemSize를 포함하는 리스트

        public List<ItemDeleteDTO> getItems() {
            return items;
        }

        public void setItems(List<ItemDeleteDTO> items) {
            this.items = items;
        }
    }

    // 상품 삭제를 위한 itemNo와 itemSize를 포함하는 DTO
    @Data
    public static class ItemDeleteDTO {
        private Long itemNo;    // 상품 번호
        private String itemSize; // 상품 사이즈

        public ItemDeleteDTO(Long itemNo, String itemSize) {
            this.itemNo = itemNo;
            this.itemSize = itemSize;
        }
    }
    
    @Data
    public static class price_update {
    	private Long itemNo;
    	private Integer itemPrice;
    }
}
