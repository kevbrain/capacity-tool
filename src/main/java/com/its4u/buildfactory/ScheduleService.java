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
import com.its4u.buildfactory.services.ServiceWriter;
import com.its4u.buildfactory.templateModel.MailAlertModel;
import com.its4u.buildfactory.utils.MailUtils;
import com.its4u.buildfactory.utils.TemplateGenerator;

import freemarker.template.TemplateException;



@Service
public class ScheduleService {

	private static Logger logger = LoggerFactory.getLogger(ScheduleService.class);
	
	private int counter=0;
	
	private int capacityNewProjects;
	    
	private int capacityPodsCluster;
	    
    private int sim_capacityPodsCluster;
	
	private OcpCluster clusterOcpAnalyseInProgess;
	
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
    		String prefixWorker = System.getenv("app.ocp.worker.prefix");
    		String appEnvsViewGroup1Label = System.getenv("app.group1.namespace.label");
    		String appEnvsViewGroup2Label = System.getenv("app.group2.namespace.label");
    		
    		clusterOcpAnalyseInProgess = new OcpCluster(name,server,token);
    		clusterOcpAnalyseInProgess.loadPropertiesFromEnv();    	
    		clusterOcpAnalyseInProgess.setHa("default");
    		clusterOcpAnalyseInProgess.setGonogoLevelWarning(Integer.valueOf(gonogoLevelWarning));
    		clusterOcpAnalyseInProgess.setGonogoLevelBlock(Integer.valueOf(gonogoLevelBlock));
    		clusterOcpAnalyseInProgess.setWorkerPrefix(prefixWorker);
    		clusterOcpAnalyseInProgess.setGroupView_label_1(appEnvsViewGroup1Label);
    		clusterOcpAnalyseInProgess.setGroupView_label_2(appEnvsViewGroup2Label);
    		
    		ServiceKubernetes serviceKubernetes = new ServiceKubernetes(server,token);
    		StringBuilder txtMail = new StringBuilder();
    		
    		logger.info("Load workers ");    		
	    	clusterOcpAnalyseInProgess.loadWorkers(serviceKubernetes,new ArrayList<String>()); 
	    	logger.info("Load environments ");
	    	clusterOcpAnalyseInProgess.loadEnvironments(serviceKubernetes);
	    	
	    	displayEvaluateWorkers(txtMail);
	    	displayEvaluateCurrentWorkload(txtMail); 
	    	displayEvaluateAvailablePodWithCurrentWorkload(txtMail);
	    	displaySimulateHAworkload(txtMail);
	    	
	    	ServiceAlerts.alertAll(clusterOcpAnalyseInProgess, new StringBuilder(), serviceKubernetes);
	    	
	    	logger.info("Current usage Cpu "+ clusterOcpAnalyseInProgess.getPrc_totCpu());
	    	logger.info("Current usage Memory = " +clusterOcpAnalyseInProgess.getPrc_totMem());
	    	logger.info("Current usage Cpu Requested = "+clusterOcpAnalyseInProgess.getPrc_totCpuRequest());
	    	logger.info("Full Workload Cpu Requested = "+clusterOcpAnalyseInProgess.getSim_prc_totCpuRequest());
	    	logger.info("Current Memory Requested = "+clusterOcpAnalyseInProgess.getPrc_totMemRequest());
	    	logger.info("Full Workload Memory Requested = "+clusterOcpAnalyseInProgess.getSim_prc_totMemRequest());
	    	logger.info("Available Pods in current usage = "+clusterOcpAnalyseInProgess.getAvailablePodsInCurrentUsage());
	    	logger.info("Available Pods in full Workload = "+clusterOcpAnalyseInProgess.getAvailablePodsInFullWorkload());
	    	
