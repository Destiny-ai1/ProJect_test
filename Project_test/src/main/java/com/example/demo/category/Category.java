package com.example.demo.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
	private Long cno;
	private String cname;
	private Long pno;
}
