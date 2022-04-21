package com.its4u.buildfactory.templateModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.its4u.buildfactory.ocp.OcpAlertPodRestart;


public class MailAlertModel {
	
	public HashMap<String,OcpAlertPodRestart> alertsPodRestarts;
	
	public List<String> appNames;
	
	public List<OcpAlertPodRestart> pods;
	
	public int gonogoLevelWarning;
	
	public int gonogoLevelBlock;
	
	private int requestCpu;
	
	private int requestMemory;

	public HashMap<String, OcpAlertPodRestart> getAlertsPodRestarts() {
		return alertsPodRestarts;
	}

	public void setAlertsPodRestarts(HashMap<String, OcpAlertPodRestart> alertsPodRestarts) {
		this.alertsPodRestarts = alertsPodRestarts;
	}

	public List<String> getAppNames() {
		return new ArrayList<String>(alertsPodRestarts.keySet());
	}

	public void setAppNames(List<String> appNames) {
		this.appNames = appNames;
	}

	public List<OcpAlertPodRestart> getPods() {
		return new ArrayList<OcpAlertPodRestart>(alertsPodRestarts.values());
	}

	public void setPods(List<OcpAlertPodRestart> pods) {
		this.pods = pods;
	}

	public int getGonogoLevelWarning() {
		return gonogoLevelWarning;
	}

	public void setGonogoLevelWarning(int gonogoLevelWarning) {
		this.gonogoLevelWarning = gonogoLevelWarning;
	}

	public int getGonogoLevelBlock() {
		return gonogoLevelBlock;
	}

	public void setGonogoLevelBlock(int gonogoLevelBlock) {
		this.gonogoLevelBlock = gonogoLevelBlock;
	}

	public int getRequestCpu() {
		return requestCpu;
	}

	public void setRequestCpu(int requestCpu) {
		this.requestCpu = requestCpu;
	}

	public int getRequestMemory() {
		return requestMemory;
	}

	public void setRequestMemory(int requestMemory) {
		this.requestMemory = requestMemory;
	}
	
	
	
	
	

}
