package com.example.demo.comment;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

public class CommentDto {
	private CommentDto() {}
	
	@Data
	public static class Read {
		private Long cno;
		private String content;
		private String writer;
		private String writeTime;
	}
	
	//댓글생성
	@Data
	public static class Create {
        @NotEmpty
        private String content; // 댓글 내용
    }
	
}
