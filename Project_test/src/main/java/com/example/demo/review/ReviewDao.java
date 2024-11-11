package com.example.demo.review;

import java.util.*;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ReviewDao { // 리뷰 관련 데이터 접근 객체 (DAO)
    // 리뷰 저장
    @Insert("INSERT INTO review (review_no, review_write_time, review_writer, review_good_cnt, username, order_no) VALUES (#{reviewNo}, #{reviewWriteTime}, #{reviewWriter}, #{reviewGoodCnt}, #{username}, #{orderNo})")
    int save(Review review); // 리뷰를 데이터베이스에 저장

    // 주문 번호로 리뷰 조회
    @Select("SELECT * FROM review WHERE order_no = #{orderNo}")
    List<ReviewDto.Read> findByOrderNo(@Param("orderNo") Long orderNo); // 특정 주문의 모든 리뷰 조회

    // 리뷰 번호로 리뷰 조회
    @Select("SELECT * FROM review WHERE review_no = #{reviewNo}")
    Optional<ReviewDto.Read> findById(@Param("reviewNo") Long reviewNo); // 특정 리뷰 조회

    // 리뷰 업데이트
    @Update("UPDATE review SET review_good_cnt = #{reviewGoodCnt}, review_writer = #{reviewWriter}, review_write_time = #{reviewWriteTime} WHERE review_no = #{reviewNo}")
    int update(Review review); // 리뷰 정보 업데이트

    // 리뷰 삭제
    @Delete("DELETE FROM review WHERE review_no = #{reviewNo}")
    int delete(@Param("reviewNo") Long reviewNo); // 특정 리뷰 삭제
}