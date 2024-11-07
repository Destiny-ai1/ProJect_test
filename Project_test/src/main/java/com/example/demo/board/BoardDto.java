package com.example.demo.board;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BoardDto {
	public BoardDto( ) {}
	
	private Long bno;
    private String title;
    private String writer;
    private String content;
    private LocalDateTime writeTime;
    private Integer readCnt;
    private String SecretPassword;
    private String comments;
}

