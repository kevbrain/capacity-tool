package com.its4u.buildfactory.rest;


public class CapacityStatus {

	
	private int currentPercentageUsageCpu;
	
	private int currentPercentageUsageMemory;
	
	private int currentPercentageUsageCpuRequested;
	
	private int fullWorkloadPercentageCpuRequested;
	
	private int currentPercentageUsageMemoryRequested;
	
	private int fullWorkloadPercentageMemoryRequested;
	
	private int availablePodsInCurrentUsage;
	
	private int availablePodsInFullWorkload;


	public CapacityStatus(int currentPercentageUsageCpu, int currentPercentageUsageMemory,
			int currentPercentageUsageCpuRequested, int fullWorkloadPercentageCpuRequested,
			int currentPercentageUsageMemoryRequested, int fullWorkloadPercentageMemoryRequested,
			int availablePodsInCurrentUsage, int availablePodsInFullWorkload) {
		super();
		this.currentPercentageUsageCpu = currentPercentageUsageCpu;
		this.currentPercentageUsageMemory = currentPercentageUsageMemory;
		this.currentPercentageUsageCpuRequested = currentPercentageUsageCpuRequested;
		this.fullWorkloadPercentageCpuRequested = fullWorkloadPercentageCpuRequested;
		this.currentPercentageUsageMemoryRequested = currentPercentageUsageMemoryRequested;
		this.fullWorkloadPercentageMemoryRequested = fullWorkloadPercentageMemoryRequested;
		this.availablePodsInCurrentUsage = availablePodsInCurrentUsage;
		this.availablePodsInFullWorkload = availablePodsInFullWorkload;
	}



	public int getCurrentPercentageUsageCpuRequested() {
		return currentPercentageUsageCpuRequested;
	}

	public void setCurrentPercentageUsageCpuRequested(int currentPercentageUsageCpuRequested) {
		this.currentPercentageUsageCpuRequested = currentPercentageUsageCpuRequested;
	}

	public int getCurrentPercentageUsageMemoryRequested() {
		return currentPercentageUsageMemoryRequested;
	}

	public void setCurrentPercentageUsageMemoryRequested(int currentPercentageUsageMemoryRequested) {
		this.currentPercentageUsageMemoryRequested = currentPercentageUsageMemoryRequested;
	}


	public int getCurrentPercentageUsageCpu() {
		return currentPercentageUsageCpu;
	}

	public void setCurrentPercentageUsageCpu(int currentPercentageUsageCpu) {
		this.currentPercentageUsageCpu = currentPercentageUsageCpu;
	}

	public int getCurrentPercentageUsageMemory() {
		return currentPercentageUsageMemory;
	}

	public void setCurrentPercentageUsageMemory(int currentPercentageUsageMemory) {
		this.currentPercentageUsageMemory = currentPercentageUsageMemory;
	}

	public int getFullWorkloadPercentageCpuRequested() {
		return fullWorkloadPercentageCpuRequested;
	}

	public void setFullWorkloadPercentageCpuRequested(int fullWorkloadPercentageCpuRequested) {
		this.fullWorkloadPercentageCpuRequested = fullWorkloadPercentageCpuRequested;
	}

	public int getFullWorkloadPercentageMemoryRequested() {
		return fullWorkloadPercentageMemoryRequested;
	}

	public void setFullWorkloadPercentageMemoryRequested(int fullWorkloadPercentageMemoryRequested) {
		this.fullWorkloadPercentageMemoryRequested = fullWorkloadPercentageMemoryRequested;
	}



	public int getAvailablePodsInCurrentUsage() {
		return availablePodsInCurrentUsage;
	}



	public void setAvailablePodsInCurrentUsage(int availablePodsInCurrentUsage) {
		this.availablePodsInCurrentUsage = availablePodsInCurrentUsage;
	}



	public int getAvailablePodsInFullWorkload() {
		return availablePodsInFullWorkload;
	}



	public void setAvailablePodsInFullWorkload(int availablePodsInFullWorkload) {
		this.availablePodsInFullWorkload = availablePodsInFullWorkload;
	}
	
	
	
	
	
	

}
