package com.its4u.buildfactory.springcontroller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		System.out.println("Dashboard");
			return "capacity-tool.xhtml";
	}
	
	@GetMapping(value = "/dashboard")
	public String getHome() {
		System.out.println("Dashboard");
			return "capacity-tool.xhtml";
	}
	
	@GetMapping(value = "/Login")
	public String getLogin() {
		System.out.println("Login Page");
		return "login.xhtml";
	}
	
	@GetMapping(value = "/logout")
	public String logout() throws ServletException {
		request.logout();
		return "redirect:/";
	}
	
	private void configCommonAttributes(Model model) {
		model.addAttribute("name", getKeycloakSecurityContext().getIdToken().getGivenName());
	}

	private KeycloakSecurityContext getKeycloakSecurityContext() {
		return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
	}

}