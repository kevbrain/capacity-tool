package com.its4u.buildfactory.springcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CapacityController {
	

	
	@GetMapping(value = "/")
	public String getHome() {
		return "index";
	}

}