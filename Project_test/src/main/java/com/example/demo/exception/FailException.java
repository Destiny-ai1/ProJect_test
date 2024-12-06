package com.example.demo.exception;

import java.sql.SQLException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FailException extends RuntimeException {
    public FailException(String message) {
    	super(message);
    }
}
