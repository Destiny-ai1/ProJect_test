package com.example.demo.item;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

public class ItemDto {
	private ItemDto() { }
	
	@Data
	public static class ItemList {
		private Long item_no;
		private String item_irum;
		private String item_info;
		private Long item_price;
		private String image;
	}
	
	@Data
	public static class Read {
	    private Long item_no;
	    private String item_irum;
	    private String item_info;
	    private Long item_price;
	    private Long item_jango;
	    private Long item_sell_qty;
	    private Long add_good_cnt;
	    private Long review_ea;
	    private Long cno;
	}
	
	@Data
	public static class Create {
		@NotEmpty(message="제품명을 입력하세요")
		private String item_no;
		@NotEmpty(message="제품 이름을 입력하세요")
		private String item_info;
		@NotEmpty(message="제품 정보를 입력하세요")
		private Long item_price;
		@NotEmpty(message="제품 잔고를 입력하세요")
		private Long item_jango;
		
		private List<MultipartFile> image;
		
		public Item toEntity() {
			return new Item(null, item_no, item_info, item_price, item_jango, null, null, null, null);
		}
	}
}
