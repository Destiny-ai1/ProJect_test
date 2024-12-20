package com.example.demo.newitem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/newitem")
public class NewItemController {

    private final NewItemService newItemService;

    // 신상품 페이지
    @GetMapping("/new")
    public ModelAndView newItems() {
        String imageUrl = "/images/";
        List<NewItemDto.ItemResponse> newItems = newItemService.getNewItems(30, imageUrl); // 최근 30일
        return new ModelAndView("newitem/new").addObject("items", newItems);
    }

    // 인기상품 페이지
    @GetMapping("/popular")
    public ModelAndView popularItems() {
        String imageUrl = "/images/";
        List<NewItemDto.ItemResponse> popularItems = newItemService.getPopularItems(10, imageUrl); // 판매량 10 이상
        return new ModelAndView("newitem/popular").addObject("items", popularItems);
    }

    // 신상품과 인기상품을 인덱스 페이지에 출력
    @GetMapping("/list2")
    public ModelAndView index() {
        String imageUrl = "/images/";

        // 신상품과 인기상품 가져오기
        List<NewItemDto.ItemResponse> newItems = newItemService.getNewItems(30, imageUrl);
        List<NewItemDto.ItemResponse> popularItems = newItemService.getPopularItems(10, imageUrl);

        // ModelAndView 생성
        ModelAndView mav = new ModelAndView("item/list2");
        mav.addObject("newItems", newItems);
        mav.addObject("popularItems", popularItems);

        return mav;
    }
}
