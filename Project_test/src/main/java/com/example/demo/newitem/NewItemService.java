package com.example.demo.newitem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewItemService {

    private final NewItemDao newItemDao;

    /**
     * 최근 신상품 조회
     * @param days 최근 며칠 내의 상품을 조회할지 지정
     * @param imageUrl 이미지 경로를 포함한 URL
     * @return 최근 신상품 리스트
     */
    public List<NewItemDto.ItemResponse> getNewItems(int days, String imageUrl) {
        System.out.println("[DEBUG] Fetching new items from past " + days + " days with image URL: " + imageUrl);

        List<NewItemDto.ItemResponse> newItems = newItemDao.findNewItems(days, imageUrl);

        if (newItems == null || newItems.isEmpty()) {
            System.out.println("[WARNING] No new items found for the past " + days + " days.");
        } else {
            System.out.println("[DEBUG] Retrieved new items: " + newItems);
        }

        return newItems;
    }

    /**
     * 인기 상품 조회
     * @param minSales 최소 판매량 기준
     * @param imageUrl 이미지 경로를 포함한 URL
     * @return 인기 상품 리스트
     */
    public List<NewItemDto.ItemResponse> getPopularItems(int minSales, String imageUrl) {
        System.out.println("[DEBUG] Fetching popular items with minimum sales: " + minSales + " and image URL: " + imageUrl);

        List<NewItemDto.ItemResponse> popularItems = newItemDao.findPopularItems(minSales, imageUrl);

        if (popularItems == null || popularItems.isEmpty()) {
            System.out.println("[WARNING] No popular items found with minimum sales: " + minSales);
        } else {
            System.out.println("[DEBUG] Retrieved popular items: " + popularItems);
        }

        return popularItems;
    }
}


