package com.its4u.buildfactory.ocp;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequestsAndLimits {
	
	private BigDecimal req_cpu = BigDecimal.ZERO;
	
	private BigDecimal lim_cpu = BigDecimal.ZERO;
	
	private BigDecimal req_memory = BigDecimal.ZERO;
	
	private BigDecimal lim_memory = BigDecimal.ZERO;
	
	public String show() {
		
		return "cpu [" + req_cpu +"/" +lim_cpu +"]   mem [" +req_memory+ "/" +lim_memory+"]";
	}

}
