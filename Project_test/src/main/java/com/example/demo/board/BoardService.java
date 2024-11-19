package com.example.demo.board;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.FailException;
import com.example.demo.member.MemberDao;







@Service
public class BoardService {
	@Autowired 
    private BoardDao boardDao;
	@Autowired
	private MemberDao readDao;
	
	//고객센터 QnA작성할때 (회원은 Q&A작성, 관리자는 공지사항이랑 FAQ 작성)
	public Long write(BoardDto.Create dto, String loginId) {
		Board board= dto.toEntity(loginId);
		boardDao.save(board);
		return board.getBno();
	}
	
	//고객센터 공지사항+FAQ+Q&A를 조회할때
	@Transactional
	public BoardDto.BoardRead Board_senter(Long bno,String loginId) {
		//DB에 저장되어있는글인지 먼저확인
		BoardDto.BoardRead dto=boardDao.findById(bno).orElseThrow(()-> new FailException("글을 찾을수 없습니다."));
		//0.삭제된 글이라면 내용을 삭제된 글이라고 변경
		if(dto.isBoard_delete()) {
			return dto.글삭제();
		}
		
		// 비로그인이거나 내가 작성한 글이면 조회수 변경 없음
		if(loginId==null || dto.getWriter().equals(loginId))
			return dto;
			
		// 로그인을 했거나,남이 작성한글인경우 조회수 증가시키고 중복을 방지하기위해 저장한다
		boolean ReadCheck= readDao.existsByBnoAndloginId(bno,loginId);
		if(!ReadCheck) {
			readDao.read_save(loginId, bno);
			boardDao.increaseReadCnt(bno);
			dto.ReadCnt();
		}
		return dto;
	}
	
	
	//페에지당 보여지는 게시물 리스트 수
	@Value("10")
	private int Page_Size;
	//페이지당 보여지는 블록 수
	@Value("5")
	private int Block_Size;
	
	public BoardDto.BoardPage findall(Integer pageno){
		int CountOfBoard= boardDao.Count();									//전체 게시물 갯수
		int NumberOfPage= (CountOfBoard-1)/10+1;							//전체 나오는 페이지수 계산
		
		int back= (pageno-1)/Block_Size*Block_Size;							//이전페이지로
		int start= back+1;													//시작하는 페이지 번호
		int end = back+Block_Size;											//마지막 페이지의 번호
		int next= end+1;													//다음페이지로
		
		if(end>=NumberOfPage) {
			end=NumberOfPage;												//마지막페이지를 전체페이지로 제한
			next=0;															//마지막페이지에 있다면 넘기는값을 0으로 설정
		}
		
		int startRowNum = (pageno-1)* Page_Size + 1;						//현재 페이지의 시작하는 번호
		int endRowNum = startRowNum + Page_Size -1;							//현재 페이지의 마지막 번호
		List<BoardDto.BoardList> boards = boardDao.findAll(startRowNum, endRowNum);				//처음부터 마지막까지 사이에있는 게시물 조회
		
		return new BoardDto.BoardPage(back, start ,end , next, pageno, boards);
	}
	
	//글을 수정할때
	public void Board_update(BoardDto.update dto, String loginId) {
		BoardDto.BoardRead board= boardDao.findById(dto.getBno()).orElseThrow(()-> new FailException("삭제된 글 입니다"));
		if(!board.getTitle().equals(loginId))
			throw new FailException("삭제되었거나 글을 읽을 권환이 없습니다");
		boardDao.Board_update(dto.toEntity());
	}
	
	//삭제한 글일때
	public void Board_delete(long bno, String loginId) {
		BoardDto.BoardRead board= boardDao.findById(bno).orElseThrow(()-> new FailException("삭제된 글 입니다"));
		if(!board.getWriter().equals(loginId))
			throw new FailException("삭제되었거나 글을 읽을 권환이 없습니다");
		boardDao.Board_delete(bno);
	}
}
