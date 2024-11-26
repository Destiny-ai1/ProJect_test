package com.example.demo.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 카테고리 엔티티
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
	private Long cno;
	private String cname;
	private Long pno;
}
