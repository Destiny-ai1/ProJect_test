package com.example.demo.newitem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class NewItemApiController {

    private final NewItemService newItemService;

    // 신상품 API
    @GetMapping("/new")
    public List<NewItemDto.ItemResponse> getNewItems() {
        String imageUrl = "/api/images?imagename="; // 동적으로 이미지 가져오기
        List<NewItemDto.ItemResponse> newItems = newItemService.getNewItems(30, imageUrl);
        
        // 각 상품에 링크 추가
        newItems.forEach(item -> item.setLink("/item/read/" + item.getItemNo()));
        
        return newItems;
    }

    // 인기상품 API
    @GetMapping("/popular")
    public List<NewItemDto.ItemResponse> getPopularItems() {
        String imageUrl = "/api/images?imagename="; // 동적으로 이미지 가져오기
        List<NewItemDto.ItemResponse> popularItems = newItemService.getPopularItems(100, imageUrl);

        // 각 상품에 링크 추가
        popularItems.forEach(item -> item.setLink("/item/read/" + item.getItemNo()));

        return popularItems;
    }
}
