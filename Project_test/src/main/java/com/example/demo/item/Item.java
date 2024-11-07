package com.example.demo.item;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Item {
	public Long item_no;
	public String item_irum;
	public String item_info;
	public Long item_price;
	public Long item_jango;
	public Long item_sell_qty;
	public Long add_good_cnt;
	public Long review_ea;
	public Long cno;
}
