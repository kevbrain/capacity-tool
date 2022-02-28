package com.its4u.buildfactory.ocp;

public class OcpAlertPodHightCpu extends OcpAlert {

	private String podName;
	
	private int percentCpu;
	
	public OcpAlertPodHightCpu(String namespaceName, String link) {
		super(namespaceName, link);		
	}

	public OcpAlertPodHightCpu(String namespaceName, String link, String podName, int percentCpu) {
		super(namespaceName, link);
		this.podName = podName;
		this.percentCpu = percentCpu;
	}

	public String getPodName() {
		return podName;
	}

	public void setPodName(String podName) {
		this.podName = podName;
	}

	public int getPercentCpu() {
		return percentCpu;
	}

	public void setPercentCpu(int percentCpu) {
		this.percentCpu = percentCpu;
	}

	
	
	

}
