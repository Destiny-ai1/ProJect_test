package com.example.demo.board;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


public class BoardDto {
	public BoardDto( ) {}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Create{
		@NotEmpty
		private String title;
		private String content;
		private String Password;
		
		//고객센터 글을 작성할때
		public Board toEntity(String logintId) {
			return new Board(null, title, logintId, content, LocalDateTime.now(), 0, Integer.parseInt(Password), null, logintId, null);
		}
	}
	
	//고객센터 안에 있는 공지사항+FAQ+Q&A를 출력할때
	@Getter
	@ToString
	public static class BoardList{
		private Long bno;
		private String title;
		private String writer;
		private String writeTime;
		private String readCnt;
	}
	
	//게시글 뒤로 넘기고 하는 작업
	@Getter
	@ToString
	@AllArgsConstructor
	public static class BoardPage{
		private Integer back;                                                 	//이전페이지로
		private Integer start;													//시작하는 페이지 번호
		private Integer end;													//마지막 페이지 번호
		private Integer next;													//다음페이지로
		private Integer pageno;													//현재 있는 페이지 번호
		private List<BoardList> boards;											//페이지당 나오는 게시글 리스트
	}
	
	//공지사항+FAQ+QnA를 조회할때
	@Data
	public static class BoardRead{
		private Long bno;                   									// 글 번호
	    private String title;               									// 제목
	    private String writer;              									// 작성자
	    private String content;             									// 내용
	    private String writeTime;    											// 작성 시간
	    private Integer readCnt;            									// 조회수
	    private Integer password;           									// 비밀글 비밀번호 (Q&A에서 비밀글 여부 확인)
	    private String comments;            									// 댓글
	    
	    
    public BoardDto.BoardRead 글삭제(){
    	this.content ="삭제된 글입니다";
    	return this;
    	}
    public void ReadCnt() {
    	this.readCnt++;
    	}
	}
	
	//공지사항+FAQ 수정할때
	@Data
	public static class update{
		private Long bno;
		private String title;
		private String content;
	
	public Board toEntity() {
		return Board.builder().bno(bno).title(title).content(content).build();
		}
	}
}

