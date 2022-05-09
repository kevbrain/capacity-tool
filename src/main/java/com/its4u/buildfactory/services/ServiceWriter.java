package com.its4u.buildfactory.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.its4u.buildfactory.ocp.OcpCluster;
import com.its4u.buildfactory.ocp.OcpEnvironment;
import com.its4u.buildfactory.ocp.OcpNamespace;
import com.its4u.buildfactory.ocp.OcpNode;



public class ServiceWriter {
	
	  private static Logger logger = LoggerFactory.getLogger(ServiceWriter.class);
	
	  public static String writeWorkersAnalyse(OcpCluster clusterOcp,List<OcpNode> ocpNodeList, boolean details, boolean resume) {
	    	
	    	StringBuilder txt = new StringBuilder();
	    	
	    	BigDecimal tot_pods = BigDecimal.ZERO;
	    	BigDecimal tot_usage_cpu = BigDecimal.ZERO;
	    	BigDecimal tot_req_cpu = BigDecimal.ZERO;
	    	BigDecimal tot_lim_cpu = BigDecimal.ZERO;
	    	BigDecimal tot_usage_memory = BigDecimal.ZERO;
	    	BigDecimal tot_req_memory = BigDecimal.ZERO;
	    	BigDecimal tot_lim_memory = BigDecimal.ZERO;
	    	
	    	txt.append(OcpNode.printHeaderHtml());
	    	
	    	if (details || resume ) OcpNode.printHeader();
	    	for (OcpNode worker: ocpNodeList) {
	    		tot_pods = tot_pods.add(BigDecimal.valueOf(worker.getNbrPods()));
	    		
	    		tot_usage_cpu = tot_usage_cpu.add(worker.getCurrent_cpu());
	    		tot_req_cpu = tot_req_cpu.add(worker.getRequests_cpu());
	    		tot_lim_cpu = tot_lim_cpu.add(worker.getLimits_cpu());
	    		
	    		tot_usage_memory = tot_usage_memory.add(worker.getCurrent_memory());    		
	    		tot_req_memory = tot_req_memory.add(worker.getRequests_memory());
	    		tot_lim_memory = tot_lim_memory.add(worker.getLimits_memory());
	    		    		
	    		if (details || resume ) logger.info(worker.printItem());
	    		txt.append(worker.printItemHtml());
	    	}
	    	
	    	
	    	if (details || resume ) OcpNode.printFooter(clusterOcp,tot_pods,tot_usage_cpu,tot_req_cpu,tot_lim_cpu,tot_usage_memory,tot_req_memory,tot_lim_memory);
	    	txt.append(printFooterHtml(clusterOcp,tot_pods,tot_usage_cpu,tot_req_cpu,tot_lim_cpu,tot_usage_memory,tot_req_memory,tot_lim_memory));
	    	
	    	clusterOcp.setTot_pods(tot_pods.intValue());
	    	clusterOcp.setTot_usage_cpu(tot_usage_cpu);
	    	clusterOcp.setTot_request_cpu(tot_req_cpu);
	    	clusterOcp.setTot_lim_cpu(tot_lim_cpu);
	    	clusterOcp.setTot_usage_memory(tot_usage_memory);
	    	clusterOcp.setTot_request_memory(tot_req_memory);
	    	clusterOcp.setTot_limit_memory(tot_lim_memory);
	    	
	    	return txt.toString();
	    }
	  
