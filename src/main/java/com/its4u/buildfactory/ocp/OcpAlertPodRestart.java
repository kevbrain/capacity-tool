package com.its4u.buildfactory.ocp;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OcpAlertPodRestart extends OcpAlert {

	private String podName;
	
	private int countRestart;
	
	private String reason;
	
	private OcpPod pod;
	
	@JsonIgnore
	private OcpNamespace namespace;
	
	private String namespaceName;
	
	
	
	public OcpAlertPodRestart(String namespaceName, String link) {
		super(namespaceName, link);		
	}

	public OcpAlertPodRestart(String namespaceName, String link, String podName, int countRestart,OcpPod pod) {
		super(namespaceName, link);
		this.podName = podName;
		this.countRestart = countRestart;
		this.pod = pod;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public OcpPod getPod() {
		return pod;
	}

	public void setPod(OcpPod pod) {
		this.pod = pod;
	}

	public OcpNamespace getNamespace() {
		return namespace;
	}

	public void setNamespace(OcpNamespace namespace) {
		this.namespace = namespace;
	}

	public String getNamespaceName() {
		return this.namespace.getName();
	}

	public void setNamespaceName(String namespaceName) {
		this.namespaceName = namespaceName;
	}

	
	
	
	
	
	
	

}
