package com.example.demo.member;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberReadBoardDao {

	static void save(Long bno, String loginId) {
		// TODO Auto-generated method stub
		
	}

	static boolean existsByBnoAndloginId(Long bno, String loginId) {
		// TODO Auto-generated method stub
		return false;
	}


}
