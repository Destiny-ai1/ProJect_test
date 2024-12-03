package com.example.demo.board;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;





@Mapper
public interface BoardDao {
	//게시한글을 저장
	public int save(Board board);
	
	//고객센터 공지사항+FAQ+Q&A를 조회
	Optional<BoardDto.BoardRead> findById(Long bno);
	
	@Update("update board set read_cnt=read_cnt+1 where bno=#{bno}")
	public void increaseReadCnt(@Param("bno")Long bno);
	
	int Count();

	public List<BoardDto.BoardList> findAll(Map<String, Object> params);
	
	//게시글 업데이트
	public void Board_update(Board entity);
	
	//게시글삭제
	public void Board_delete(long bno);

	public Long findBnoByCategory(Integer cno);
	
}
