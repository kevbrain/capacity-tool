package com.its4u.buildfactory.services;

import java.math.BigDecimal;
import java.util.Map;

import com.its4u.buildfactory.ocp.RequestsAndLimits;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;


public class ServiceRequestsAndLimits {
	public static RequestsAndLimits extractResourceRequirementsToRequestAndLimits(ResourceRequirements resources) {
	    
    	RequestsAndLimits reqLim = new RequestsAndLimits();
    	if(resources.getLimits()!=null ) {
    		extractMapQuantityLimitsToRequestAndLimits(resources.getLimits(),reqLim);           
        }
      
        if(resources.getRequests()!=null ) {
        	extractMapQuantityRequestsToRequestAndLimits(resources.getRequests(),reqLim);          
        }      
    	return reqLim;
    }
 
 public static RequestsAndLimits extractMapQuantityLimitsToRequestAndLimits(Map<String, Quantity> quantities,RequestsAndLimits reqLim) {
	    
    	
    	for (String typeLimit : quantities.keySet()) {
    		
            if (typeLimit=="cpu" || typeLimit=="limits.cpu") { 
          
                  if (quantities.get(typeLimit).getFormat().equalsIgnoreCase("m")) {                                                  
                	  	reqLim.setLim_cpu(new BigDecimal(quantities.get(typeLimit).getAmount()));                         	  
                  } else {                                                                           
                 	reqLim.setLim_cpu(new BigDecimal(quantities.get(typeLimit).getAmount()).multiply(new BigDecimal(1000)));                       	  
                  }
            } 
            if (typeLimit=="memory" || typeLimit=="limits.memory") {    
            	
                  if (quantities.get(typeLimit).getFormat().equalsIgnoreCase("Gi")){                        
                 	 reqLim.setLim_memory((new BigDecimal(quantities.get(typeLimit).getAmount()).multiply(new BigDecimal(1024))));
                	  
                  } else {                                           
                 	 reqLim.setLim_memory(new BigDecimal(quantities.get(typeLimit).getAmount()));                                    	  
                  }
            }
            
           
    	}
    	return reqLim;
 }
    
 public static RequestsAndLimits extractMapQuantityRequestsToRequestAndLimits(Map<String, Quantity> quantities,RequestsAndLimits reqLim) {

    	for (String typeRequest : quantities.keySet()) {
    		
            if (typeRequest=="cpu" || typeRequest =="requests.cpu") {
           
	           	 if (quantities.get(typeRequest).getFormat().equalsIgnoreCase("m")) {
	           		reqLim.setReq_cpu(new BigDecimal(quantities.get(typeRequest).getAmount()));                               		
	           	 } else {
	           		reqLim.setReq_cpu(new BigDecimal(quantities.get(typeRequest).getAmount()).multiply(new BigDecimal(1000)));                               		
	           	 }
            }
            if (typeRequest=="memory" || typeRequest =="requests.memory") {
            	
	           	 if (quantities.get(typeRequest).getFormat().equalsIgnoreCase("Gi")){
	           		reqLim.setReq_memory(new BigDecimal(quantities.get(typeRequest).getAmount()).multiply(new BigDecimal(1024))); 
	           	 } else {
	           		reqLim.setReq_memory(new BigDecimal(quantities.get(typeRequest).getAmount()));                               		
	           	 }
            }
     }
    	return reqLim;
 }
}
