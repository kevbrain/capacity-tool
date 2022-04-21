package com.its4u.buildfactory.ocp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;


public abstract class OcpResource {

	private String name;
	
	private List<Pod> podList;
	
	private List<Pod> podListFailed;
	
	private HashMap<String,OcpPod> ocpPods;

	private BigDecimal allocatable_cpu=null;
    
    private BigDecimal allocatable_memory=null;
    
    private BigDecimal quota_request_cpu=BigDecimal.ZERO;
    private BigDecimal quota_request_memory=BigDecimal.ZERO; 
    
    
	private BigDecimal current_cpu = BigDecimal.ZERO;
    private BigDecimal current_memory = BigDecimal.ZERO;
    
    private BigDecimal max_cpu_last2w = BigDecimal.ZERO;
    private BigDecimal max_memory_last2w = BigDecimal.ZERO;
    
    private BigDecimal limits_cpu = BigDecimal.ZERO;
    private BigDecimal limits_memory = BigDecimal.ZERO;
    
    private BigDecimal requests_cpu= BigDecimal.ZERO;
    private BigDecimal requests_memory= BigDecimal.ZERO;
    
    private BigDecimal percentageCurrentCpu = BigDecimal.ZERO;
    private BigDecimal percentageCurrentMemory = BigDecimal.ZERO;
    
    private BigDecimal percentageRequestCpu = BigDecimal.ZERO;
    private BigDecimal percentageRequestMemory = BigDecimal.ZERO;
    
    private BigDecimal percentageLimitCpu = BigDecimal.ZERO;
    private BigDecimal percentageLimitMemory = BigDecimal.ZERO;
    
    private String displayCurrentCpu;
    
    private String displayCurrentMemory;
    
    private String displayRequestCpu;
    
    private String displayRequestMemory;
    
    private String displayLimitCpu;
    
    private String displayLimitMemory;
    
    protected int nbrPods;
    
    private boolean hightRequestOrMemory;
    
    private boolean mediumRequestOrMemory;
    
    
    
    public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<Pod> getPodList() {
		return podList;
	}


	public void setPodList(List<Pod> podList) {
		this.podList = podList;
	}


	public HashMap<String, OcpPod> getOcpPods() {
		return ocpPods;
	}


	public void setOcpPods(HashMap<String, OcpPod> ocpPods) {
		this.ocpPods = ocpPods;
	}


