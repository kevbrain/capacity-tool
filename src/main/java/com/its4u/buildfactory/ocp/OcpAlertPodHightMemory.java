package com.its4u.buildfactory.ocp;

public class OcpAlertPodHightMemory extends OcpAlert {

	private String podName;
	
	private int percentMemory;
	
	public OcpAlertPodHightMemory(String namespaceName, String link) {
		super(namespaceName, link);		
	}

	public OcpAlertPodHightMemory(String namespaceName, String link, String podName, int percentMemory) {
		super(namespaceName, link);
		this.podName = podName;
		this.percentMemory = percentMemory;
	}

	public String getPodName() {
		return podName;
	}

	public void setPodName(String podName) {
		this.podName = podName;
	}

	public int getPercentMemory() {
		return percentMemory;
	}

	public void setPercentMemory(int percentMemory) {
		this.percentMemory = percentMemory;
	}



	
	
	

}
