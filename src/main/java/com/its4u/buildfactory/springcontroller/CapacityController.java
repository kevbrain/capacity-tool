package com.its4u.buildfactory.springcontroller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CapacityController {
	
	private final HttpServletRequest request;
	
	
	
	public CapacityController(HttpServletRequest request) {
		super();
		this.request = request;
	}

	@GetMapping(value = "/capacity-tool")
	public String getDashboard() {
			return "capacity-tool.xhtml";
	}
	
	@GetMapping(value = "/dashboard")
	public String getHome() {
			return "capacity-tool.xhtml";
	}
	
	@GetMapping(value = "/Login")
	public String getLogin() {
		return "login.xhtml";
	}
	
	@GetMapping(value = "/logout")
	public String logout() throws ServletException {
		request.logout();
		return "redirect:/";
	}
	

}