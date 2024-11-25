package com.example.demo.comment;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.comment.CommentDto.Read;

@Mapper
public interface CommentDao {

    // 댓글 저장
    Integer save(Long bno, String content, String writer, String writeTime);

    // 댓글 삭제
    void delete(Long cno, String loginId);

    // 글 번호로 댓글 조회
    List<Read> findByBno(Long bno);
}

