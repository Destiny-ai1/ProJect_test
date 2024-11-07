package com.example.demo.board;


import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface BoardDao {
	public int save(Board board);

	public Optional<BoardDto.BoardRead> findById(Long bno);
	
	@Update("update board set read_cnt=read_cnt+1 where bno=#{bno} and rownum=1")
	public void increaseReadCnt(Long bno);
	
	
	
}
