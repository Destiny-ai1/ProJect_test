package com.example.demo.item;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class ItemDto {
	private ItemDto() { }
	
	@Data
	public static class ItemList {
		private Long itemNo;
		private String itemIrum;
		private String itemInfo;
		private Integer itemPrice;
		private String itemImage;
	}
	
	@Data
	public static class Read {
	    private Long itemNo;
	    private String itemIrum;
	    private String itemInfo;
	    private Integer itemPrice;
	    private Integer itemJango;
	    private Integer itemSellQty;
	    private Integer addGoodCnt;
	    private Integer reviewEa;
	    private Integer cno;
	    
	    // 이미지를 담을 리스트
	    private List<String> itemImages;
	}

	
	@Data
	public static class Create {
		private Long itemNo;
		@NotEmpty(message="제품명을 입력하세요")
		private String itemIrum;
		@NotEmpty(message="제품 정보를 입력하세요")
		private String itemInfo;
		@DecimalMin(value="1000", message="가격은 1000원이상이어야합니다")
		private Integer itemPrice;
		@DecimalMin(value="1", message="잔고는 1개이상이어야 합니다")
		private Integer itemJango;
		private Integer itemSellQty;
		private Integer addGoodCnt;
		private Integer reviewEa;
		private Integer cno;
		
		private List<MultipartFile> itemImages;
		
		public Item toEntity() {
			return new Item(null, itemIrum, itemInfo, itemPrice, itemJango, itemSellQty, addGoodCnt, reviewEa, cno);
		}
	}
	
	@Data
	public static class Pnos {
		@NotNull(message="상품을 선택하세요")
		private List<Long> pnos;
	}
}