	    	this.capacityStatus = new CapacityStatus(
	    			clusterOcpAnalyseInProgess.getPrc_totCpu(),
	    			clusterOcpAnalyseInProgess.getPrc_totMem(),
	    			clusterOcpAnalyseInProgess.getPrc_totCpuRequest(),
	    			clusterOcpAnalyseInProgess.getSim_prc_totCpuRequest(),
	    			clusterOcpAnalyseInProgess.getPrc_totMemRequest(),
	    			clusterOcpAnalyseInProgess.getSim_prc_totMemRequest(),
	    			clusterOcpAnalyseInProgess.getAvailablePodsInCurrentUsage(),
	    			clusterOcpAnalyseInProgess.getAvailablePodsInFullWorkload()
	    	);
	    	this.loaded=true;
	    	this.clusterOcp = clusterOcpAnalyseInProgess;
	    
	    	
    	} catch (Exception e) {
    		logger.info(e.getMessage());    		
    	}
    	
	}
	
	  public void displayEvaluateWorkers(StringBuilder txtMail) {
	    	String analyseYorkers = ServiceWriter.writeWorkersAnalyse(clusterOcpAnalyseInProgess,new ArrayList(clusterOcpAnalyseInProgess.getNodes().values()),false,false);
	    	txtMail.append(analyseYorkers);
	    }

	    public void displayEvaluateCurrentWorkload(StringBuilder txtMail) {
	    	txtMail.append(OcpEnvironment.printHeaderHtml("Current workload"));
	    	for (OcpEnvironment env:clusterOcpAnalyseInProgess.getEnvironment().values()) {
	    		txtMail.append(env.printItemHtml(clusterOcpAnalyseInProgess));
	    	}	    		    
	    	txtMail.append(ServiceWriter.printFooterTotalHtml(clusterOcpAnalyseInProgess,clusterOcpAnalyseInProgess.getTot_pods(),clusterOcpAnalyseInProgess.getTot_request_cpu(),clusterOcpAnalyseInProgess.getTot_lim_cpu(),clusterOcpAnalyseInProgess.getTot_request_memory(),clusterOcpAnalyseInProgess.getTot_limit_memory()));    	
	    }
	    
	    public void displayEvaluateAvailablePodWithCurrentWorkload(StringBuilder txtMail) {
	    	BigDecimal start_request_cpu = clusterOcpAnalyseInProgess.getTot_request_cpu();
	    	BigDecimal start_limit_cpu = clusterOcpAnalyseInProgess.getTot_lim_cpu();
	    	BigDecimal start_memory = clusterOcpAnalyseInProgess.getTot_request_memory();
	    	BigDecimal start_limit_memory = clusterOcpAnalyseInProgess.getTot_limit_memory();    	
	    	this.capacityPodsCluster = clusterOcpAnalyseInProgess.calculateCapacityForNewPodByCluster(start_request_cpu,start_limit_cpu,start_memory,start_limit_memory);
	    	txtMail.append("Available pods (default limits [cpu "+ clusterOcpAnalyseInProgess.getDefault_request_cpu()+"/"+clusterOcpAnalyseInProgess.getDefault_limit_cpu()+" mem "+clusterOcpAnalyseInProgess.getDefault_request_memory()+"/"+clusterOcpAnalyseInProgess.getDefault_limit_memory()+"]) with current workload : "+capacityPodsCluster);    	
	    }
	    
	    public void displaySimulateHAworkload(StringBuilder txtMail) {
	    	
	    	txtMail.append(OcpEnvironment.printHeaderHtml("Simulate workload mode ["+clusterOcpAnalyseInProgess.getHa()+"]"));
	    	for (OcpEnvironment env:clusterOcpAnalyseInProgess.getEnvironment().values()) {
	    		if (env.getName().equalsIgnoreCase("others")) {
	    			env.setNewPodsWithSimulation(env.getCurrentPods());
	    			env.setNewRequestCpuWithSimulation(env.getRequestCpu());
	    			env.setNewLimitCpuWithSimulation(env.getLimitCpu());
	    			env.setNewRequestMemoryWithSimulation(env.getRequestMemory());
	    			env.setNewLimitMemoryWithSimulation(env.getLimitMemory());
	    		}
	    		txtMail.append(env.printFullChargeItemHtml(clusterOcpAnalyseInProgess));
	    	}    		  
	    	String resultsAdditionalWorkloadAfterSimulation = ServiceWriter.writeAdditionalWorkloadPerEnvAfterSimulation(clusterOcpAnalyseInProgess,false,false);
	    	txtMail.append(resultsAdditionalWorkloadAfterSimulation);

	    	// compute available pod based on the mode HA
	    	BigDecimal start_request_cpu = clusterOcpAnalyseInProgess.getSim_tot_request_cpu();
	    	BigDecimal start_limit_cpu = clusterOcpAnalyseInProgess.getSim_tot_lim_cpu();
	    	BigDecimal start_memory = clusterOcpAnalyseInProgess.getSim_tot_request_memory();
	    	BigDecimal start_limit_memory = clusterOcpAnalyseInProgess.getSim_tot_limit_memory();  
	    	this.sim_capacityPodsCluster = clusterOcpAnalyseInProgess.calculateCapacityForNewPodByCluster(start_request_cpu,start_limit_cpu,start_memory,start_limit_memory);
	    		    	
	    	// compute available projects [ we reserve  x pods for rolling update ]
	    	this.capacityNewProjects = (sim_capacityPodsCluster-clusterOcpAnalyseInProgess.getNbrPodsForReserve())/9;    

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



	public OcpCluster getclusterOcpAnalyseInProgess() {
		return clusterOcpAnalyseInProgess;
	}



	public void setclusterOcpAnalyseInProgess(OcpCluster clusterOcpAnalyseInProgess) {
		this.clusterOcpAnalyseInProgess = clusterOcpAnalyseInProgess;
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
			model.setGonogoLevelBlock(clusterOcpAnalyseInProgess.getGonogoLevelBlock());
			model.setGonogoLevelWarning(clusterOcpAnalyseInProgess.getGonogoLevelWarning());
			model.setRequestCpu(clusterOcpAnalyseInProgess.getPrc_totCpuRequest());
			model.setRequestMemory(clusterOcpAnalyseInProgess.getPrc_totMemRequest());
			if (!computeIfGoNoGo()) {	
				// We raise a NOGO
				mailContent=generator.generateAlertEmail(model, generator.getMailBlockCapacity());
				MailUtils.SendMail(mailContent,null ,clusterOcpAnalyseInProgess.getName()+" CapacityTool Alert");
				return "NOGO";
			} else {
				if (clusterOcpAnalyseInProgess.getPrc_totCpuRequest()>=clusterOcpAnalyseInProgess.getGonogoLevelWarning() || clusterOcpAnalyseInProgess.getPrc_totMemRequest()>=clusterOcpAnalyseInProgess.getGonogoLevelWarning()) {
					// We Raise a GO with Warning
					mailContent=generator.generateAlertEmail(model, generator.getMailWarningCapacity());
					MailUtils.SendMail(mailContent, null,clusterOcpAnalyseInProgess.getName()+" CapacityTool Warning");
				}
				return "GO";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			
		}
		return "NOGO";
		
	}


	public void setGoNoGo(String goNoGo) {
		this.goNoGo = goNoGo;
	}
	
	public boolean computeIfGoNoGo() {
		// For a Nogo , total percentage cpu or memory request must be superior than gonogoLevelBlock
		if (clusterOcpAnalyseInProgess.getPrc_totCpuRequest()>=clusterOcpAnalyseInProgess.getGonogoLevelBlock() || clusterOcpAnalyseInProgess.getPrc_totMemRequest()>=clusterOcpAnalyseInProgess.getGonogoLevelBlock()) return false;
		return true;
	}


	public boolean isLoaded() {
		return loaded;
	}


	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}


	public OcpCluster getClusterOcpAnalyseInProgess() {
		return clusterOcpAnalyseInProgess;
	}


	public void setClusterOcpAnalyseInProgess(OcpCluster clusterOcpAnalyseInProgess) {
		this.clusterOcpAnalyseInProgess = clusterOcpAnalyseInProgess;
	}


	public OcpCluster getClusterOcp() {
		return clusterOcp;
	}


	public void setClusterOcp(OcpCluster clusterOcp) {
		this.clusterOcp = clusterOcp;
	}
	
	
	
}
