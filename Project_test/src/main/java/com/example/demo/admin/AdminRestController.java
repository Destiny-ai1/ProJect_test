package com.example.demo.admin;

import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
@PreAuthorize("hasRole('admin')")
public class AdminRestController {

	@PreAuthorize("isAnonymous()")
	@GetMapping("terms")
	public ModelAndView terms() 	{
		return new ModelAndView("terms");
	}
	@PreAuthorize("isAnonymous()")
	@GetMapping("guide")
	public ModelAndView guide() 	{
		return new ModelAndView("guide");
	}
	@PreAuthorize("isAnonymous()")
	@GetMapping("privacy")
	public ModelAndView privacy() 	{
		return new ModelAndView("privacy");
	}
}
