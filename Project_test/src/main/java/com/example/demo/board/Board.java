package com.example.demo.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Board {
	private Long bno;                  								// 글 번호 (Primary Key)
	private String title;              								// 글 제목
	private String writer;             								// 작성자 이름
	private String content;            								// 글 내용
	private LocalDateTime writeTime;   								// 작성 시간
    private Integer readCnt;           								// 조회수
    private String password;       									// 비밀글 비밀번호
    private boolean SecretBoard;									// 비밀글 확인여부
    private boolean board_delete;           						// 글 삭제
    private String username;           								// 작성자 ID (외래 키, member 테이블 참조)
    private Integer cno;               								// 카테고리 번호 (외래 키, category 테이블 참조)
    
 // bno 필드에 대한 setter 메서드 추가
    public void setBno(Long bno) {
        this.bno = bno;
    }
}


