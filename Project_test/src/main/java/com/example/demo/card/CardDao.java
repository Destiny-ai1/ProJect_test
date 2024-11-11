package com.example.demo.card;

import java.util.*;

import org.apache.ibatis.annotations.*;

@Mapper
public interface CardDao { // 카드 관련 데이터 접근 객체 (DAO)
    // 카드 정보 저장
    @Insert("INSERT INTO card (card_no, ex_date, cvc, member_username) VALUES (#{cardNo}, #{exDate}, #{cvc}, #{memberUsername})")
    int save(Card card); // 카드 정보를 데이터베이스에 저장

    // 카드 번호로 카드 정보 조회
    @Select("SELECT * FROM card WHERE card_no = #{cardNo}")
    Optional<CardDto.Read> findById(@Param("cardNo") Long cardNo); // 특정 카드 번호로 카드 정보 조회

    // 회원 아이디로 카드 정보 조회
    @Select("SELECT * FROM card WHERE member_username = #{memberUsername}")
    List<CardDto.Read> findAllByUsername(@Param("memberUsername") String memberUsername); // 특정 회원의 모든 카드 정보 조회
    
    // 카드 번호 중복 체크
    @Select("SELECT COUNT(*) > 0 FROM card WHERE card_no = #{cardNo}")
    boolean isCardNoExists(@Param("cardNo") Long cardNo); // 카드 번호가 이미 존재하는지 확인

    // 카드 정보 업데이트
    @Update("UPDATE card SET ex_date = #{exDate}, cvc = #{cvc} WHERE card_no = #{cardNo}")
    int update(Card card); // 카드 정보 업데이트

    // 카드 정보 삭제
    @Delete("DELETE FROM card WHERE card_no = #{cardNo}")
    int delete(@Param("cardNo") Long cardNo); // 특정 카드 정보 삭제
}