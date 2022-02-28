package com.its4u.buildfactory.ocp;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.its4u.buildfactory.services.ServiceRequestsAndLimits;
import com.its4u.buildfactory.services.ServiceWriter;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class OcpNode extends OcpResource{

	private static Logger logger = LoggerFactory.getLogger(OcpNode.class);
	
	public OcpNode(String name) {
        super(name);                   
    }
	
	
    public static String printHeader() {
    	StringBuffer txt = new StringBuffer();
		String header =
    			format("Resource",50) +    	
    			format("Pods",5) +
    			" | "+
    			format("Usg CPU",13) +
    			format("%",4)+
    
    			format("Req CPU",13) +
    			format("%",4)+
    			
    			format("Lim CPU",13) +
    			format("%",4) +
    			
    			format("Usg MEM",16) +
    			format("%",4)+
       			
    			format("Req MEM",16)+
    			format("%",4)+
    			
    			format("Lim MEM",16)+
    			format("%",4);
		
		underline(header);
		logger.info(header);
		underline(header);
		
		txt.append(underline(header));
		txt.append(header);
		txt.append(underline(header));
		
		return txt.toString();
	}
    
    public static String printHeaderHtml() {
		StringBuffer txt = new StringBuffer();
		txt.append("<html>" + 
				"<style>" + 
				"table, th, td {" + 
				"  border:1px solid black;text-align: right;" + 
				"}" + 
				"</style>" + 
				"<body>");
		txt.append("<h2>Workers : Current charge  :</h2>");
		txt.append("<table>");
		txt.append("<tr>");
		txt.append("<th>"+format("Resource",50)+"</th>");
		txt.append("<th>"+format("Pods",5)+"</th>");
		txt.append("<th>"+format("Usg CPU",13)+"</th>");
		txt.append("<th>"+format("%",4)+"</th>");
		txt.append("<th>"+format("Req CPU",13)+"</th>");
		txt.append("<th>"+format("%",4)+"</th>");
		txt.append("<th>"+format("Lim CPU",13)+"</th>");
		txt.append("<th>"+format("%",4)+"</th>");
		txt.append("<th>"+format("Usg MEM",16)+"</th>");
		txt.append("<th>"+format("%",4)+"</th>");
		txt.append("<th>"+format("Req MEM",16)+"</th>");
		txt.append("<th>"+format("%",4)+"</th>");
		txt.append("<th>"+format("Lim MEM",16)+"</th>");
		txt.append("<th>"+format("%",4)+"</th>");
		txt.append("</tr>");
		
		return txt.toString();
	}
    
    public String printItemHtml() {
    	    	   	    
    	StringBuilder txt = new StringBuilder();
    	txt.append("<tr>");
    	txt.append("<td>"+format(this.getName(),50)+"</td>");
		txt.append("<td>"+format(""+this.getNbrPods(),5)+"</td>");
		txt.append("<td>"+format(this.getCurrent_cpu()+"/"+(this.getAllocatable_cpu()!=null?this.getAllocatable_cpu().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),13)+"</td>");
		txt.append(ServiceWriter.evaluatePercentColor(this.getPercentageCurrentCpu())+format(""+this.getPercentageCurrentCpu().setScale(0, BigDecimal.ROUND_HALF_UP),4)+"</td>");
		txt.append("<td>"+format(this.getRequests_cpu()+"/"+(this.getAllocatable_cpu()!=null?this.getAllocatable_cpu().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),13)+"</td>");
		txt.append(ServiceWriter.evaluatePercentColor(this.getPercentageRequestCpu())+format(""+this.getPercentageRequestCpu().setScale(0, BigDecimal.ROUND_HALF_UP),4)+"</td>");
		txt.append("<td>"+format(this.getLimits_cpu()+"/"+(this.getAllocatable_cpu()!=null?this.getAllocatable_cpu().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),13)+"</td>");
		txt.append(ServiceWriter.evaluatePercentColor(this.getPercentageLimitCpu())+format(""+this.getPercentageLimitCpu().setScale(0, BigDecimal.ROUND_HALF_UP),4)+"</td>");
		txt.append("<td>"+format(""+this.getCurrent_memory().setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+this.getAllocatable_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16)+"</td>");
		txt.append(ServiceWriter.evaluatePercentColor(this.getPercentageRequestMemory())+format(""+this.getPercentageRequestMemory().setScale(0, BigDecimal.ROUND_HALF_UP),4)+"</td>");
		txt.append("<td>"+format(this.getRequests_memory()+"/"+(this.getAllocatable_memory()!=null?this.getAllocatable_memory().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),16)+"</td>");
		txt.append(ServiceWriter.evaluatePercentColor(this.getPercentageRequestMemory())+format(""+this.getPercentageRequestMemory().setScale(0, BigDecimal.ROUND_HALF_UP),4)+"</td>");
		txt.append("<td>"+format(this.getLimits_memory()+"/"+(this.getAllocatable_memory()!=null?this.getAllocatable_memory().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),16)+"</td>");
		txt.append(ServiceWriter.evaluatePercentColor(this.getPercentageLimitMemory())+format(""+this.getPercentageLimitMemory().setScale(0, BigDecimal.ROUND_HALF_UP),4)+"</td>");
		txt.append("</tr>");
    	return txt.toString();
    			
    }
    
    
    
    public String printItem() {
    	return 
    			format(this.getName(),50)
    			+ format(""+this.getNbrPods(),5)+
    			" | " 
    			+ format(this.getCurrent_cpu()+"/"+(this.getAllocatable_cpu()!=null?this.getAllocatable_cpu().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),13)    			
				+ format(""+this.getPercentageCurrentCpu().setScale(0, BigDecimal.ROUND_HALF_UP),4)
				
				+ format(this.getRequests_cpu()+"/"+(this.getAllocatable_cpu()!=null?this.getAllocatable_cpu().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),13)    			
				+ format(""+this.getPercentageRequestCpu().setScale(0, BigDecimal.ROUND_HALF_UP),4)
				
				+ format(this.getLimits_cpu()+"/"+(this.getAllocatable_cpu()!=null?this.getAllocatable_cpu().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),13) 
				+ format(""+this.getPercentageLimitCpu().setScale(0, BigDecimal.ROUND_HALF_UP),4) 
		
				+ format(""+this.getCurrent_memory().setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+this.getAllocatable_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16)			    	
			    + format(""+this.getPercentageCurrentMemory().setScale(0, BigDecimal.ROUND_HALF_UP),4) 
			    
			    + format(this.getRequests_memory()+"/"+(this.getAllocatable_memory()!=null?this.getAllocatable_memory().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),16)    			
				+ format(""+this.getPercentageRequestMemory().setScale(0, BigDecimal.ROUND_HALF_UP),4)
				
				+ format(this.getLimits_memory()+"/"+(this.getAllocatable_memory()!=null?this.getAllocatable_memory().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),16) 
				+ format(""+this.getPercentageLimitMemory().setScale(0, BigDecimal.ROUND_HALF_UP),4);
    }
    
    public static String printFooter(OcpCluster clusterOcp,BigDecimal tot_pods,BigDecimal tot_usage_cpu,BigDecimal tot_req_cpu,BigDecimal tot_lim_cpu,BigDecimal tot_usage_memory,BigDecimal tot_req_memory,BigDecimal tot_lim_memory) {
    	
    	StringBuilder txt = new StringBuilder();
    	BigDecimal per_usage_memory = tot_usage_memory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));    	    	
    	BigDecimal per_lim_memory = tot_lim_memory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_req_memory = tot_req_memory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_usage_cpu = tot_usage_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_req_cpu = tot_req_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));    	
    	BigDecimal per_lim_cpu = tot_lim_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	
    	String footer =
    			format("TOTAL USAGE",50) +
    		  
     			format(""+tot_pods,5) +
     			" | " +    			
     			format(tot_usage_cpu+"/"+clusterOcp.getCluster_cpu(),13) +     			
     			format(""+per_usage_cpu.setScale(0, BigDecimal.ROUND_HALF_UP),4)
     			+
     			format(tot_req_cpu+"/"+clusterOcp.getCluster_cpu(),13) +
     			format(""+per_req_cpu.setScale(0, BigDecimal.ROUND_HALF_UP),4)
     			+     			
     			format(tot_lim_cpu+"/"+clusterOcp.getCluster_cpu(),13) +
     			format(""+per_lim_cpu.setScale(0, BigDecimal.ROUND_HALF_UP),4)
     			+
     			format(tot_usage_memory.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16) +
     			format(""+per_usage_memory.setScale(0, BigDecimal.ROUND_HALF_UP),4)
     			+     			
     			format(tot_req_memory.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16) +
     			format(""+per_req_memory.setScale(0, BigDecimal.ROUND_HALF_UP),4)
     			+     			
     			format(tot_lim_memory.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16) +
     			format(""+per_lim_memory.setScale(0, BigDecimal.ROUND_HALF_UP),4) ;    			
     			
    	
    	
    	underline(footer);
    	logger.info(footer);
    	underline(footer);
    	
    	txt.append(underline(footer)).append (" \n ");
    	txt.append(footer).append (" \n ");
    	txt.append(underline(footer)).append (" \n ");
    	
    	return txt.toString();
    }
    
    private static String format(String str,int length) {
    	return String.format("%1$"+length+ "s", str);
    	
    }
 
	private static String underline(String str) {
		 String und="";
		 for (int i=0;i<str.length();i++) {
			 und=und+"-";
		 }
		 logger.info(und);
		 return und;
	}
	
	public  void analysePods() {
    	for (Pod pod: this.getPodList()) {
            for (io.fabric8.kubernetes.api.model.Container container: pod.getSpec().getContainers()) {
            	ResourceRequirements resources = container.getResources();
            	RequestsAndLimits reqLim = ServiceRequestsAndLimits.extractResourceRequirementsToRequestAndLimits(resources);
            	this.setLimits_cpu(this.getLimits_cpu().add(reqLim.getLim_cpu()));
            	this.setLimits_memory(this.getLimits_memory().add(reqLim.getLim_memory()));
            	this.setRequests_cpu(this.getRequests_cpu().add(reqLim.getReq_cpu()));
            	this.setRequests_memory(this.getRequests_memory().add(reqLim.getReq_memory()));
                }
          }
    }
}
