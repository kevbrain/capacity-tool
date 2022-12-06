package com.its4u.buildfactory.rest;


import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.its4u.buildfactory.ScheduleService;
import com.its4u.buildfactory.ocp.OcpAlertPodHightCpu;
import com.its4u.buildfactory.ocp.OcpAlertPodHightMemory;
import com.its4u.buildfactory.ocp.OcpAlertPodRestart;
import com.its4u.buildfactory.ocp.OcpAlertProjectWhithoutlimits;
import com.its4u.buildfactory.ocp.OcpAlertProjectWhithoutquotas;
import com.its4u.buildfactory.ocp.OcpCluster;
import com.its4u.buildfactory.ocp.OcpEnvironment;
import com.its4u.buildfactory.ocp.OcpNode;
import com.its4u.buildfactory.ocp.OcpResource;
import com.its4u.buildfactory.ocp.ProjectTeam;
import com.its4u.buildfactory.ocp.ProjectValueChain;

import lombok.AllArgsConstructor;
@CrossOrigin(maxAge = 3600)
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
	
	@GetMapping(value = "/api/v1/nodes")
	@ResponseBody
	public ResponseEntity<Collection<OcpResource>> getNodes() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getNodes());
		
	}
	
	@GetMapping(value = "/api/v1/environments")
	@ResponseBody
	public ResponseEntity<Collection<OcpEnvironment>> getenvironments() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getEnvironments());
		
	}
	
	@GetMapping(value = "/api/v1/teams")
	@ResponseBody
	public ResponseEntity<Collection<ProjectTeam>> getTeams() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getTeams());
		
	}
	
	@GetMapping(value = "/api/v1/valueChains")
	@ResponseBody
	public ResponseEntity<Collection<ProjectValueChain>> getValueChains() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getValueChains());
		
	}
	
	@GetMapping(value = "/api/v1/alerts/podRestarts")
	@ResponseBody
	public ResponseEntity<List<OcpAlertPodRestart>> getAlertsPodsRestart() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getAlertsPodRestart());
		
	}
	
	@GetMapping(value = "/api/v1/alerts/podsHightCpu")
	@ResponseBody
	public ResponseEntity<List<OcpAlertPodHightCpu>> getAlertsPodsHightCpu() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getAlertPodHigthCpu());
		
	}
	
	@GetMapping(value = "/api/v1/alerts/podHightMemory")
	@ResponseBody
	public ResponseEntity<List<OcpAlertPodHightMemory>> getAlertsalertsPodHightMemory() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getAlertPodHightMemory());
		
	}
	

	@GetMapping(value = "/api/v1/alerts/podwithoutLimits")
	@ResponseBody
	public ResponseEntity<List<OcpAlertProjectWhithoutlimits>> getAlertsalertsPodwithoutLimits() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getAlertPodWithoutLimit());
		
	}
	
	@GetMapping(value = "/api/v1/alerts/podwithoutQuotas")
	@ResponseBody
	public ResponseEntity<List<OcpAlertProjectWhithoutquotas>> getAlertsalertsPodwithoutQuotas() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getAlertPodWithoutQuotas());
		
	}
	
	@GetMapping(value = "/api/v1/cluster")
	@ResponseBody
	public ResponseEntity<OcpCluster> getCluster() {
								
		return ResponseEntity.ok().headers(createHeaders()).contentType(MediaType.APPLICATION_JSON).body(scheduleService.getCluster());
		
	}
	
	private HttpHeaders createHeaders() {
	      HttpHeaders headers = new HttpHeaders();
	      headers.add("Cache-Control", "no-cache, no-store, must revalidate");
	      headers.add("Pragma", "no-cache");
	      headers.add("Expires", "0");
	      return headers;
	}
}
