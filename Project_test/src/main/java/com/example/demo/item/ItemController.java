package com.example.demo.item;

import java.security.Principal;
import java.util.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.example.demo.category.CategoryService;
import com.example.demo.newitem.*;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private NewItemService newitemService;
    // 메인 페이지
    @GetMapping("/")
    public ModelAndView list() {
        String imageUrl = "/api/images?imagename=";
        List<ItemDto.ItemList> allItems = itemService.findAll(imageUrl);
        List<NewItemDto.ItemResponse> newItems = newitemService.getNewItems(30, imageUrl);
        List<NewItemDto.ItemResponse> popularItems = newitemService.getPopularItems(100, imageUrl);

        // 데이터를 4개씩 나누어 그룹화
        List<List<NewItemDto.ItemResponse>> newItemGroups = partitionList(newItems, 4);
        List<List<NewItemDto.ItemResponse>> popularItemGroups = partitionList(popularItems, 4);

        ModelAndView mav = new ModelAndView("item/index");
        mav.addObject("newItemGroups", newItemGroups);
        mav.addObject("popularItemGroups", popularItemGroups);

        return mav;
    }

    private <T> List<List<T>> partitionList(List<T> list, int size) {
    	  // 결과를 저장할 리스트 초기화
        List<List<T>> partitioned = new ArrayList<>();
        // 원본 리스트를 그룹화
        for (int i = 0; i < list.size(); i += size) {
            partitioned.add(list.subList(i, Math.min(i + size, list.size())));
        }
     // 그룹화된 리스트 반환
        return partitioned;
    }

    // 관리자의 상품 추가 페이지 이동
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/item/add")
    public ModelAndView addItem() {
        List<Map> majorCategory = itemService.findMajorCategory();
        return new ModelAndView("item/add").addObject("category", majorCategory); // 카테고리 추가
    }

    // 상품 추가 후 루트 페이지로 이동
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/item/add")
    public ModelAndView addItem(@Valid ItemDto.Create dto, BindingResult br) {
        if (br.hasErrors()) {
            List<Map> majorCategory = itemService.findMajorCategory();  // 카테고리 목록 다시 가져오기
            return new ModelAndView("item/add")
                .addObject("category", majorCategory)  // 카테고리 추가
                .addObject("dto", dto);  // 입력된 데이터를 폼에 다시 채우기
        }

        // 상품 저장 로직 처리
        itemService.save(dto);  // 유효성 검사 통과 시, 상품 저장

        return new ModelAndView("redirect:/");  // 상품 리스트로 리다이렉트 분리이유 아이템이 생성제대로되었는지 확인하기위해 관리자만
    }

 // 상품 상세 정보 페이지 이동
    @GetMapping("/item/read/{itemNo}")
    public ModelAndView read(@PathVariable Long itemNo, 
                             @RequestParam(required = false) String imageUrl, 
                             @RequestParam(required = false) String itemSize) {
        ItemDto.Read result = itemService.read(itemNo, imageUrl, itemSize);

        // 만약 itemInfo가 존재하면, HTML 태그를 제거
        if (result != null && result.getItemInfo() != null) {
            // HTML 태그 제거
            String itemInfoWithoutTags = result.getItemInfo().replaceAll("<[^>]*>", "");
            result.setItemInfo(itemInfoWithoutTags); // HTML 태그 제거된 값으로 업데이트
        }

        // 상품이 없으면 아이템 추가 페이지로 리다이렉트
        if (result == null) {
            return new ModelAndView("redirect:/item/add"); // 상품이 없으면 아이템 추가 페이지로 리다이렉트
        }

        // 선택된 사이즈에 맞는 재고 메시지 설정
        if (itemSize != null) {
            String stockMessage = itemService.getStockMessage(itemNo, itemSize);
            result.setStockMessage(stockMessage); // 재고 메시지 설정
        }

        return new ModelAndView("item/read").addObject("result", result); // 상품 상세 정보와 재고 메시지 전달
    }

    // 재고 메시지 조회 (사이즈 선택에 따른 재고 상태)
    @PostMapping("/get-stock-message")
    @ResponseBody
    public String getStockMessage(@RequestParam Long itemNo, @RequestParam String itemSize) {
        return itemService.getStockMessage(itemNo, itemSize);  // 선택된 사이즈에 대한 재고 메시지 반환
    }

    // 예외 처리: ConstraintViolationException (입력 검증 오류 처리)
    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView CVEHandler(ConstraintViolationException e, RedirectAttributes ra) {
        String message = e.getConstraintViolations().stream().findFirst().get().getMessage();
        ra.addFlashAttribute("message", message);
        return new ModelAndView("redirect:/item/add");
    }
}
