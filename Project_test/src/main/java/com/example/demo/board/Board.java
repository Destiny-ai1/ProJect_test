package com.example.demo.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board {
	private Long bno;
	private String title;
	private String writer;
	private String content;
	private LocalDateTime writeTime;
	private Integer readCnt;
	private String SecretPassword;
	private String Comment;
}

