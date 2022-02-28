package com.its4u.buildfactory.ocp;

public class OcpAlertProjectWhithoutlimits extends OcpAlert {

	private String podName;
	
	private int countRestart;
	
	public OcpAlertProjectWhithoutlimits(String namespaceName, String link) {
		super(namespaceName, link);		
	}

	public OcpAlertProjectWhithoutlimits(String namespaceName, String link, String podName, int countRestart) {
		super(namespaceName, link);
		this.podName = podName;
		this.countRestart = countRestart;
	}

	public String getPodName() {
		return podName;
	}

	public void setPodName(String podName) {
		this.podName = podName;
	}

	public int getCountRestart() {
		return countRestart;
	}

	public void setCountRestart(int countRestart) {
		this.countRestart = countRestart;
	}
	
	

}