	public BigDecimal getAllocatable_cpu() {
		return allocatable_cpu!=null?allocatable_cpu.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setAllocatable_cpu(BigDecimal allocatable_cpu) {
		this.allocatable_cpu = allocatable_cpu;
	}


	public BigDecimal getAllocatable_memory() {
		return allocatable_memory!=null?allocatable_memory.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setAllocatable_memory(BigDecimal allocatable_memory) {
		this.allocatable_memory = allocatable_memory;
	}


	public BigDecimal getQuota_request_cpu() {
		return quota_request_cpu!=null?quota_request_cpu.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setQuota_request_cpu(BigDecimal quota_request_cpu) {
		this.quota_request_cpu = quota_request_cpu;
	}


	public BigDecimal getQuota_request_memory() {
		return quota_request_memory!=null?quota_request_memory.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setQuota_request_memory(BigDecimal quota_request_memory) {
		this.quota_request_memory = quota_request_memory;
	}


	public BigDecimal getCurrent_cpu() {
		return current_cpu!=null?current_cpu.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setCurrent_cpu(BigDecimal current_cpu) {
		this.current_cpu = current_cpu;
	}


	public BigDecimal getCurrent_memory() {
		return current_memory!=null?current_memory.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setCurrent_memory(BigDecimal current_memory) {
		this.current_memory = current_memory;
	}


	public BigDecimal getMax_cpu_last2w() {
		return max_cpu_last2w!=null?max_cpu_last2w.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setMax_cpu_last2w(BigDecimal max_cpu_last2w) {
		this.max_cpu_last2w = max_cpu_last2w;
	}


	public BigDecimal getMax_memory_last2w() {
		return max_memory_last2w!=null?max_memory_last2w.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setMax_memory_last2w(BigDecimal max_memory_last2w) {
		this.max_memory_last2w = max_memory_last2w;
	}


	public BigDecimal getLimits_cpu() {
		return limits_cpu!=null?limits_cpu.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setLimits_cpu(BigDecimal limits_cpu) {
		this.limits_cpu = limits_cpu;
	}


	public BigDecimal getLimits_memory() {
		return limits_memory!=null?limits_memory.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setLimits_memory(BigDecimal limits_memory) {
		this.limits_memory = limits_memory;
	}


	public BigDecimal getRequests_cpu() {
		return requests_cpu!=null?requests_cpu.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setRequests_cpu(BigDecimal requests_cpu) {
		this.requests_cpu = requests_cpu;
	}


	public BigDecimal getRequests_memory() {
		return requests_memory!=null?requests_memory.setScale(0, BigDecimal.ROUND_HALF_UP):null;
	}


	public void setRequests_memory(BigDecimal requests_memory) {
		this.requests_memory = requests_memory;
	}


	public int getNbrPods() {
		return nbrPods;
	}


	public void setNbrPods(int nbrPods) {
		this.nbrPods = nbrPods;
	}


	public BigDecimal getPercentageMaxCpu() {
    	return allocatable_cpu!=null&&!allocatable_cpu.equals(BigDecimal.ZERO)?(max_cpu_last2w.divide(allocatable_cpu, 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ONE;
    }
    
    
    public BigDecimal getPercentageCurrentCpu() {
    	return allocatable_cpu!=null&&!allocatable_cpu.equals(BigDecimal.ZERO)?(current_cpu.divide(allocatable_cpu, 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ONE;
    }
    
    public BigDecimal getPercentageRequestCpu() {
    	return allocatable_cpu!=null&&!allocatable_cpu.equals(BigDecimal.ZERO)?(requests_cpu.divide(allocatable_cpu, 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ONE;
    }
    
    public BigDecimal getPercentageLimitCpu() {
    	return allocatable_cpu!=null&&!allocatable_cpu.equals(BigDecimal.ZERO)?(limits_cpu.divide(allocatable_cpu, 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ONE;   	
    }
    
    public BigDecimal getPercentageMaxMemory() {
    	return allocatable_memory!=null&&!limits_memory.equals(BigDecimal.ZERO)?(max_memory_last2w.divide(limits_memory, 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ONE;
    }
    
    public BigDecimal getPercentageCurrentMemory() {
    	return allocatable_memory!=null&&!allocatable_memory.equals(BigDecimal.ZERO)?(current_memory.divide(allocatable_memory, 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ONE;
    }
    
    public BigDecimal getPercentageRequestMemory() {
    	return allocatable_memory!=null&&!allocatable_memory.equals(BigDecimal.ZERO)?(requests_memory.divide(allocatable_memory, 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ONE;
    }
    
    public BigDecimal getPercentageLimitMemory() {
    	return allocatable_memory!=null&&!allocatable_memory.equals(BigDecimal.ZERO)?(limits_memory.divide(allocatable_memory, 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ONE;    	
    }
    
    public String getPerCpuFromCpuLimit()  {
    	if (this.getLimits_cpu()!=null && this.getLimits_cpu().compareTo(BigDecimal.ZERO)>0) {
    		return ""+getCurrent_cpu().divide(getLimits_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
    	} 
    	return "-";
    }
	
    public String getPerMemoryFromMemoryLimit() {
    	if (this.getLimits_memory()!=null && this.getLimits_memory().compareTo(BigDecimal.ZERO)>0) {
    		return ""+getCurrent_memory().divide(getLimits_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
    	} 
    	return "-";
    }
    
    
    
	public String getDisplayCurrentCpu() {
		return getCurrent_cpu()+"/"+getAllocatable_cpu();
	}


	public void setDisplayCurrentCpu(String displayCurrentCpu) {
		this.displayCurrentCpu = displayCurrentCpu;
	}


	public String getDisplayCurrentMemory() {
		return getCurrent_memory()+"/"+getAllocatable_memory();
	}


	public void setDisplayCurrentMemory(String displayCurrentMemory) {
		this.displayCurrentMemory = displayCurrentMemory;
	}


	public String getDisplayRequestCpu() {
		return getRequests_cpu()+"/"+getAllocatable_cpu();
	}


	public void setDisplayRequestCpu(String displayRequestCpu) {
		this.displayRequestCpu = displayRequestCpu;
	}


	public String getDisplayRequestMemory() {
		return getRequests_memory()+"/"+getAllocatable_memory();
	}


	public void setDisplayRequestMemory(String displayRequestMemory) {
		this.displayRequestMemory = displayRequestMemory;
	}


	public String getDisplayLimitCpu() {
		return getLimits_cpu()+"/"+getAllocatable_memory();
	}


	public void setDisplayLimitCpu(String displayLimitCpu) {
		this.displayLimitCpu = displayLimitCpu;
	}


	public String getDisplayLimitMemory() {
		return getLimits_memory()+"/"+getAllocatable_memory();
	}


	public void setDisplayLimitMemory(String displayLimitMemory) {
		this.displayLimitMemory = displayLimitMemory;
	}

	

	public void setPercentageCurrentCpu(BigDecimal percentageCurrentCpu) {
		this.percentageCurrentCpu = percentageCurrentCpu;
	}


	public void setPercentageCurrentMemory(BigDecimal percentageCurrentMemory) {
		this.percentageCurrentMemory = percentageCurrentMemory;
	}


	public void setPercentageRequestCpu(BigDecimal percentageRequestCpu) {
		this.percentageRequestCpu = percentageRequestCpu;
	}


	public void setPercentageRequestMemory(BigDecimal percentageRequestMemory) {
		this.percentageRequestMemory = percentageRequestMemory;
	}


	public void setPercentageLimitCpu(BigDecimal percentageLimitCpu) {
		this.percentageLimitCpu = percentageLimitCpu;
	}


	public void setPercentageLimitMemory(BigDecimal percentageLimitMemory) {
		this.percentageLimitMemory = percentageLimitMemory;
	}


	public OcpResource(String name) {
		super();
		this.name = name;
		this.ocpPods = new HashMap<String,OcpPod>();
	}


	public boolean isHightRequestOrMemory() {
		return (getPercentageRequestCpu().compareTo(new BigDecimal(90))>0 || getPercentageRequestMemory().compareTo(new BigDecimal(90))>0);
	}


	public void setHightRequestOrMemory(boolean hightRequestOrMemory) {
		this.hightRequestOrMemory = hightRequestOrMemory;
	}


	public boolean isMediumRequestOrMemory() {
		return (getPercentageRequestCpu().compareTo(new BigDecimal(80))>0 || getPercentageRequestMemory().compareTo(new BigDecimal(80))>0);
	}


	public void setMediumRequestOrMemory(boolean mediumRequestOrMemory) {
		this.mediumRequestOrMemory = mediumRequestOrMemory;
	}


	public List<Pod> getPodListFailed() {
		return podListFailed;
	}


	public void setPodListFailed(List<Pod> podListFailed) {
		this.podListFailed = podListFailed;
	}
	
	
	
	
	
}
