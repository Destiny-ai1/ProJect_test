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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
		itemService.save(dto);
		return new ModelAndView("redirect:/"); // 상품 리스트로 리다이렉트
	}

	// 상품 상세 정보 페이지 이동
	@GetMapping("/item/read")
	public ModelAndView read(Long itemNo, String imageUrl) {
		ItemDto.Read result = itemService.read(itemNo, imageUrl); // 상품 상세 정보 조회

		// 상품이 없다면 에러 페이지로 리다이렉트
		if (result == null) {
			return new ModelAndView("redirect:/item/add"); // 상품이 없으면 아이템 추가 페이지로 리다이렉트
		}

		return new ModelAndView("item/read").addObject("result", result);
	}

	@GetMapping("/category/{cno}/items")
	public ModelAndView getItemsByCategory(@PathVariable Long cno, @RequestParam(required = false) String imageUrl) {
	    // 서비스 호출하여 카테고리 번호에 해당하는 상품들 조회
	    List<ItemDto.ItemList> categoryResult = itemService.findItemsByCategory(cno, imageUrl);

	    // 조회한 결과를 ModelAndView로 반환
	    return new ModelAndView("item/test/list") // "item/test/testfiled"는 해당하는 Thymeleaf 템플릿 경로
	            .addObject("categoryResult", categoryResult); // 모델에 결과를 담아서 뷰로 전달
	}


	// 예외 처리: ConstraintViolationException (입력 검증 오류 처리)
	@ExceptionHandler(ConstraintViolationException.class)
	public ModelAndView CVEHandler(ConstraintViolationException e, RedirectAttributes ra) {
		// 입력 검증 예외 처리
		String message = e.getConstraintViolations().stream().findFirst().get().getMessage(); // 첫 번째 검증 오류 메시지를 추출
		ra.addFlashAttribute("message", message); // 메시지 리다이렉트 속성에 추가
		return new ModelAndView("redirect:/item/add"); // 상품 추가 페이지로 리다이렉트
	}
}
