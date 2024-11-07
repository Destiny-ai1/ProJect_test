package com.example.demo.item;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ItemController {
	@Autowired
	private ItemService service;
	
	@GetMapping("/")
	public ModelAndView list(Principal p) {
		List<ItemDto.ItemList> result = service.findAll();
		return new ModelAndView("item/list").addObject("result", result);
		
	}
}
