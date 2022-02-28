package com.its4u.buildfactory.services;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.its4u.buildfactory.ScheduleService;
import com.its4u.buildfactory.ocp.OcpCluster;
import com.its4u.buildfactory.ocp.OcpEnvironment;
import com.its4u.buildfactory.utils.MailUtils;
import com.its4u.buildfactory.utils.SSLUtils;

import picocli.CommandLine;

/**
 * @author xrr6t
 *
 */
@CommandLine.Command(name = "checksum", mixinStandardHelpOptions = true)
public class ServiceToKubernetes implements Callable<Integer> {

    @CommandLine.Option(names = {"-t", "--token"}, description = "Token")
    private String token = "";
    
    @CommandLine.Option(names = {"-s", "--server"}, description = "URL OCP API server")
    private String server = "";
    
    @CommandLine.Option(names = {"-ha", "--ha"}, description = "HA (Full,Default,Degraded)")
    private String ha = "default";

    @CommandLine.Option(names = {"-m", "--mode"}, description = "Mode (Details,Resume,Batch,mail)")
    private String mode = "resume";
    
    private Properties prop = null;
        
    private OcpCluster clusterOcp;
        
    private boolean details = false;
    
    private boolean resume = false;
    
    private boolean batch = false;

    private boolean mail = false;
    
    private int capacityNewProjects;
    
    private int capacityPodsCluster;
    
    private int sim_capacityPodsCluster;
    
    private static Logger logger = LoggerFactory.getLogger(ServiceToKubernetes.class);
    
    
    
    public Integer call() throws Exception {
    	   	    	
    	try {
    		
    		server = System.getenv("app.ocp.server.url");
    		token = System.getenv("app.ocp.server.token");
    		String name= System.getenv("app.ocp.instance.name").toUpperCase();
    		
    		clusterOcp = new OcpCluster(name,server,token);
    		clusterOcp.loadPropertiesFromEnv();    	
    		clusterOcp.setHa(ha);
    		  		    	    	
	    	if (mode.equalsIgnoreCase("Details")) details=true;
	    	if (mode.equalsIgnoreCase("resume")) resume=true;
	    	if (mode.equalsIgnoreCase("batch")) batch=true;
	    	if (mode.equalsIgnoreCase("mail")) mail=true;
	    	
	    	if (details || resume) {
		    	logger.info("##################################################################################################################");		    			    			    	
		    	logger.info("# OCP Api : "+server);
		    	logger.info("# H.A mode : "+ha);
		    	logger.info("# Execution mode : "+mode);
		    	logger.info("# Environments :");
		    	logger.info("#       Ref Label in namespace : "+clusterOcp.getNamespace_env_label());
		    	
		    	for (OcpEnvironment env: clusterOcp.getEnvironment().values()) {
		    		logger.info("#       "+env.getName().toUpperCase()+ " Default Replicas = "+env.getDefaultMaxReplicas());
		    	}
		    	logger.info("# Prometheus :");
		    	logger.info("#       Url :"+clusterOcp.getPrometheus_url());
		    	logger.info("#       query.maxovertime : "+clusterOcp.getPrometheus_query_maxOverTime());
		    	logger.info("##################################################################################################################");
	    	}
	    	
	    	StringBuilder txtMail = new StringBuilder();
	    	ServiceKubernetes serviceKubernetes = new ServiceKubernetes(this.server,this.token);
	    	
	    	clusterOcp.loadWorkers(serviceKubernetes,new ArrayList<String>());   		    	
	    	clusterOcp.loadEnvironments(serviceKubernetes);

	    	displayEvaluateWorkers(txtMail);
	    	displayEvaluateCurrentWorkload(txtMail); 
	    	displayEvaluateAvailablePodWithCurrentWorkload(txtMail);
	    	displaySimulateHAworkload(txtMail);
	    	    		    
	    	ServiceAlerts.alertAll(clusterOcp,txtMail,serviceKubernetes);	
	    	
	    	logger.info("Capacity new project = "+capacityNewProjects);
	    	
	    	if (batch) {
	    		if (capacityNewProjects <=0 ) {
	    			throw new Exception("Unable to create Project infra in OCP. Please ,Check the cluster capacity !!");	    			
	    		}
	    		logger.info("Project creation autorized, available capacity in projects : "+capacityNewProjects);
	    	}
	    	
	    	if (mail) MailUtils.SendMail(txtMail.toString(),null,clusterOcp.getName()+" Capacity Report And Simulation");
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		logger.info(e.getMessage());
    		return -1;
    	}
        return 0;
    }
    
    public void displayEvaluateWorkers(StringBuilder txtMail) {
    	String analyseYorkers = ServiceWriter.writeWorkersAnalyse(clusterOcp,new ArrayList(clusterOcp.getNodes().values()),details,resume);
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
    	String resultsAdditionalWorkloadAfterSimulation = ServiceWriter.writeAdditionalWorkloadPerEnvAfterSimulation(clusterOcp,details,resume);
    	txtMail.append(resultsAdditionalWorkloadAfterSimulation);

    	// compute available pod based on the mode HA
    	BigDecimal start_request_cpu = clusterOcp.getSim_tot_request_cpu();
    	BigDecimal start_limit_cpu = clusterOcp.getSim_tot_lim_cpu();
    	BigDecimal start_memory = clusterOcp.getSim_tot_request_memory();
    	BigDecimal start_limit_memory = clusterOcp.getSim_tot_limit_memory();  
    	this.sim_capacityPodsCluster = clusterOcp.calculateCapacityForNewPodByCluster(start_request_cpu,start_limit_cpu,start_memory,start_limit_memory);
    		    	
    	// compute available projects [ we reserve  x pods for rolling update ]
    	this.capacityNewProjects = (sim_capacityPodsCluster-clusterOcp.getNbrPodsForReserve())/9;    

    	txtMail.append("Available pods (default limits [cpu "+ clusterOcp.getDefault_request_cpu()+"/"+clusterOcp.getDefault_limit_cpu()+" mem "+clusterOcp.getDefault_request_memory()+"/"+clusterOcp.getDefault_limit_memory()+"]) in mode "+this.ha+" : "+sim_capacityPodsCluster).append("<br/>");
    	txtMail.append("<br/>");
    	txtMail.append("<h1>Number of projects ( with dev,tst and int environments) authorized to create  : "+capacityNewProjects).append("</h1><br/>");;
    }

	public OcpCluster getClusterOcp() {
		return clusterOcp;
	}

	public void setClusterOcp(OcpCluster clusterOcp) {
		this.clusterOcp = clusterOcp;
	}

	public int getCapacityNewProjects() {
		return capacityNewProjects;
	}

	public void setCapacityNewProjects(int capacityNewProjects) {
		this.capacityNewProjects = capacityNewProjects;
	}

	public int getCapacityPodsCluster() {
		return capacityPodsCluster;
	}

	public void setCapacityPodsCluster(int capacityPodsCluster) {
		this.capacityPodsCluster = capacityPodsCluster;
	}
        
    
   
}
