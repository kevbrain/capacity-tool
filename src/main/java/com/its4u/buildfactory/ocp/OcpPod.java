package com.its4u.buildfactory.ocp;



import java.math.BigDecimal;
import java.math.RoundingMode;

import com.its4u.buildfactory.utils.FormatUtils;


public class OcpPod extends OcpResource{

	private int countRestart=0;
	
	private String namespace;
	
	private String appName;
	
	private String terminatedReason;
	
	private BigDecimal container_memory_request=BigDecimal.ZERO;
    
    private BigDecimal container_memory_limit=BigDecimal.ZERO;
    
    private BigDecimal container_cpu_request=BigDecimal.ZERO;
    
    private BigDecimal container_cpu_limit=BigDecimal.ZERO;
    
    private String cpuDisplay;
    
    private String memoryDisplay;
    
    private String usageCpu;
    
	private String maxCpu;
	
	private String usageMemory;
    
	private String maxMemory;
	
	private String podName;
	
	public OcpPod(String name) {
		super(name);
		this.nbrPods = 1;
	} 

	public String printCpuItemPod() {
	    	
    	return
    			FormatUtils.leftpad(this.getName(),50)
				+ FormatUtils.format("" ,2)
				+ FormatUtils.format("" ,2) 
			    + FormatUtils.format("cpu["+this.getRequests_cpu().setScale(0, RoundingMode.HALF_UP)+"/"+this.getLimits_cpu().setScale(0, RoundingMode.HALF_UP)+"]",15)
			    + FormatUtils.format("mem["+this.getRequests_memory().setScale(0, RoundingMode.HALF_UP)+"/"+this.getLimits_memory().setScale(0, RoundingMode.HALF_UP)+"]",15)
				+ FormatUtils.format(""+this.getNbrPods(),5)
				+ " | "			
				+ FormatUtils.format(this.getCurrent_cpu()+"/"+this.getLimits_cpu(),13)    			
				+ FormatUtils.format(""+this.getPercentageCurrentCpu().setScale(0, RoundingMode.HALF_UP),4)
				
				+ FormatUtils.format(this.getMax_cpu_last2w()+"/"+(this.getLimits_cpu()!=null?this.getLimits_cpu():"-"),13)    			
				+ FormatUtils.format(""+this.getPercentageMaxCpu().setScale(0, RoundingMode.HALF_UP),4) 
				
				+ FormatUtils.format(this.getCurrent_memory().setScale(0, RoundingMode.HALF_UP)+"/"+(this.getLimits_memory()!=null?this.getLimits_memory().setScale(0, RoundingMode.HALF_UP):"-"),16) 
				+ FormatUtils.format(""+this.getPercentageCurrentMemory().setScale(0, RoundingMode.HALF_UP),4) 
				
				+ FormatUtils.format(this.getMax_memory_last2w().setScale(0, RoundingMode.HALF_UP)+"/"+(this.getLimits_memory()!=null?this.getLimits_memory().setScale(0, RoundingMode.HALF_UP):"-"),16) 
				+ FormatUtils.format(""+this.getPercentageMaxMemory().setScale(0, RoundingMode.HALF_UP),4)
				+ FormatUtils.format(""+this.getCountRestart(),8)	    		
				;  				
    }
	
	
	
	public String getUsageCpu() {
		return this.getCurrent_cpu()+"/"+this.getLimits_cpu();
	}

	public void setUsageCpu(String usageCpu) {
		this.usageCpu = usageCpu;
	}

	public String getMaxCpu() {
		return this.getMax_cpu_last2w()+"/"+(this.getLimits_cpu()!=null?this.getLimits_cpu():"-");
	}

	public void setMaxCpu(String maxCpu) {
		this.maxCpu = maxCpu;
	}

	public String getUsageMemory() {
		return this.getCurrent_memory().setScale(0, RoundingMode.HALF_UP)+"/"+(this.getLimits_memory()!=null?this.getLimits_memory().setScale(0, RoundingMode.HALF_UP):"-");
	}

	public void setUsageMemory(String usageMemory) {
		this.usageMemory = usageMemory;
	}

	public String getMaxMemory() {
		return this.getMax_memory_last2w().setScale(0, RoundingMode.HALF_UP)+"/"+(this.getLimits_memory()!=null?this.getLimits_memory().setScale(0, RoundingMode.HALF_UP):"-");
	}

	public void setMaxMemory(String maxMemory) {
		this.maxMemory = maxMemory;
	}

	public BigDecimal getPercentageCurrentCpu() {
		
    	return (this.getLimits_cpu().compareTo(BigDecimal.ZERO)>0 ?(this.getCurrent_cpu().divide(this.getLimits_cpu(), 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ZERO);
    }
	
	public BigDecimal getPercentageMaxCpu() {
		
    	return (this.getLimits_cpu().compareTo(BigDecimal.ZERO)>0 ?(this.getMax_cpu_last2w().divide(this.getLimits_cpu(), 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ZERO);
    }
	
	public BigDecimal getPercentageCurrentMemory() {
		
    	return (this.getLimits_memory().compareTo(BigDecimal.ZERO)>0 ?(this.getCurrent_memory().divide(this.getLimits_memory(), 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ZERO);
    }
	
	public BigDecimal getPercentageMaxMemory() {
	    
		return (this.getLimits_memory().compareTo(BigDecimal.ZERO)>0 ?(this.getMax_memory_last2w().divide(this.getLimits_memory(), 3, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)):BigDecimal.ZERO);
	}
 
		 
	public String getCpuDisplay() {
		return "cpu["+this.getRequests_cpu().setScale(0, RoundingMode.HALF_UP)+"/"+this.getLimits_cpu().setScale(0, RoundingMode.HALF_UP)+"]";
	}

	public void setCpuDisplay(String cpuDisplay) {
		this.cpuDisplay = cpuDisplay;
	}

	public String getMemoryDisplay() {
		return "mem["+this.getRequests_memory().setScale(0, RoundingMode.HALF_UP)+"/"+this.getLimits_memory().setScale(0, RoundingMode.HALF_UP)+"]";
	}

	public void setMemoryDisplay(String memoryDisplay) {
		this.memoryDisplay = memoryDisplay;
	}

	public int getCountRestart() {
		return countRestart;
	}

	public void setCountRestart(int countRestart) {
		this.countRestart = countRestart;
	}

	public String getTerminatedReason() {
		return terminatedReason;
	}

	public void setTerminatedReason(String terminatedReason) {
		this.terminatedReason = terminatedReason;
	}

	public BigDecimal getContainer_memory_request() {
		return container_memory_request;
	}

	public void setContainer_memory_request(BigDecimal container_memory_request) {
		this.container_memory_request = container_memory_request;
	}

	public BigDecimal getContainer_memory_limit() {
		return container_memory_limit;
	}

	public void setContainer_memory_limit(BigDecimal container_memory_limit) {
		this.container_memory_limit = container_memory_limit;
	}

	public BigDecimal getContainer_cpu_request() {
		return container_cpu_request;
	}

	public void setContainer_cpu_request(BigDecimal container_cpu_request) {
		this.container_cpu_request = container_cpu_request;
	}

	public BigDecimal getContainer_cpu_limit() {
		return container_cpu_limit;
	}

	public void setContainer_cpu_limit(BigDecimal container_cpu_limit) {
		this.container_cpu_limit = container_cpu_limit;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getPodName() {
		return this.getName();
	}

	public void setPodName(String podName) {
		this.podName = podName;
	}
	
	
	 
	 
	
}
