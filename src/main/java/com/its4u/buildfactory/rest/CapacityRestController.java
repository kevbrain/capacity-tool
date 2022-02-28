package com.its4u.buildfactory.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.its4u.buildfactory.ScheduleService;

import lombok.AllArgsConstructor;

@RestController
@EnableAutoConfiguration
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class CapacityRestController {

	@Autowired 
	private ScheduleService scheduleService;
	
	
	@GetMapping(value = "/capacity/status")
	@ResponseBody
	public ResponseEntity<CapacityStatus> getStatus() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getCapacityStatus());
		
	}
	
	@GetMapping(value = "/capacity/status/goNogo")
	@ResponseBody
	public ResponseEntity<String> getStatusGoNoGo() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.TEXT_PLAIN).body(scheduleService.getGoNoGo());
		
	}
	
	private HttpHeaders createHeaders() {
	      HttpHeaders headers = new HttpHeaders();
	      headers.add("Cache-Control", "no-cache, no-store, must revalidate");
	      headers.add("Pragma", "no-cache");
	      headers.add("Expires", "0");
	      return headers;
	}
}
