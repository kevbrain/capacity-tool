package com.its4u.buildfactory;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WelcomePageRedirect implements WebMvcConfigurer{

/*
	  public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/")
	        .setViewName("forward:/capacity-tool.xhtml");
	    registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	  }
	  */
}
