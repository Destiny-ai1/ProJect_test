package com.example.demo.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Board {
	private Long bno;                  								// 글 번호 (Primary Key)
	private String title;              								// 글 제목
	private String writer;             								// 작성자 이름 또는 ID
	private String content;            								// 글 내용
	private LocalDateTime writeTime;   								// 작성 시간
    private Integer readCnt;           								// 조회수
    private Integer Password;       								// 비밀글 비밀번호
    private String comments;           								// 댓글
    private String username;           								// 작성자 ID (외래 키, member 테이블 참조)
    private Integer cno;               								// 카테고리 번호 (외래 키, category 테이블 참조)
}


