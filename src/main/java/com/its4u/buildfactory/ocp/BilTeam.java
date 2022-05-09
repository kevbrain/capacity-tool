package com.its4u.buildfactory.ocp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class BilTeam {

	private String teamName;
	
	private List<OcpNamespace> namespaces;
	
	private BigDecimal pods=null;
	
	private BigDecimal currentCurCpu=null;
	
	private BigDecimal currentReqCpu=null;
	
	private BigDecimal limCpu=null;
	
	private BigDecimal currentMemory=null;
	
	private BigDecimal reqMemory=null;
	
	private BigDecimal limMemory=null;
	

	public BilTeam(String teamName, List<OcpNamespace> namespaces) {
		super();
		this.teamName = teamName;
		this.namespaces = namespaces;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public List<OcpNamespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<OcpNamespace> namespaces) {
		this.namespaces = namespaces;
	}

	public BigDecimal getPods() {
		if (pods==null) {
			pods = BigDecimal.ZERO;
			for (OcpNamespace ns:namespaces) {
				pods = pods.add(new BigDecimal(ns.getNbrPods()));
			}
		}
		return pods;
	}
	
	public BigDecimal getCurrentReqCpu() {
		if (currentReqCpu==null) {
			currentReqCpu = BigDecimal.ZERO;
			for (OcpNamespace ns:namespaces) {
				currentReqCpu = currentReqCpu.add(ns.getRequests_cpu());
			}
		}
		return currentReqCpu.setScale(0, RoundingMode.HALF_UP);
	}
	

	public BigDecimal getCurrentCurCpu() {
		if (currentCurCpu==null) {
			currentCurCpu = BigDecimal.ZERO;
			for (OcpNamespace ns:namespaces) {
				currentCurCpu = currentCurCpu.add(ns.getCurrent_cpu());
			}
		}
		return currentCurCpu.setScale(0, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getLimCpu() {
		if (limCpu==null) {
			limCpu = BigDecimal.ZERO;
			for (OcpNamespace ns:namespaces) {
				limCpu = limCpu.add(ns.getLimits_cpu());
			}
		}
		return limCpu;
	}
	
	public BigDecimal getCurrentMemory() {
		if (currentMemory==null) {
			currentMemory = BigDecimal.ZERO;
			for (OcpNamespace ns:namespaces) {
				currentMemory = currentMemory.add(ns.getCurrent_memory());
			}
		}
		return currentMemory;
	}

	public BigDecimal getReqMemory() {
		if (reqMemory==null) {
			reqMemory = BigDecimal.ZERO;
			for (OcpNamespace ns:namespaces) {
				reqMemory = reqMemory.add(ns.getRequests_memory());
			}
		}
		return reqMemory;
	}
	
	public BigDecimal getLimMemory() {
		if (limMemory==null) {
			limMemory = BigDecimal.ZERO;
			for (OcpNamespace ns:namespaces) {
				limMemory = limMemory.add(ns.getLimits_memory());
			}
		}
		return limMemory;
	}
	
	public void setCurrentCurCpu(BigDecimal currentCurCpu) {
		this.currentCurCpu = currentCurCpu;
	}

	public void setCurrentReqCpu(BigDecimal currentReqCpu) {
		this.currentReqCpu = currentReqCpu;
	}

	

	public void setLimCpu(BigDecimal limCpu) {
		this.limCpu = limCpu;
	}

	

	public void setCurrentMemory(BigDecimal currentMemory) {
		this.currentMemory = currentMemory;
	}

	

	public void setReqMemory(BigDecimal reqMemory) {
		this.reqMemory = reqMemory;
	}

	

	public void setLimMemory(BigDecimal limMemory) {
		this.limMemory = limMemory;
	}

	

	public void setPods(BigDecimal pods) {
		this.pods = pods;
	}

	


	
	
	
	
	
}
