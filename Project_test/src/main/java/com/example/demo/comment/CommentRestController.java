package com.example.demo.comment;


import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
		System.out.println("Content: " + dto.getContent());
		List<CommentDto.Read> comments = commentService.write(bno, dto.getContent(), principal.getName());
			return ResponseEntity.ok(comments);
	}
}
