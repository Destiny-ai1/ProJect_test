package com.example.demo.comment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Comment {
	private Long cno;
	private String content;
	private String writer;
	private LocalDateTime writeTime;
	private Long bno;
}