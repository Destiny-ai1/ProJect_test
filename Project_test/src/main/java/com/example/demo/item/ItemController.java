package com.example.demo.item;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
public class ItemController {

    @Autowired
    private ItemService service;

    // 상품 리스트 메인에 출력
    @GetMapping("/")
    public ModelAndView list(Principal p) {
        List<ItemDto.ItemList> result = service.findAll(); // 상품 목록 조회
        return new ModelAndView("item/list").addObject("result", result); // list.html에 결과 전달
    }

    // 관리자의 아이템 추가 이동
    @Secured("ROLE_ADMIN")  // 관리자만 접근 가능
    @GetMapping("/item/add")
    public ModelAndView addItem() {
        List<Map> majorCategory = service.findMajorCategory();
        return new ModelAndView("item/add").addObject("category", majorCategory); // 카테고리 추가
    }

    // 관리자의 상품 추가 처리
    @Secured("ROLE_ADMIN")  // 관리자만 접근 가능
    @PostMapping("/item/add")
    public ModelAndView addItem(@Valid ItemDto.Create dto, BindingResult br) {
        if (br.hasErrors()) {
            // 유효성 검증 실패 시 처리
            return new ModelAndView("item/add").addObject("errors", br.getAllErrors());
        }

        service.save(dto);
        return new ModelAndView("redirect:/"); // 상품 리스트로 리다이렉트
    }

    // 상품 상세 정보 조회
    @GetMapping("/item/read")
    public ModelAndView read(Long itemNo, String imageUrl) {
        ItemDto.Read result = service.read(itemNo);  // 상품 상세 정보 조회
        return new ModelAndView("item/read").addObject("result", result);
    }

    // 예외 처리: ConstraintViolationException (입력 검증 오류 처리)
    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView CVEHandler(ConstraintViolationException e, RedirectAttributes ra) {
        // 입력 검증 예외 처리
        String message = e.getConstraintViolations().stream()
                        .findFirst()
                        .get()
                        .getMessage();  // 첫 번째 검증 오류 메시지를 추출
        ra.addFlashAttribute("message", message);  // 메시지 리다이렉트 속성에 추가
        return new ModelAndView("redirect:/item/add");  // 상품 추가 페이지로 리다이렉트
    }
}
