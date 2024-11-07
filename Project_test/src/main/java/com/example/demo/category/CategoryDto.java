package com.example.demo.category;

import java.util.List;

import lombok.Data;

@Data
public class CategoryDto {
	private Long cno;
	private String cname;
	private List<CategoryDto> children;
}
