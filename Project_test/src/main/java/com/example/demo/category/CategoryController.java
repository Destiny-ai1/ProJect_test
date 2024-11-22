package com.example.demo.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.item.ItemDto;
import com.example.demo.item.ItemService;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;  // ItemService도 함께 사용

    // 카테고리 번호에 해당하는 상품 목록을 조회하는 페이지
    @GetMapping("/category/{cno}")
    public ModelAndView testCategory(@PathVariable Long cno) {
        // 1. 대분류에 해당하는 소분류 목록을 조회
        List<Map> majorCategory = categoryService.findCategoryByParentCno(cno);
        
        // 2. 소분류의 cno들을 가져와서 해당 소분류의 상품 목록을 조회
        List<ItemDto.ItemList> items = new ArrayList<>();
        
        for (Map subCategory : majorCategory) {
            // 소분류 cno
            Long subCategoryCno = (Long) subCategory.get("ccno");
            List<ItemDto.ItemList> subCategoryItems = itemService.findItemsByCategory(subCategoryCno, "/api/images?imagename=");
            items.addAll(subCategoryItems); // 소분류의 상품들을 items에 추가
        }

        return new ModelAndView("test/category")
                .addObject("majorCategory", majorCategory)  // 소분류 목록을 전달
                .addObject("items", items);  // 소분류에 해당하는 상품 목록을 전달
    }

}
