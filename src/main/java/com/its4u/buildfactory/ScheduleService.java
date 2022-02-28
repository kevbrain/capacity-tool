package com.its4u.buildfactory;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.its4u.buildfactory.ocp.OcpCluster;
import com.its4u.buildfactory.ocp.OcpEnvironment;
import com.its4u.buildfactory.rest.CapacityStatus;
import com.its4u.buildfactory.services.ServiceAlerts;
import com.its4u.buildfactory.services.ServiceKubernetes;
import com.its4u.buildfactory.services.ServiceToKubernetes;
import com.its4u.buildfactory.services.ServiceWriter;
import com.its4u.buildfactory.templateModel.MailAlertModel;
import com.its4u.buildfactory.utils.MailUtils;
import com.its4u.buildfactory.utils.TemplateGenerator;

import freemarker.template.TemplateException;
import picocli.CommandLine;



@Service
public class ScheduleService {

	private static Logger logger = LoggerFactory.getLogger(ScheduleService.class);
	
	private int counter=0;
	
	private int capacityNewProjects;
	    
	private int capacityPodsCluster;
	    
    private int sim_capacityPodsCluster;
	
	private OcpCluster clusterOcp;
	
	private CapacityStatus capacityStatus;
	
	private String goNoGo;
	
	private TemplateGenerator generator;
	
	private String pathTemplate="/app/resources/templates";
	
	private boolean loaded=false;

	
	@Scheduled(fixedDelay = 180000)
	public void computeCapacity() throws InterruptedException {
		
		counter++;
		logger.info("Compute capacity cluster : "+counter);
		analyse();

	}


	public void analyse() {
    	try {
    		this.loaded=false;
    		String server = System.getenv("app.ocp.server.url");
    		String token = System.getenv("app.ocp.server.token");
    		String gonogoLevelWarning = System.getenv("app.gonogo.warning.request.level");
    		String gonogoLevelBlock = System.getenv("app.gonogo.block.request.level");
    		String name = System.getenv("app.ocp.instance.name");
    		
    		clusterOcp = new OcpCluster(name,server,token);
    		clusterOcp.loadPropertiesFromEnv();    	
    		clusterOcp.setHa("default");
    		clusterOcp.setGonogoLevelWarning(Integer.valueOf(gonogoLevelWarning));
    		clusterOcp.setGonogoLevelBlock(Integer.valueOf(gonogoLevelBlock));
    		
    		ServiceKubernetes serviceKubernetes = new ServiceKubernetes(server,token);
    		StringBuilder txtMail = new StringBuilder();
	    	clusterOcp.loadWorkers(serviceKubernetes,new ArrayList<String>());   		    	
	    	clusterOcp.loadEnvironments(serviceKubernetes);
	    	
	    	displayEvaluateWorkers(txtMail);
	    	displayEvaluateCurrentWorkload(txtMail); 
	    	displayEvaluateAvailablePodWithCurrentWorkload(txtMail);
	    	displaySimulateHAworkload(txtMail);
	    	
	    	ServiceAlerts.alertAll(clusterOcp, new StringBuilder(), serviceKubernetes);
	    	
	    	logger.info("Current usage Cpu "+ clusterOcp.getPrc_totCpu());
	    	logger.info("Current usage Memory = " +clusterOcp.getPrc_totMem());
	    	logger.info("Current usage Cpu Requested = "+clusterOcp.getPrc_totCpuRequest());
	    	logger.info("Full Workload Cpu Requested = "+clusterOcp.getSim_prc_totCpuRequest());
	    	logger.info("Current Memory Requested = "+clusterOcp.getPrc_totMemRequest());
	    	logger.info("Full Workload Memory Requested = "+clusterOcp.getSim_prc_totMemRequest());
	    	logger.info("Available Pods in current usage = "+clusterOcp.getAvailablePodsInCurrentUsage());
	    	logger.info("Available Pods in full Workload = "+clusterOcp.getAvailablePodsInFullWorkload());
	    	
	    	this.capacityStatus = new CapacityStatus(
	    			clusterOcp.getPrc_totCpu(),
	    			clusterOcp.getPrc_totMem(),
	    			clusterOcp.getPrc_totCpuRequest(),
	    			clusterOcp.getSim_prc_totCpuRequest(),
	    			clusterOcp.getPrc_totMemRequest(),
	    			clusterOcp.getSim_prc_totMemRequest(),
	    			clusterOcp.getAvailablePodsInCurrentUsage(),
	    			clusterOcp.getAvailablePodsInFullWorkload()
	    	);
	    	this.loaded=true;
	    
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		logger.info(e.getMessage());
    		
    	}
    	
	}
	
	  public void displayEvaluateWorkers(StringBuilder txtMail) {
	    	String analyseYorkers = ServiceWriter.writeWorkersAnalyse(clusterOcp,new ArrayList(clusterOcp.getNodes().values()),false,false);
	    	txtMail.append(analyseYorkers);
	    }

