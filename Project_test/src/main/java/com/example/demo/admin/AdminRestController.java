package com.example.demo.admin;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class AdminRestController {


	@GetMapping("terms")
	public ModelAndView terms() 	{
		return new ModelAndView("terms");
	}
	@GetMapping("guide")
	public ModelAndView guide() 	{
		return new ModelAndView("guide");
	}
	@GetMapping("privacy")
	public ModelAndView privacy() 	{
		return new ModelAndView("privacy");
	}
}
