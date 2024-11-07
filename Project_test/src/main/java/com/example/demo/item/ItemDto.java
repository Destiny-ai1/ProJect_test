package com.example.demo.item;

import lombok.Data;

public class ItemDto {
	private ItemDto() { }
	
	@Data
	public static class ItemList {
		private Long item_no;
		private String item_irum;
		private String item_info;
		private Long item_price;
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
}