	    public void displayEvaluateCurrentWorkload(StringBuilder txtMail) {
	    	txtMail.append(OcpEnvironment.printHeaderHtml("Current workload"));
	    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
	    		txtMail.append(env.printItemHtml(clusterOcp));
	    	}	    		    
	    	txtMail.append(ServiceWriter.printFooterTotalHtml(clusterOcp,clusterOcp.getTot_pods(),clusterOcp.getTot_request_cpu(),clusterOcp.getTot_lim_cpu(),clusterOcp.getTot_request_memory(),clusterOcp.getTot_limit_memory()));    	
	    }
	    
	    public void displayEvaluateAvailablePodWithCurrentWorkload(StringBuilder txtMail) {
	    	BigDecimal start_request_cpu = clusterOcp.getTot_request_cpu();
	    	BigDecimal start_limit_cpu = clusterOcp.getTot_lim_cpu();
	    	BigDecimal start_memory = clusterOcp.getTot_request_memory();
	    	BigDecimal start_limit_memory = clusterOcp.getTot_limit_memory();    	
	    	this.capacityPodsCluster = clusterOcp.calculateCapacityForNewPodByCluster(start_request_cpu,start_limit_cpu,start_memory,start_limit_memory);
	    	txtMail.append("Available pods (default limits [cpu "+ clusterOcp.getDefault_request_cpu()+"/"+clusterOcp.getDefault_limit_cpu()+" mem "+clusterOcp.getDefault_request_memory()+"/"+clusterOcp.getDefault_limit_memory()+"]) with current workload : "+capacityPodsCluster);    	
	    }
	    
	    public void displaySimulateHAworkload(StringBuilder txtMail) {
	    	
	    	txtMail.append(OcpEnvironment.printHeaderHtml("Simulate workload mode ["+clusterOcp.getHa()+"]"));
	    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
	    		if (env.getName().equalsIgnoreCase("others")) {
	    			env.setNewPodsWithSimulation(env.getCurrentPods());
	    			env.setNewRequestCpuWithSimulation(env.getRequestCpu());
	    			env.setNewLimitCpuWithSimulation(env.getLimitCpu());
	    			env.setNewRequestMemoryWithSimulation(env.getRequestMemory());
	    			env.setNewLimitMemoryWithSimulation(env.getLimitMemory());
	    		}
	    		txtMail.append(env.printFullChargeItemHtml(clusterOcp));
	    	}    		  
	    	String resultsAdditionalWorkloadAfterSimulation = ServiceWriter.writeAdditionalWorkloadPerEnvAfterSimulation(clusterOcp,false,false);
	    	txtMail.append(resultsAdditionalWorkloadAfterSimulation);

	    	// compute available pod based on the mode HA
	    	BigDecimal start_request_cpu = clusterOcp.getSim_tot_request_cpu();
	    	BigDecimal start_limit_cpu = clusterOcp.getSim_tot_lim_cpu();
	    	BigDecimal start_memory = clusterOcp.getSim_tot_request_memory();
	    	BigDecimal start_limit_memory = clusterOcp.getSim_tot_limit_memory();  
	    	this.sim_capacityPodsCluster = clusterOcp.calculateCapacityForNewPodByCluster(start_request_cpu,start_limit_cpu,start_memory,start_limit_memory);
	    		    	
	    	// compute available projects [ we reserve  x pods for rolling update ]
	    	this.capacityNewProjects = (sim_capacityPodsCluster-clusterOcp.getNbrPodsForReserve())/9;    

	    }

	public int getCounter() {
		return counter;
	}


	public void setCounter(int counter) {
		this.counter = counter;
	}



	public int getCapacityNewProjects() {
		return capacityNewProjects;
	}


	public void setCapacityNewProjects(int capacityNewProjects) {
		this.capacityNewProjects = capacityNewProjects;
	}



	public OcpCluster getClusterOcp() {
		return clusterOcp;
	}



	public void setClusterOcp(OcpCluster clusterOcp) {
		this.clusterOcp = clusterOcp;
	}


	public CapacityStatus getCapacityStatus() {
		return capacityStatus;
	}


	public void setCapacityStatus(CapacityStatus capacityStatus) {
		this.capacityStatus = capacityStatus;
	}


	public String getGoNoGo() {
		String mailContent;
		
		try {
			this.generator = new TemplateGenerator(pathTemplate);
			MailAlertModel model = new MailAlertModel();
			model.setGonogoLevelBlock(clusterOcp.getGonogoLevelBlock());
			model.setGonogoLevelWarning(clusterOcp.getGonogoLevelWarning());
			model.setRequestCpu(clusterOcp.getPrc_totCpuRequest());
			model.setRequestMemory(clusterOcp.getPrc_totMemRequest());
			if (!computeIfGoNoGo()) {	
				// We raise a NOGO
				mailContent=generator.generateAlertEmail(model, generator.getMailBlockCapacity());
				MailUtils.SendMail(mailContent,null ,clusterOcp.getName()+" CapacityTool Alert");
				return "NOGO";
			} else {
				if (clusterOcp.getPrc_totCpuRequest()>=clusterOcp.getGonogoLevelWarning() || clusterOcp.getPrc_totMemRequest()>=clusterOcp.getGonogoLevelWarning()) {
					// We Raise a GO with Warning
					mailContent=generator.generateAlertEmail(model, generator.getMailWarningCapacity());
					MailUtils.SendMail(mailContent, null,clusterOcp.getName()+" CapacityTool Warning");
				}
				return "GO";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "NOGO";
		
	}


	public void setGoNoGo(String goNoGo) {
		this.goNoGo = goNoGo;
	}
	
	public boolean computeIfGoNoGo() {
		// For a Nogo , total percentage cpu or memory request must be superior than gonogoLevelBlock
		if (clusterOcp.getPrc_totCpuRequest()>=clusterOcp.getGonogoLevelBlock() || clusterOcp.getPrc_totMemRequest()>=clusterOcp.getGonogoLevelBlock()) return false;
		return true;
	}


	public boolean isLoaded() {
		return loaded;
	}


	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	
	
	
}
