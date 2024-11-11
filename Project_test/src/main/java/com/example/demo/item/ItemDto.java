package com.example.demo.item;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

public class ItemDto {
	private ItemDto() { }
	
	@Data
	public static class ItemList {
		private Long itemNo;
		private String itemIrum;
		private String itemInfo;
		private Long itemPrice;
		private String image;
	}
	
	@Data
	public static class Read {
	    private Long itemNo;
	    private String itemIrum;
	    private String itemInfo;
	    private Long itemPrice;
	    private Long itemJango;
	    private Long itemSellQty;
	    private Long addGoodCnt;
	    private Long reviewEa;
	    private Long cno;
	}
	
	@Data
	public static class Create {
		@NotEmpty(message="제품명을 입력하세요")
		private String itemNo;
		@NotEmpty(message="제품 이름을 입력하세요")
		private String itemInfo;
		@NotEmpty(message="제품 정보를 입력하세요")
		private Long itemPrice;
		@NotEmpty(message="제품 잔고를 입력하세요")
		private Long itemJango;
		
		private List<MultipartFile> image;
		
		public Item toEntity() {
			return new Item(null, itemNo, itemInfo, itemPrice, itemJango, null, null, null, null);
		}
	}
}
