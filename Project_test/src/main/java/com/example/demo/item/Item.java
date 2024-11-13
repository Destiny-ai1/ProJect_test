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
	public Long itemNo;
	public String itemIrum;
	public String itemInfo;
	public Long itemPrice;
	public Long itemJango;
	public Long itemSellQty;
	public Long addGoodCnt;
	public Long reviewEa;
	public Long cno;
}
