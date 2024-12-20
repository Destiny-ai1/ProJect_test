package com.example.demo.newitem;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewItemDao {
    // 신상품 조회
    List<NewItemDto.ItemResponse> findNewItems(@Param("days") int days, @Param("imageUrl") String imageUrl);

    // 인기상품 조회
    List<NewItemDto.ItemResponse> findPopularItems(@Param("minSales") int minSales, @Param("imageUrl") String imageUrl);

    // 상품 저장
    void saveNewItem(NewItemDto.CreateRequest request);
}
