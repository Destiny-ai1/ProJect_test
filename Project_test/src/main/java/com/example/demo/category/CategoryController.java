package com.example.demo.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.item.ItemDto;
import com.example.demo.item.ItemService;

@Controller
public class CategoryController {

    @Autowired
    private ItemService itemService; // 추가된 itemService, 상품 정보 조회용

    @GetMapping("/category/{cno}/items")
    public ModelAndView getItemsByCategory(
            @PathVariable Long cno,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Long excludeCno // 필터링 조건 추가
    ) {
        // 상품 목록을 필터링하여 가져오는 로직
        List<ItemDto.ItemList> categoryResult = itemService.findItemsByCategory(cno, imageUrl, excludeCno);

        // 필터링된 결과를 뷰에 전달
        return new ModelAndView("item/miniCategory/list")
                .addObject("categoryResult", categoryResult);
    }

}
