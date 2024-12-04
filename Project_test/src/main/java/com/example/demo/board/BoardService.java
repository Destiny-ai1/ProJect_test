package com.example.demo.board;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.comment.CommentDao;
import com.example.demo.comment.CommentDto;
import com.example.demo.exception.FailException;
import com.example.demo.member.MemberDao;
import com.example.demo.security.SecurityUtils;






@Service
public class BoardService {
	@Autowired 
    private BoardDao boardDao;
	@Autowired
	private MemberDao readDao;
	@Autowired
	private CommentDao commentDao;
	
	// 고객센터 QnA 작성 서비스 (회원은 Q&A 작성, 관리자는 공지사항과 FAQ 작성)
	public Long write(BoardDto.Create dto, String loginId) {
	    // 기존 게시글 번호 조회
	    Long lastBno = boardDao.findBnoByCategory(dto.getCno());
	    Long newBno = (lastBno != null ? lastBno : 0) + 1;

	    // 작성자 이름 가져오기
	    String name = readDao.UserDetails(loginId).getName();

	    // 공지사항일 경우 비밀번호와 비밀글 설정 무시
	    if (dto.getCno() == 1001) {
	        dto.setPassword(null);
	        dto.setSecretBoard(false);
	    }

	    // Q&A일 경우 비밀글 설정 검증
	    if (dto.getCno() == 1002 && dto.isSecretBoard()) {
	        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
	            throw new IllegalArgumentException("Q&A 비밀글에는 비밀번호를 입력해야 합니다.");
	        }
	    }

	    // DTO를 엔티티로 변환
	    Board board = dto.toEntity(loginId, name);
	    // 엔티티에 새 게시글 번호 설정
	    board.setBno(newBno);
	    
	    // 게시글 저장
	    boardDao.save(board);
	    // 저장된 게시글 번호 반환
	    return board.getBno();
	}
	
	// 고객센터 공지사항+FAQ+Q&A를 조회할 때
	@Transactional
	public BoardDto.BoardRead Board_senter(Long bno, String loginId, String password) {
	    // 1. 게시글 데이터 조회
	    BoardDto.BoardRead dto = boardDao.findById(bno)
	        .orElseThrow(() -> new FailException("글을 찾을 수 없습니다.")); // 게시글이 없는 경우 예외 발생   
	    System.out.println("SecretBoard 상태: " + dto.isSecretBoard());
	    
	    // 2. 삭제된 글인지 확인
	    if (dto.isBoard_delete()) {
	        return dto.글삭제(); // 삭제된 글의 경우, 해당 내용을 반환
	    }

	    // 3. 비밀글 접근 제어
	    if (dto.isSecretBoard()) {
	        String userName = readDao.UserDetails(loginId).getName(); // 로그인 사용자 이름 조회      
	        // 관리자 또는 작성자라면 비밀번호 검증 없이 접근 허용
	        if (SecurityUtils.isAdmin() || (loginId != null && dto.getWriter().equals(userName))) {
	           
	            return dto; // 작성자나 관리자는 비밀번호 없이 접근 가능
	        }
	        
	        System.out.println("DTO SecretBoard 상태: " + dto.isSecretBoard());
	        System.out.println("DTO Password: " + dto.getPassword());

	        // 비밀번호가 없거나 잘못된 경우 접근 차단
	        if (password == null || !dto.getPassword().equals(password)) {
	            throw new FailException("비밀글 입니다. 비밀번호를 입력해주세요.");
	        }
	    }

	    // 4. 조회수 증가 로직 (작성자 제외)
	    if (loginId != null && !dto.getWriter().equals(readDao.UserDetails(loginId).getName())) {
	        boolean isWriter = dto.getWriter().equals(readDao.UserDetails(loginId).getName());
	        boolean readCheck = readDao.existsByBnoAndloginId(bno, loginId);

	        // 작성자가 아니고, 이전에 읽은 기록이 없을 경우 조회수 증가
	        if (!isWriter && !readCheck) {
	            readDao.read_save(loginId, bno); // 읽은 기록 저장
	            boardDao.increaseReadCnt(bno); // DB 조회수 증가
	            dto.ReadCnt(); // DTO 조회수 증가 반영
	        }
	    }

	    // 5. 댓글 조회 추가
	    List<CommentDto.Read> comments = commentDao.findByBno(bno); // 해당 게시글의 댓글 목록 조회
	    dto.setComments(comments); // 댓글 데이터를 DTO에 추가
	    return dto;
	}
	

	//페에지당 보여지는 게시물 리스트 수
	@Value("10")
	private int Page_Size;
	//페이지당 보여지는 블록 수
	@Value("5")
	private int Block_Size;
	
	public BoardDto.BoardPage findall(Integer pageno ,String cno){
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
		
		Map<String, Object> params = new HashMap<>();
		params.put("cno", cno);	
        params.put("startRowNum", startRowNum);
        params.put("endRowNum", endRowNum);
        
		List<BoardDto.BoardList> boards = boardDao.findAll(params);				//처음부터 마지막까지 사이에있는 게시물 조회
		
		return new BoardDto.BoardPage(back, start ,end , next, pageno, boards);
	}
	
	//글을 수정할때
	public void Board_update(BoardDto.update dto, String loginId) {
		
	    BoardDto.BoardRead board = boardDao.findById(dto.getBno()).orElseThrow(() -> new FailException("삭제된 글입니다."));
	    
	    String userName = readDao.UserDetails(loginId).getName();
	   
	    if (!board.getWriter().equals(userName)) {
	        throw new FailException("작성자만 글을 수정할 수 있습니다.");
	    }
	    Board entity = dto.toEntity(); 								
	    boardDao.Board_update(entity);
	}
	
	//삭제한 글일때
	public void Board_delete(long bno, String loginId) {
		// 게시글을 가져옴
		BoardDto.BoardRead board = boardDao.findById(bno).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
		
		String userName = readDao.UserDetails(loginId).getName();
		
	    // 로그인한 아이디의 작성자 이름확인
	    if (!board.getWriter().equals(userName) && !SecurityUtils.isAdmin()) {
	        throw new SecurityException("작성자와 관리자만이 삭제할 수 있습니다.");
	    }

	    // 게시글 삭제
	    boardDao.Board_delete(bno);
	}
}
