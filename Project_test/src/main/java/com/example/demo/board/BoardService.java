package com.example.demo.board;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.board.BoardDto.BoardRead;

@Service
public class BoardService {
	@Autowired 
    private BoardDao boardDao;
	
	//고객센터 QnA작성할때
	public Long write(BoardDto.Create dto, String loginId) {
		Board board= dto.toEntity(loginId);
		boardDao.save(board);
		return board.getBno();
	}
	
	//고객센터 공지사항+FAQ+Q&A를 조회할때
	public BoardRead Board_senter(Long bno, String categoryType,String loginId) {
		BoardDto.BoardRead dto=boardDao.findById(bno).orElseThrow(()-> new FailException("글을 찾을수 없습니다."));
		//0.삭제된 글이라면 내용을 삭제된 글이라고 변경
		if("삭제된 글입니다".equals(dto.getContent())) {
			return dto.글삭제();
		}
		
		//1. 비로그인이거나 내가 작성한 글이면 조회수 변경 없음
		if(loginId==null || dto.getWriter().equals(loginId))
			return dto;
			
		//2. 로그인을 했거나 ,남이 작성한글인경우 조회수 증가
		boolean ReadCheck= memeberReadBoardDao.existsByBnoAndloginId(bno,loginId);
		if(ReadCheck==false) {
			memberReadBoardDao.save(bno,loginId);
			boardDao.increaseReadCnt(bno);
			dto.ReadCnt();
		}
		return dto;
	}
	
}
