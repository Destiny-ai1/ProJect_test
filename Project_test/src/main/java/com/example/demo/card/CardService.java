package com.example.demo.card;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.FailException;

@Service
public class CardService { // 카드 비즈니스 로직을 처리하는 서비스 클래스

    @Autowired
    private CardDao cardDao; // 카드 데이터 접근 객체 (DAO)

    @Transactional
    public void addCard(CardDto.Create dto) { // 카드 정보를 추가하는 메소드
        Card card = dto.toEntity(); // DTO를 엔티티로 변환
        cardDao.save(card); // 카드 정보 저장
    }

    public List<CardDto.Read> getCards(String memberUsername) { // 특정 회원의 카드 정보를 조회하는 메소드
        return cardDao.findAllByUsername(memberUsername); // 회원의 모든 카드 정보 조회
    }

    @Transactional
    public void updateCard(CardDto.Update dto) { // 카드 정보를 업데이트하는 메소드
        Card card = dto.toEntity(); // DTO를 엔티티로 변환
        int updatedRows = cardDao.update(card); // 카드 정보 업데이트
        if (updatedRows == 0) { // 업데이트된 항목이 없는 경우 예외 발생
            throw new FailException("카드 정보를 찾을 수 없습니다");
        }
    }

    @Transactional
    public void removeCard(Long cardNo) { // 카드 정보를 삭제하는 메소드
        int deletedRows = cardDao.delete(cardNo); // 카드 정보 삭제
        if (deletedRows == 0) { // 삭제된 항목이 없는 경우 예외 발생
            throw new FailException("카드 정보를 찾을 수 없습니다");
        }
    }
}