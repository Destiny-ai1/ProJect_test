package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice는 모든 컨트롤러에서 발생하는 예외를 처리하는 클래스입니다.
@RestControllerAdvice
public class GlobalExceptionHandler {

	// FailException을 처리하는 핸들러
	@ExceptionHandler(FailException.class)
	public ResponseEntity<String> handleFailException(FailException ex) {
	    // 메시지만 클라이언트에 전달 (단순히 ex.getMessage()만 전달)
	    return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);  // 409 Conflict
	}
    /*
    // 다른 예외를 처리할 수 있는 핸들러 추가 가능
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // 예외 메시지 로그
        System.err.println("Unexpected Error: " + ex.getMessage());
        
        // 일반적인 예외 발생 시
        return new ResponseEntity<>("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    */
}
