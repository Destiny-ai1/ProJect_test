package com.example.demo.item;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
	/* @Secured("enum_admin(어드민 입력)") */
	@GetMapping("/item/add")
	public ModelAndView addItem() {
		List<Map> majorCategory = service.findMajorCategory();
		return new ModelAndView("item/add").addObject("category", majorCategory); // 카테고리 추가
	}
	
	/* @Secured("enum_admin(어드민 입력)") */
	@PostMapping("/item/add")
	public ModelAndView addItem(@Valid ItemDto.Create dto, BindingResult br	) {
		service.save(dto);
		return new ModelAndView("redirect:/");
	}
	
	@GetMapping("/item/read")
	public ModelAndView read(Long itemNo, String imageUrl) {
		ItemDto.Read result = service.read(itemNo, imageUrl);	// 상품 상세 정보 조회
		return new ModelAndView("item/read").addObject("result", result);
	}
	
	// 예외처리
	@ExceptionHandler(ConstraintViolationException.class)
	public ModelAndView CVEHandler(ConstraintViolationException e, RedirectAttributes ra) {
		String message = e.getConstraintViolations().stream().findFirst().get().getMessage();
		ra.addFlashAttribute("message", message);
		return new ModelAndView("redirect:/item/add");
	}
}
