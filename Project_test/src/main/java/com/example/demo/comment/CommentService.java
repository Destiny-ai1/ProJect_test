package com.example.demo.comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.comment.CommentDto.Read;


@Service
public class CommentService {
	@Autowired
	private CommentDao commentDao;
	
	//댓글 작성 후 DB에 저장
	public List<CommentDto.Read> write(Long bno, String content, String writer) {
		String writeTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		commentDao.save(bno, content, writer,writeTime);
		return commentDao.findByBno(bno);
	}
	
	//댓글 삭제
	public List<Read> remove(Long bno, Long cno, String loginId) {
		commentDao.delete(cno, loginId);
		return commentDao.findByBno(bno);
	}
}
