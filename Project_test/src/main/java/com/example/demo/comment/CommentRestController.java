package com.example.demo.comment;


import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


@RequestMapping("/api/comments")
@PreAuthorize("isAuthenticated()")
@RestController
public class CommentRestController {
	@Autowired
	private CommentService commentService;

	@PostMapping("/{bno}")
	public ResponseEntity<?> writeComment(@PathVariable Long bno,@Valid @RequestBody CommentDto.Create dto,Principal principal) {
		List<CommentDto.Read> comments = commentService.write(bno, dto.getContent(), principal.getName());
			return ResponseEntity.ok(comments);
	}
	
	@DeleteMapping("/{bno}/{cno}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComment(@PathVariable Long bno, @PathVariable Long cno, Principal principal) {
        String loginId = principal.getName(); 										// 현재 로그인한 사용자의 ID 가져오기
        List<CommentDto.Read> comments = commentService.remove(bno, cno, loginId); 	// 댓글 삭제 및 해당 게시글의 댓글 목록 조회
        return ResponseEntity.ok(comments);
    }
}
