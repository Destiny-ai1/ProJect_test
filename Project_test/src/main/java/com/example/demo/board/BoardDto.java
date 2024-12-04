package com.example.demo.board;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.comment.CommentDto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class BoardDto {
    public BoardDto() {}

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotEmpty(message = "제목을 입력해주세요.")
        private String title;
        @NotEmpty(message = "내용을 입력해주세요.")
        private String content;
        private String password; // 비밀글 password
        private boolean SecretBoard; // 비밀글 확인 여부
        private Integer cno; // 카테고리 번호 추가

        // 고객센터 글을 작성할때
        public Board toEntity(String loginId, String name) {
            // 디버깅: Entity로 변환 전 필드 확인
            return new Board(null, title, name, content, LocalDateTime.now(), 0,
                    (password != null && !password.isBlank() ? password : null), SecretBoard, false, loginId, cno);
        }
    }

    // 고객센터 안에 있는 공지사항+FAQ+Q&A를 출력할 때
    @Data
    @ToString
    public static class BoardList {
        private Long bno;
        private String title;
        private String writer;
        private String writeTime;
        private String readCnt;
        private Integer cno;
        private String cname;
        private boolean SecretBoard; 

    }

    // 게시글 뒤로 넘기고 하는 작업
    @Getter
    @ToString
    @AllArgsConstructor
    public static class BoardPage {
        private Integer back; // 이전 페이지로
        private Integer start; // 시작하는 페이지 번호
        private Integer end; // 마지막 페이지 번호
        private Integer next; // 다음 페이지로
        private Integer pageno; // 현재 있는 페이지 번호
        private List<BoardList> boards; // 페이지당 나오는 게시글 리스트
    }

    // 공지사항+FAQ+QnA를 조회할 때
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardRead {
        private Long bno; // 글 번호
        private String title; // 제목
        private String writer; // 작성자
        private String content; // 내용
        private String writeTime; // 작성 시간
        private Integer readCnt = 0; // 조회수
        private String Password; // 비밀글 비밀번호 (Q&A에서 비밀글 여부 확인)
        private boolean SecretBoard; // 비밀글 여부 추가
        private List<CommentDto.Read> comments; // 댓글
        private boolean board_delete; // 글 삭제
        private Integer cno; // 카테고리 번호
        private String cname;

        public void setIsDeleted(boolean board_delete) {
            this.board_delete = board_delete;
        }

        public BoardDto.BoardRead 글삭제() {
            this.content = "삭제된 글입니다";
            return this;
        }

        public void ReadCnt() {
            if (readCnt == null) {
                readCnt = 0;
            }
            this.readCnt++;
        }

        public boolean isSecret_Board() {
            return SecretBoard;
        }
    }

    // 공지사항+FAQ 수정할 때
    @Data
    public static class update {
        private Long bno;
        private String title;
        private String content;

        public Board toEntity() {
            return Board.builder().bno(bno).title(title).content(content).build();
        }
    }
}
