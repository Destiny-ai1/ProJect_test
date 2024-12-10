package com.example.demo.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminDao {
    List<AdminDto.User> getAllUser(); // 모든 사용자 정보 가져오기

    AdminDto.User findUserByUsername(String username); // 특정 사용자 정보 가져오기

    void updateUserStatus(@Param("username") String username, @Param("enabled") boolean enabled); // 사용자 상태 업데이트

    List<AdminDto.User> findUsersByCriteria(@Param("search") String search, @Param("status") String status); // 검색 및 필터링
}

