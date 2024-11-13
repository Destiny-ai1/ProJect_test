package com.example.demo.address;

import java.util.*;

import org.apache.ibatis.annotations.*;

@Mapper
public interface AddressDao {
    // 배송지 정보 저장
    //@Insert("INSERT INTO address (address_main, post_no, address_road, address_detail, address_name, member_username) VALUES (#{addressMain}, #{postNo}, #{addressRoad}, #{addressDetail}, #{addressName}, #{memberUsername})")
    // 어노테이션을 넣으면 mapper가 안읽힘
	public int save(Address address);

    // 주소 번호로 배송지 조회
    //@Select("SELECT * FROM address WHERE address_no = #{addressNo}")
    Optional<AddressDto.Read> findById(@Param("addressNo") Long addressNo);

    // 회원 아이디로 모든 배송지 조회
    //@Select("SELECT * FROM address WHERE member_username = #{memberUsername}")
    List<AddressDto.Read> findAllByUsername(@Param("memberUsername") String memberUsername);

    // 배송지 정보 업데이트
    //@Update("UPDATE address SET address_detail = #{addressDetail}, address_name = #{addressName} WHERE address_no = #{addressNo}")
    int update(Address address);

    // 배송지 삭제
    //@Delete("DELETE FROM address WHERE address_no = #{addressNo}")
    int delete(@Param("addressNo") Long addressNo);
}
