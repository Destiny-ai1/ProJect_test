package com.example.demo.item;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Entity
@Table(name = "items")
public class Item {
	@Id
	public Long itemNo;
	
	public String itemIrum;
	public String itemInfo;
	public Integer itemPrice;
	public Integer itemJango;
	public Integer itemSellQty;
	public Integer addGoodCnt;
	public Integer reviewEa;
	public Integer cno;
}