	  public static String printFooterHtml(OcpCluster clusterOcp,BigDecimal tot_pods,BigDecimal tot_usage_cpu,BigDecimal tot_req_cpu,BigDecimal tot_lim_cpu,BigDecimal tot_usage_memory,BigDecimal tot_req_memory,BigDecimal tot_lim_memory) {
	    	StringBuilder txt = new StringBuilder();

	    	BigDecimal per_usage_memory = tot_usage_memory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));    	    	
	    	BigDecimal per_lim_memory = tot_lim_memory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
	    	BigDecimal per_req_memory = tot_req_memory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
	    	BigDecimal per_usage_cpu = tot_usage_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
	    	BigDecimal per_req_cpu = tot_req_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));    	
	    	BigDecimal per_lim_cpu = tot_lim_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
	    	
	    	
	    	txt.append("<tr>");
	    	txt.append("<td>"+"Totals :"+"</td>");
	    	txt.append("<td>"+tot_pods+"</td>");    	
	    	txt.append("<td>"+tot_usage_cpu+"/"+clusterOcp.getCluster_cpu()+"</td>");
	    	txt.append(evaluatePercentColor(per_usage_cpu)+per_usage_cpu.setScale(0, RoundingMode.HALF_UP)+"</td>");
	    	txt.append("<td>"+tot_req_cpu+"/"+clusterOcp.getCluster_cpu()+"</td>");
	    	txt.append(evaluatePercentColor(per_req_cpu)+per_req_cpu.setScale(0, RoundingMode.HALF_UP)+"</b></td>");    	
	    	txt.append("<td>"+tot_lim_cpu+"/"+clusterOcp.getCluster_cpu()+"</td>");
	    	txt.append(evaluatePercentColor(per_lim_cpu)+per_lim_cpu.setScale(0, RoundingMode.HALF_UP)+"</td>");
	    	txt.append("<td>"+tot_usage_memory.setScale(0, RoundingMode.HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, RoundingMode.HALF_UP)+"</td>");
	    	txt.append(evaluatePercentColor(per_usage_memory)+per_usage_memory.setScale(0, RoundingMode.HALF_UP)+"</td>");
	    	txt.append("<td>"+tot_req_memory.setScale(0, RoundingMode.HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, RoundingMode.HALF_UP)+"</td>");
	    	txt.append(evaluatePercentColor(per_req_memory)+per_req_memory.setScale(0, RoundingMode.HALF_UP)+"</b></td>");
	    	txt.append("<td>"+tot_lim_memory.setScale(0, RoundingMode.HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, RoundingMode.HALF_UP)+"</td>");
	    	txt.append(evaluatePercentColor(per_lim_memory)+per_lim_memory.setScale(0, RoundingMode.HALF_UP)+"</td>");
	    	txt.append("</tr>");
	    	    	
	    	txt.append("</table>");
	    	return txt.toString();
	    }

	public static String writeAnalyseOfNamespaces(OcpCluster clusterOcp,String env, boolean details) {
    	
    	StringBuilder txt = new StringBuilder();
    	
    	OcpEnvironment envi = clusterOcp.getEnvironment().get(env) ;
    	
    	List<OcpNamespace> ocpResourceList = clusterOcp.getEnvironment().get(env).getNamespaces();
    	    	
    	BigDecimal tot_pods = BigDecimal.ZERO;
    	BigDecimal tot_usage_cpu = BigDecimal.ZERO;
    	BigDecimal tot_req_cpu = BigDecimal.ZERO;
    	BigDecimal tot_max_cpu = BigDecimal.ZERO;
    	BigDecimal tot_lim_cpu = BigDecimal.ZERO;
    	BigDecimal tot_usage_memory = BigDecimal.ZERO;
    	BigDecimal tot_req_memory = BigDecimal.ZERO;
    	BigDecimal tot_max_memory = BigDecimal.ZERO;
    	BigDecimal tot_lim_memory = BigDecimal.ZERO;

    	for (OcpNamespace namespace:ocpResourceList) {
    		tot_pods = tot_pods.add(BigDecimal.valueOf(namespace.getNbrPods()));
    		tot_usage_cpu = tot_usage_cpu.add(namespace.getCurrent_cpu());
    		tot_max_cpu = tot_max_cpu.add(namespace.getMax_cpu_last2w());
    		tot_req_cpu = tot_req_cpu.add(namespace.getRequests_cpu());
    		tot_lim_cpu = tot_lim_cpu.add(namespace.getLimits_cpu());  
    		tot_usage_memory = tot_usage_memory.add(namespace.getCurrent_memory());
    		tot_max_memory = tot_max_memory.add(namespace.getMax_memory_last2w());    
    		tot_req_memory = tot_req_memory.add(namespace.getRequests_memory());
    		tot_lim_memory = tot_lim_memory.add(namespace.getLimits_memory());

    	} 
 
    	envi.setCurrentPods(tot_pods.intValue());
    	envi.setUsageCpu(tot_usage_cpu);
    	envi.setUsageMemory(tot_usage_memory);
    	envi.setCurrentRequestCpu(tot_req_cpu);
    	envi.setCurrentLimitCpu(tot_lim_cpu);
    	envi.setCurrentRequestMemory(tot_req_memory);
    	envi.setCurentLimitMemory(tot_lim_memory);
    	envi.setRequestCpu(tot_req_cpu);
    	envi.setRequestMemory(tot_req_memory);
    	envi.setLimitCpu(tot_lim_cpu);
    	envi.setLimitMemory(tot_lim_memory);
    	
    	clusterOcp.setTot_usage_cpu(clusterOcp.getTot_usage_cpu().add(tot_usage_cpu));
    	clusterOcp.setTot_usage_memory(clusterOcp.getTot_usage_memory().add(tot_usage_memory));
    	    	    	
    	clusterOcp.setTot_pods(clusterOcp.getTot_pods()+tot_pods.intValue());
    	clusterOcp.setTot_request_cpu(clusterOcp.getTot_request_cpu().add(tot_req_cpu));
    	clusterOcp.setTot_lim_cpu(clusterOcp.getTot_lim_cpu().add(tot_lim_cpu));
    	clusterOcp.setTot_request_memory(clusterOcp.getTot_request_memory().add(tot_req_memory));
    	clusterOcp.setTot_limit_memory(clusterOcp.getTot_limit_memory().add(tot_lim_memory));
    	
    	clusterOcp.setPrc_totCpuRequest(envi.getPer_req_cpu().intValue()+clusterOcp.getPrc_totCpuRequest());
    	clusterOcp.setPrc_totCpuLimit(envi.getPer_lim_cpu().intValue()+clusterOcp.getPrc_totCpuLimit());
    	clusterOcp.setPrc_totMemRequest(envi.getPer_req_memory().intValue()+clusterOcp.getPrc_totMemRequest());
    	clusterOcp.setPrc_totMemlimit(envi.getPer_lim_memory().intValue()+clusterOcp.getPrc_totMemlimit());
    
    	return txt.toString();
    }

	public static void writeSimulateAnalyseOfNamespaces(OcpCluster clusterOcp,String env, boolean details) {
	
		List<OcpNamespace> ocpResourceList = clusterOcp.getEnvironment().get(env).getNamespaces();
		
		BigDecimal tot_pods = BigDecimal.ZERO;
		BigDecimal tot_new_pods = BigDecimal.ZERO;
		BigDecimal tot_pod_simulation = BigDecimal.ZERO;
		BigDecimal tot_new_req_cpu = BigDecimal.ZERO;
		BigDecimal tot_new_lim_cpu = BigDecimal.ZERO;
		BigDecimal tot_new_req_mem = BigDecimal.ZERO;
		BigDecimal tot_new_lim_mem = BigDecimal.ZERO;
		
		for (OcpNamespace namespace:ocpResourceList) {
				tot_pods = tot_pods.add(BigDecimal.valueOf(namespace.getNbrPods()));
				tot_new_pods = tot_new_pods.add(BigDecimal.valueOf(namespace.getNbrPodPossibleToDeploy()));
				tot_pod_simulation = tot_pod_simulation.add(BigDecimal.valueOf(namespace.getNbrPodSimulation()));
				tot_new_req_cpu = tot_new_req_cpu.add(namespace.getNewRequest_cpu());
				tot_new_lim_cpu = tot_new_lim_cpu.add(namespace.getNewLimit_cpu());
				tot_new_req_mem = tot_new_req_mem.add(namespace.getNewRequest_memory());
				tot_new_lim_mem = tot_new_lim_mem.add(namespace.getNewLimit_memory());
		}
	
		clusterOcp.setSim_tot_lim_cpu(clusterOcp.getSim_tot_lim_cpu().add(tot_new_lim_cpu));
		clusterOcp.setSim_tot_request_cpu(clusterOcp.getSim_tot_request_cpu().add(tot_new_req_cpu));
		clusterOcp.setSim_tot_request_memory(clusterOcp.getSim_tot_request_memory().add(tot_new_req_mem));
		clusterOcp.setSim_tot_limit_memory(clusterOcp.getSim_tot_limit_memory().add(tot_new_lim_mem));
		
		OcpEnvironment envi = clusterOcp.getEnvironment().get(env) ;		
		
		envi.setNewPodsWithSimulation(tot_pod_simulation.intValue());
		envi.setNewRequestCpuWithSimulation(tot_new_req_cpu);
		envi.setNewLimitCpuWithSimulation(tot_new_lim_cpu);
		envi.setNewRequestMemoryWithSimulation(tot_new_req_mem);
		envi.setNewLimitMemoryWithSimulation(tot_new_lim_mem);
	
		clusterOcp.setSim_prc_totCpuRequest(envi.getNew_per_req_cpu().intValue()+clusterOcp.getSim_prc_totCpuRequest());
		clusterOcp.setSim_prc_totCpuLimit(envi.getNew_per_lim_cpu().intValue()+clusterOcp.getSim_prc_totCpuLimit());
		clusterOcp.setSim_prc_totMemRequest(envi.getNew_per_req_memory().intValue()+clusterOcp.getSim_prc_totMemRequest());
		clusterOcp.setSim_prc_totMemlimit(envi.getNew_per_lim_memory().intValue()+clusterOcp.getSim_prc_totMemlimit());
		
		    
		envi.setAdditionalPodWithSimulation(tot_pod_simulation.intValue()-envi.getCurrentPods());
		if (envi.getCurrentRequestCpu()!=null) envi.setAdditionalRequestCpuWithSimulation(tot_new_req_cpu.subtract(envi.getCurrentRequestCpu()));
		if (envi.getCurrentLimitCpu()!=null) envi.setAdditionalLimitCpuWithSimulation(tot_new_lim_cpu.subtract(envi.getCurrentLimitCpu()));
		if (envi.getCurrentRequestMemory()!=null) envi.setAdditionalRequestMemoryWithSimulation(tot_new_req_mem.subtract(envi.getCurrentRequestMemory()));
		if (envi.getCurentLimitMemory()!=null) envi.setAdditionalLimitMemoryWithSimulation(tot_new_lim_mem.subtract(envi.getCurentLimitMemory()));
	}
	
	public static String writeAdditionalWorkloadPerEnvAfterSimulation(OcpCluster clusterOcp, boolean details, boolean resume) {
    	
    	int newTotPods = clusterOcp.getTot_pods();
    	BigDecimal newTotRequestCpu = clusterOcp.getTot_request_cpu();
    	BigDecimal newTotLimitCpu = clusterOcp.getTot_lim_cpu();
    	BigDecimal newTotRequestMemory = clusterOcp.getTot_request_memory();
    	BigDecimal newTotLimitMemory = clusterOcp.getTot_limit_memory();
    	
    	       	
    	for (OcpEnvironment env : clusterOcp.getEnvironment().values()) {
    		if (!env.getName().equalsIgnoreCase("others")) {
    			if (details || resume ) logger.info(env.printAdditionalValues());
	    		newTotPods = newTotPods + env.getAdditionalPodWithSimulation();
	    		newTotRequestCpu = newTotRequestCpu.add(env.getAdditionalRequestCpuWithSimulation());
	    		newTotLimitCpu = newTotLimitCpu.add(env.getAdditionalLimitCpuWithSimulation());
	    		newTotRequestMemory = newTotRequestMemory.add(env.getAdditionalRequestMemoryWithSimulation());
	    		newTotLimitMemory = newTotLimitMemory.add(env.getAdditionalLimitMemoryWithSimulation());
    		}
    	}    
    	clusterOcp.setSim_tot_request_cpu(newTotRequestCpu);
    	clusterOcp.setSim_tot_lim_cpu(newTotLimitCpu);
    	clusterOcp.setSim_tot_request_memory(newTotRequestMemory);
    	clusterOcp.setSim_tot_limit_memory(newTotLimitMemory);
    	//if (details || resume ) OcpEnvironment.printFooter(clusterOcp,newTotPods,newTotRequestCpu,newTotLimitCpu,newTotRequestMemory,newTotLimitMemory);
    	return ServiceWriter.printFooterTotalHtml(clusterOcp,newTotPods,newTotRequestCpu,newTotLimitCpu,newTotRequestMemory,newTotLimitMemory);
    	
    	
    	
    }
	
	public static String printFooterTotalHtml(OcpCluster clusterOcp,int newTotPods,BigDecimal newTotRequestCpu,BigDecimal newTotLimitCpu, BigDecimal newTotRequestMemory,BigDecimal newTotLimitMemory) {
		
		StringBuilder txt = new StringBuilder();
    	BigDecimal per_lim_memory = newTotLimitMemory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_req_memory = newTotRequestMemory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	
    	BigDecimal per_req_cpu = newTotRequestCpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));    	
    	BigDecimal per_lim_cpu = newTotLimitCpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	
    	BigDecimal per_quot_req_cpu = clusterOcp.getTotQuotaCpuRequest().divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_quot_lim_cpu = clusterOcp.getTotQuotaCpuLimit().divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));    	
    	BigDecimal per_quot_req_memory = clusterOcp.getTotQuotaMemoryRequest().divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)); 
    	BigDecimal per_quot_lim_memory = clusterOcp.getTotQuotaMemoryLimit().divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));;
    	
    	txt.append("<tr>");
    	txt.append("<td colspan=3>TOTALS</td>");		
		
		
		txt.append("<td>"+newTotRequestCpu+"</td>");
		txt.append(evaluatePercentColor(per_req_cpu)+per_req_cpu.setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+newTotLimitCpu+"</td>");
		txt.append(evaluatePercentColor(per_lim_cpu)+per_lim_cpu.setScale(0, RoundingMode.HALF_UP)+"</td>");
		
		txt.append("<td>"+newTotRequestMemory.setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append(evaluatePercentColor(per_req_memory)+per_req_memory.setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+newTotLimitMemory.setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append(evaluatePercentColor(per_lim_memory)+per_lim_memory.setScale(0, RoundingMode.HALF_UP)+"</td>");
		
		txt.append("<td>"+clusterOcp.getTotQuotaCpuRequest().setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append(evaluatePercentColor(per_quot_req_cpu)+per_quot_req_cpu.setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+clusterOcp.getTotQuotaCpuLimit().setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append(evaluatePercentColor(per_quot_lim_cpu)+per_quot_lim_cpu.setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+clusterOcp.getTotQuotaMemoryRequest().setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append(evaluatePercentColor(per_quot_req_memory)+per_quot_req_memory.setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+clusterOcp.getTotQuotaMemoryLimit().setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append(evaluatePercentColor(per_quot_lim_memory)+per_quot_lim_memory.setScale(0, RoundingMode.HALF_UP)+"</td>");
		
		txt.append("</tr>");
		txt.append("</table>").append("</body>").append("</html>");
		return txt.toString();
	}
	
	
	
	public static String evaluatePercentColor(BigDecimal percent) {
		String balise="<td";
		if (percent.compareTo(new BigDecimal(100))>0) {
			balise=balise+" style=\"background-color:#FF0000;color:white;font-weight: bold;\"><b>";
			return balise;
		} if (percent.compareTo(new BigDecimal(50))>0) {
			balise=balise+" style=\"background-color:#F5B041;color:black;font-weight: bold;\"><b>";
			return balise;
		} else  {
			balise=balise+" style=\"background-color:#00FF00;color:black;font-weight: bold;\"><b>";
		}
		return balise;
	}
}
