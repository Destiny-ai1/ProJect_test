package com.example.demo.item;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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

import com.example.demo.category.CategoryService;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    // 메인 페이지
    @GetMapping("/")
    public ModelAndView list(Principal p) {
        String imageUrl = "/api/images?imagename=";
        List<ItemDto.ItemList> result = itemService.findAll(imageUrl); // 상품 목록 조회
        return new ModelAndView("item/list").addObject("result", result); // list.html에 결과 전달
    }

    // 관리자의 상품 추가 페이지 이동
    @GetMapping("/item/add")
    public ModelAndView addItem() {
        List<Map> majorCategory = itemService.findMajorCategory();
        return new ModelAndView("item/add").addObject("category", majorCategory); // 카테고리 추가
    }

    // 관리자의 상품 추가 후 루트페이지로 이동
    @PostMapping("/item/add")
    public ModelAndView addItem(@Valid ItemDto.Create dto, BindingResult br) {
        if (br.hasErrors()) {
            List<Map> majorCategory = itemService.findMajorCategory();  // 카테고리 목록 다시 가져오기
            return new ModelAndView("item/add")
                .addObject("category", majorCategory)  // 카테고리 추가
                .addObject("dto", dto);  // 입력된 데이터를 폼에 다시 채우기
        }
        itemService.save(dto);  // 유효성 검사 통과 시, 상품 저장
        return new ModelAndView("redirect:/");  // 상품 리스트로 리다이렉트
    }

    // 상품 상세 정보 페이지 이동
    @GetMapping("/item/read/{itemNo}")
    public ModelAndView read(@PathVariable Long itemNo, 
                             @RequestParam(required = false) String imageUrl, 
                             @RequestParam(required = false) String itemSize) {
        ItemDto.Read result = itemService.read(itemNo, imageUrl, itemSize);

        // 상품이 없다면 에러 페이지로 리다이렉트
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
    
    // 하위 카테고리에 해당하는 상품페이지로 이동
    @GetMapping("/category/{cno}/items")
    public ModelAndView getItemsByCategory(@PathVariable Long cno, @RequestParam(required = false) String imageUrl) {
        List<ItemDto.ItemList> categoryResult = itemService.findItemsByCategory(cno, imageUrl);
        return new ModelAndView("item/miniCategory/list")
                .addObject("categoryResult", categoryResult);
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
