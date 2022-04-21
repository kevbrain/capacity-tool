package com.its4u.buildfactory.ocp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.its4u.buildfactory.services.PrometheusService;
import com.its4u.buildfactory.services.ServiceKubernetes;
import com.its4u.buildfactory.services.ServiceRequestsAndLimits;
import com.its4u.buildfactory.utils.FormatUtils;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.LimitRange;
import io.fabric8.kubernetes.api.model.LimitRangeItem;
import io.fabric8.kubernetes.api.model.LimitRangeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.api.model.ResourceQuotaList;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.ContainerMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.kubernetes.client.KubernetesClientException;



public class OcpNamespace extends OcpResource{

	private static Logger logger = LoggerFactory.getLogger(OcpNamespace.class);
	
	private OcpEnvironment ocpEnvironment;
	
    private boolean protectedByLimits=false;
                  
    private boolean protectedByQuotas=false;
    
    private boolean rollingUpdate=false;
    
    private BigDecimal container_memory_request=BigDecimal.ZERO;
    
    private BigDecimal container_memory_limit=BigDecimal.ZERO;
    
    private BigDecimal container_cpu_request=BigDecimal.ZERO;
    
    private BigDecimal container_cpu_limit=BigDecimal.ZERO;
    
    private BigDecimal newRequest_cpu=BigDecimal.ZERO;
    
    private BigDecimal newLimit_cpu=BigDecimal.ZERO;
    
    private BigDecimal newRequest_memory=BigDecimal.ZERO;
	
    private BigDecimal newLimit_memory=BigDecimal.ZERO;
    
    private BigDecimal per_newRequest_cpu=BigDecimal.ZERO;;
    
    private BigDecimal per_newLimit_cpu=BigDecimal.ZERO;
    
    private BigDecimal per_newRequest_memory=BigDecimal.ZERO;
    
    private BigDecimal per_newLimit_memory=BigDecimal.ZERO;
    
        
    private int nbrPodPossibleToDeploy = 0;
    
    private int nbrPodSimulation = 0;
    
    private String limitsRecommendations ;
    
    private boolean containsRedis;
    
    private String team;
    
    private String valueChain;
    
    private String teamEmail;
    
 
	public OcpNamespace(String name) {
		super(name);
	}

	public OcpNamespace(OcpEnvironment ocpEnvironment, String name) {
           super(name);
           this.ocpEnvironment = ocpEnvironment;
    }
    
    public static void printHeaderSimulateCharge() {
		String header =
    			FormatUtils.format("Resource",50) +
      			FormatUtils.format("CPU [req/lim]",15)+
    			FormatUtils.format("MEM [req/lim]",15)+
    			FormatUtils.format("Pods",5) +
    			" | "+
    			FormatUtils.format("AvaP" ,5)+
    			" | "+
    			FormatUtils.format("RolU",3)+
    			" | "+
    			FormatUtils.format("SimP" ,5)+
    			" | "+
    			FormatUtils.format("Req CPU/Quot",16) +
    			FormatUtils.format("%",4)+    			
    			FormatUtils.format("Lim CPU/Quot",16) +
    			FormatUtils.format("%",4)+
    			FormatUtils.format("Req MEM/Quot",16) +
    			FormatUtils.format("%",4)+
				FormatUtils.format("Lim MEM/Quot",16) +
				FormatUtils.format("%",4);
		FormatUtils.underline(header);
		logger.info(header);
		FormatUtils.underline(header);
	}
    
    public static String printHeaderCpu() {
    	StringBuilder txt = new StringBuilder();
		String header =
    			FormatUtils.format("Resource",50) +
    			FormatUtils.format(" L Q",4) +
    			FormatUtils.format("CPU [req/lim]",15)+
    			FormatUtils.format("MEM [req/lim]",15)+
    			FormatUtils.format("Pods",5) +
    			" | "+
    			FormatUtils.format("Usg CPU",13) +
    			FormatUtils.format("%",4)+
    			FormatUtils.format("Max CPU",13) +
    			FormatUtils.format("%",4) +
    			FormatUtils.format("Usg MEM",16) +
    			FormatUtils.format("%",4)+
       			FormatUtils.format("Max MEM",16) +
    			FormatUtils.format("%",4) +
    			FormatUtils.format("ReStart",8)
    			;
		FormatUtils.underline(header);
		logger.info(header);
		FormatUtils.underline(header);
		
		
		return txt.toString();
	}
    
  
    
    public static String printFooterCpu(OcpCluster clusterOcp,BigDecimal tot_pods, BigDecimal tot_usage_cpu, BigDecimal tot_max_cpu, BigDecimal tot_lim_cpu, BigDecimal tot_usage_memory, BigDecimal tot_max_memory, BigDecimal tot_lim_memory) {
    	
    	StringBuilder txt =new StringBuilder();
    	BigDecimal per_usage_cpu = tot_usage_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_max_cpu = tot_max_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));    	 
    	BigDecimal per_lim_memory = tot_lim_memory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_usg_memory_limit = tot_lim_memory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	
    	String footer =
    			FormatUtils.format("TOTAL",50) +
     		    FormatUtils.format("",19) +  
     		    FormatUtils.format("",15) + 
     			FormatUtils.format(""+tot_pods,5) +
     			" | " +    			
     			FormatUtils.format(tot_usage_cpu+"/"+clusterOcp.getCluster_cpu(),13) +     			
     			FormatUtils.format(""+per_usage_cpu.setScale(0, BigDecimal.ROUND_HALF_UP),4)
     			+
     			FormatUtils.format(tot_max_cpu+"/"+clusterOcp.getCluster_cpu(),13) +
     			FormatUtils.format(""+per_max_cpu.setScale(0, BigDecimal.ROUND_HALF_UP),4)
				+     	
				FormatUtils.format(tot_usage_memory.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16) +
				FormatUtils.format(""+per_usg_memory_limit.setScale(0, BigDecimal.ROUND_HALF_UP),4)
				+
				FormatUtils.format(tot_max_memory.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16) +
				FormatUtils.format(""+per_max_cpu.setScale(0, BigDecimal.ROUND_HALF_UP),4)     			
				
				+ FormatUtils.format("",8);
				
    	
    	
    	FormatUtils.underline(footer);
    	logger.info(footer);
    	FormatUtils.underline(footer);
    	
    	txt.append(FormatUtils.underline(footer));
    	txt.append(footer);
    	txt.append(FormatUtils.underline(footer));
    	
    	return txt.toString();
    }
    
      
    public static void printFooterSimulateCharge(OcpCluster clusterOcp,BigDecimal tot_pods,BigDecimal tot_new_pods,BigDecimal tot_pod_simulation,BigDecimal tot_new_req_cpu, BigDecimal tot_new_lim_cpu, BigDecimal tot_new_req_mem, BigDecimal tot_new_lim_mem ) {
    	BigDecimal per_tot_new_req_cpu = tot_new_req_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_tot_new_lim_cpu = tot_new_lim_cpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_tot_new_req_mem = tot_new_req_mem.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_tot_new_lim_mem = tot_new_lim_mem.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	
    	String footer =
    			FormatUtils.format("TOTAL",50) +
    			FormatUtils.format("",30)+
    			FormatUtils.format(""+tot_pods,5) +
    			" | "+
    			FormatUtils.format(""+tot_new_pods,5)+
    			" | "+
    			FormatUtils.format("",3)+
    			" | "+
    			FormatUtils.format(""+tot_pod_simulation ,5) +
    			" | "+
    			FormatUtils.format(tot_new_req_cpu.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_cpu().setScale(0, BigDecimal.ROUND_HALF_UP),16)+
    			FormatUtils.format(""+per_tot_new_req_cpu.setScale(0, BigDecimal.ROUND_HALF_UP),4)+
    			FormatUtils.format(tot_new_lim_cpu.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_cpu().setScale(0, BigDecimal.ROUND_HALF_UP),16) +
    			FormatUtils.format(""+per_tot_new_lim_cpu.setScale(0, BigDecimal.ROUND_HALF_UP),4)+
    			FormatUtils.format(tot_new_req_mem.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16) +
    			FormatUtils.format(""+per_tot_new_req_mem.setScale(0, BigDecimal.ROUND_HALF_UP),4)+
    			FormatUtils.format(tot_new_lim_mem.setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, BigDecimal.ROUND_HALF_UP),16) +
    			FormatUtils.format(""+per_tot_new_lim_mem.setScale(0, BigDecimal.ROUND_HALF_UP),4);
    	FormatUtils.underline(footer);
    	logger.info(footer);
    	FormatUtils.underline(footer);
    }
    
    public String printSimulateChargeNamespace() {		
    	return
  			  FormatUtils.format(this.getName()+"",50)
  			+ FormatUtils.format("cpu["+this.getContainer_cpu_request().setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+this.getContainer_cpu_limit().setScale(0, BigDecimal.ROUND_HALF_UP)+"]",15)
  			+ FormatUtils.format("mem["+this.getContainer_memory_request().setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+this.getContainer_memory_limit().setScale(0, BigDecimal.ROUND_HALF_UP)+"]",15)
  			+ FormatUtils.format(""+this.getNbrPods(),5) 
  			+ " | "
  			+ FormatUtils.format(""+this.getNbrPodPossibleToDeploy() ,5)
  			+ " | "
  			+ FormatUtils.format((this.getNbrPods()+this.getNbrPodPossibleToDeploy()>=3?"V":"X"),3)
  			+ " | "
  			+ FormatUtils.format(""+this.getNbrPodSimulation() ,5)
  			+ " | "
  			+ FormatUtils.format(this.getNewRequest_cpu()+"/"+this.getAllocatable_cpu() ,16)
  			+ FormatUtils.format(""+this.getPer_newRequest_cpu().setScale(0, BigDecimal.ROUND_HALF_UP) ,4)
	    	+ FormatUtils.format(this.getNewLimit_cpu()+"/"+this.getAllocatable_cpu() ,16)
	    	+ FormatUtils.format(""+this.getPer_newLimit_cpu().setScale(0, BigDecimal.ROUND_HALF_UP) ,4)
	    	+ FormatUtils.format(this.getNewRequest_memory()+"/"+this.getAllocatable_memory() ,16)
	    	+ FormatUtils.format(""+this.getPer_newRequest_memory().setScale(0, BigDecimal.ROUND_HALF_UP) ,4)
	    	+ FormatUtils.format(this.getNewLimit_memory()+"/"+this.getAllocatable_memory() ,16)
	    	+ FormatUtils.format(""+this.getPer_newLimit_memory().setScale(0, BigDecimal.ROUND_HALF_UP) ,4);
    		
    }
    
    
    public String printCpuItemNamespace() {
    	
    	return
    			FormatUtils.rightpad(this.getName().toUpperCase(),50)
				+ FormatUtils.format(this.isProtectedByLimits()?"V":"X" ,2)
				+ FormatUtils.format(this.isProtectedByQuotas()?"V":"X" ,2) 
			    + FormatUtils.format("",15)
			    + FormatUtils.format("",15)
				+ FormatUtils.format(""+this.getNbrPods(),5)
				+ " | "			
				+ FormatUtils.format(this.getCurrent_cpu()+"/"+(this.getAllocatable_cpu()!=null?this.getAllocatable_cpu():"-"),13)    			
				+ FormatUtils.format(""+this.getPercentageCurrentCpu().setScale(0, BigDecimal.ROUND_HALF_UP),4)
				
				+ FormatUtils.format(this.getMax_cpu_last2w()+"/"+(this.getAllocatable_cpu()!=null?this.getAllocatable_cpu():"-"),13)    			
				+ FormatUtils.format(""+this.getPercentageMaxCpu().setScale(0, BigDecimal.ROUND_HALF_UP),4) 
			    		
				+ FormatUtils.format(this.getCurrent_memory().setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+(this.getAllocatable_memory()!=null?this.getAllocatable_memory().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),16) 
				+ FormatUtils.format(""+this.getPercentageCurrentMemory().setScale(0, BigDecimal.ROUND_HALF_UP),4) 
				
				+ FormatUtils.format(this.getMax_memory_last2w().setScale(0, BigDecimal.ROUND_HALF_UP)+"/"+(this.getLimits_memory()!=null?this.getLimits_memory().setScale(0, BigDecimal.ROUND_HALF_UP):"-"),16) 
				+ FormatUtils.format(""+this.getPercentageMaxMemory().setScale(0, BigDecimal.ROUND_HALF_UP),4)
				+ FormatUtils.format("",8)
				;  
				
    }
    
    private  void loadMetricNamespace(ServiceKubernetes serviceKubernetes) {

    	for (Pod pod: this.getPodList() ) {
    		OcpPod ocppod= new OcpPod(pod.getMetadata().getName());
    		
	    	try {
	            PodMetrics podMetrics =  serviceKubernetes.getClient().top().pods().metrics(this.getName(), pod.getMetadata().getName());
	            for (ContainerMetrics containeMetric: podMetrics.getContainers()) {
	            	
	            	ocppod.setCurrent_cpu(ocppod.getCurrent_cpu().add(new BigDecimal(containeMetric.getUsage().get("cpu").getAmount())));
	            	ocppod.setCurrent_memory(ocppod.getCurrent_memory().add(new BigDecimal(containeMetric.getUsage().get("memory").getAmount()).divide(new BigDecimal(1024))));
	            }	            	            
	          } catch (KubernetesClientException ex) {
	            if (ex.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
	              //logger.info(" - Pod has not reported any metrics yet");
	            } else {
	              //logger.info(" - Error retrieving Pod metrics");
	            }	          
	        }
	    	
	    	String appName=null;
	    	try {
	    		appName=pod.getMetadata().getLabels().get("app");
	    		ocppod.setAppName(appName);
	    	} catch (Exception e) {
	    		
	    	}    		    
	    	
	    	ocppod.setMax_cpu_last2w(PrometheusService.getMaxConsumptionLast2wOfService(ocpEnvironment.getOcpCluster(),this,ocppod,"cpu_usage"));
	    	ocppod.setMax_memory_last2w(PrometheusService.getMaxConsumptionLast2wOfService(ocpEnvironment.getOcpCluster(),this,ocppod,"memory_usage_bytes"));
	    	
	    	this.getOcpPods().put(ocppod.getName(),ocppod);
	    	
	    	this.setMax_cpu_last2w(this.getMax_cpu_last2w().add(ocppod.getMax_cpu_last2w()));
	    	this.setMax_memory_last2w(this.getMax_memory_last2w().add(ocppod.getMax_memory_last2w()));
	    			
	    	this.setCurrent_cpu(this.getCurrent_cpu().add(ocppod.getCurrent_cpu()));
	    	this.setCurrent_memory(this.getCurrent_memory().add(ocppod.getCurrent_memory()));
    	}
    	
    }
    
    public void analyse(ServiceKubernetes serviceKubernetes) {
    	
            loadMetricNamespace(serviceKubernetes);            
            analysePods(serviceKubernetes);               
            allocateResourceFromQuotas(serviceKubernetes);

    }
    
    public void simulate(ServiceKubernetes serviceKubernetes) {
    	this.setNbrPodSimulation(this.getNbrPods());
        this.setNewRequest_cpu(this.getRequests_cpu());
        this.setNewLimit_cpu(this.getLimits_cpu());
        this.setNewRequest_memory(this.getRequests_memory());
        this.setNewLimit_memory(this.getLimits_memory());

        if (this.isProtectedByQuotas() && this.isProtectedByLimits() && !this.ocpEnvironment.getName().equalsIgnoreCase("others")
        		) {
        	calculateCapacityForNewPodByNamespace(serviceKubernetes);
        	simuleChargeWorkload(serviceKubernetes);
        }   
    }
    
    public void simuleChargeWorkload(ServiceKubernetes serviceKubernetes) {
        
    
    	if (this.ocpEnvironment.getHa().equalsIgnoreCase("degraded")) {      		 		
    		this.ocpEnvironment.setDefaultMaxReplicas(1);
    	} else if ( (this.ocpEnvironment.getHa().equalsIgnoreCase("default") && (this.ocpEnvironment.getName().equalsIgnoreCase("tst") || this.ocpEnvironment.getName().equalsIgnoreCase("int"))) || this.ocpEnvironment.getHa().equalsIgnoreCase("full") ) {
    		this.ocpEnvironment.setDefaultMaxReplicas(2);   	
    	}
    	int defaultMaxReplicas = this.ocpEnvironment.getDefaultMaxReplicas();
              
    	int nbrPodsToDeploy = defaultMaxReplicas-this.getNbrPods();
    	    	
    	// reset new_values
		BigDecimal newRequest_cpu = this.getRequests_cpu();
    	BigDecimal newLimit_cpu = this.getLimits_cpu();
    	BigDecimal newRequest_memory = this.getRequests_memory();
    	BigDecimal newLimit_memory = this.getLimits_memory();
    	
    	searchRequestAndLimitsInDeployment(serviceKubernetes);
    	
    	if (this.getNbrPods()<defaultMaxReplicas && this.getNbrPodPossibleToDeploy()>=nbrPodsToDeploy && nbrPodsToDeploy>0) {
    		        	              
        	for ( int i=1 ; i<=nbrPodsToDeploy ;i++) {
        		newRequest_cpu = newRequest_cpu.add(this.getContainer_cpu_request());
        		newLimit_cpu = newLimit_cpu.add(this.getContainer_cpu_limit());
        		
        		newRequest_memory = newRequest_memory.add(this.getContainer_memory_request());
        		newLimit_memory = newLimit_memory.add(this.getContainer_memory_limit());
        	}
        	this.setNbrPodSimulation(nbrPodsToDeploy+this.getNbrPods());
        	
						
    	} else if (this.ocpEnvironment.getHa().equalsIgnoreCase("degraded") && this.getNbrPods()>defaultMaxReplicas && !this.getName().startsWith("documentum")) {
  			
    		for ( int i=-1 ; i>=nbrPodsToDeploy ;i--) {
        		newRequest_cpu = newRequest_cpu.subtract(this.getContainer_cpu_request());
        		newLimit_cpu = newLimit_cpu.subtract(this.getContainer_cpu_limit());
        		
        		newRequest_memory = newRequest_memory.subtract(this.getContainer_memory_request());
        		newLimit_memory = newLimit_memory.subtract(this.getContainer_memory_limit());

        	}
    		this.setNbrPodSimulation(nbrPodsToDeploy+this.getNbrPods());
    		
    	} else {
    		this.setNbrPodSimulation(this.getNbrPods());
    	
    	}
    	this.setNewRequest_cpu(newRequest_cpu);
    	this.setNewLimit_cpu(newLimit_cpu);
    	this.setNewRequest_memory(newRequest_memory);
    	this.setNewLimit_memory(newLimit_memory);
    	
    	this.setPer_newRequest_cpu(this.getNewRequest_cpu().divide(this.getAllocatable_cpu(), 3, RoundingMode.CEILING).multiply(new BigDecimal(100)));
    	this.setPer_newLimit_cpu(this.getNewLimit_cpu().divide(this.getAllocatable_cpu(), 3, RoundingMode.CEILING).multiply(new BigDecimal(100)));
    	this.setPer_newRequest_memory(this.getNewRequest_memory().divide(this.getAllocatable_memory(), 3, RoundingMode.CEILING).multiply(new BigDecimal(100)));
    	this.setPer_newLimit_memory(this.getNewLimit_memory().divide(this.getAllocatable_memory(), 3, RoundingMode.CEILING).multiply(new BigDecimal(100)));
    }
    
    
    public  void calculateCapacityForNewPodByNamespace(ServiceKubernetes serviceKubernetes) {
    	
    	int nbrPodPossibleToDeploy = 1;
    	boolean tryToDeploy= true;
    	
    	BigDecimal newRequest_cpu = this.getRequests_cpu();
    	BigDecimal newLimit_cpu = this.getLimits_cpu();
    	BigDecimal newRequest_memory = this.getRequests_memory();
    	BigDecimal newLimit_memory = this.getLimits_memory();
    	
    	
    	
    	searchRequestAndLimitsInDeployment(serviceKubernetes);
    	
    	if (this.getContainer_cpu_request()==BigDecimal.ZERO && this.getContainer_cpu_limit()== BigDecimal.ZERO && this.getContainer_memory_request()==BigDecimal.ZERO && this.getContainer_memory_limit()==BigDecimal.ZERO) {
    		allocateRequestAndLimitsFromDefaultLimits(serviceKubernetes);
    	}
		    	while (tryToDeploy) {
		    				    		 
		    		newRequest_cpu = newRequest_cpu.add(this.getContainer_cpu_request());
		    		newLimit_cpu = newLimit_cpu.add(this.getContainer_cpu_limit());
		    		
		    		newRequest_memory = newRequest_memory.add(this.getContainer_memory_request());
		    		newLimit_memory = newLimit_memory.add(this.getContainer_memory_limit());
		    		   	 		    		        	
		    		
		    		if (     newRequest_cpu.compareTo(BigDecimal.ZERO)==0 ||
		    				 newLimit_cpu.compareTo(BigDecimal.ZERO)==0 ||
		    				 newRequest_memory.compareTo(BigDecimal.ZERO)==0 ||
		    				 newLimit_memory.compareTo(BigDecimal.ZERO)==0 ||
		    						 this.getAllocatable_cpu().compareTo(BigDecimal.ZERO)==0 ||
		    								 this.getAllocatable_memory().compareTo(BigDecimal.ZERO)==0 ||
		    										 this.getContainer_cpu_request().compareTo(BigDecimal.ZERO)==0 ||
		    												 this.getContainer_cpu_limit().compareTo(BigDecimal.ZERO)==0 ||
		    														 this.getContainer_memory_request().compareTo(BigDecimal.ZERO)==0 ||
		    																 this.getContainer_memory_limit().compareTo(BigDecimal.ZERO)==0 ||
		    				 newRequest_cpu.compareTo(this.getAllocatable_cpu())==1 ||
		    				 newLimit_cpu.compareTo(this.getAllocatable_cpu())==1 ||
		    				 newRequest_memory.compareTo(this.getAllocatable_memory())==1 ||
		    				 newLimit_memory.compareTo(this.getAllocatable_memory())==1
		    				)
		    		{		  		
		    			nbrPodPossibleToDeploy--;
		    			tryToDeploy= false;
		    			
		    		} else {
		    			this.setNewRequest_cpu(newRequest_cpu);
		    			this.setNewLimit_cpu(newLimit_cpu);
		    			this.setNewRequest_memory(newRequest_memory);
		    			this.setNewLimit_memory(newLimit_memory);
		    			nbrPodPossibleToDeploy++;
		    			
		    		}
    	}
    	this.setNbrPodPossibleToDeploy(nbrPodPossibleToDeploy);
    }
    
 
    
    public void searchRequestAndLimitsInDeployment(ServiceKubernetes serviceKubernetes) {
    	Deployment dep =null;
    	RequestsAndLimits reqlim = null;
		try {
				
	    		DeploymentList deps = serviceKubernetes.getClient().apps().deployments().inNamespace(this.getName()).list();
	    		
	    		if (deps!=null && deps.getItems()!=null) {
	    			dep = deps.getItems().get(0);				    			
	    			Container containerMs =dep.getSpec().getTemplate().getSpec().getContainers().get(0);
	    			ResourceRequirements resources = containerMs.getResources();				    			
	    			reqlim = ServiceRequestsAndLimits.extractResourceRequirementsToRequestAndLimits(resources);
	    			if (reqlim!=null && reqlim.getReq_cpu()!=null && reqlim.getLim_cpu()!=null && reqlim.getReq_memory()!=null && reqlim.getLim_memory()!=null) {
	    				this.setContainer_cpu_request(reqlim.getReq_cpu());
	    				this.setContainer_cpu_limit(reqlim.getLim_cpu());
	    				this.setContainer_memory_request(reqlim.getReq_memory());
	    				this.setContainer_memory_limit(reqlim.getLim_memory());
	    			} else {
	    				logger.info("SETUP REQ_LIM FROM DEFAULT LIMIT FOR "+this.getName());
	    				allocateRequestAndLimitsFromDefaultLimits(serviceKubernetes);
	    				logger.info(""+this.getContainer_cpu_request());
	    				logger.info(""+this.getContainer_cpu_limit());
	    				logger.info(""+this.getContainer_memory_request());
	    				logger.info(""+this.getContainer_memory_limit());
	    			}
	    		} 
	    		
		} catch (Exception e) {
			
		}
		
    }
    
    private void analysePods(ServiceKubernetes serviceKubernetes) {

    	if (this.getNbrPods()>0) {
	    	for (Pod pod: this.getPodList()) {
	    		if (pod.getMetadata().getName().contains("redis")) {
	    			this.containsRedis=true;
	    			
	    		}
	    		OcpPod ocppod = this.getOcpPods().get(pod.getMetadata().getName());
	    		String reasonRestart = null;
	    		try {
	    			reasonRestart = pod.getStatus().getContainerStatuses().get(0).getLastState().getTerminated().getReason();
	    		} catch(Exception e) {
	    			
	    		}
	    		ocppod.setTerminatedReason(reasonRestart);
	    		/*
	    		if (pod.getStatus().getPhase().equalsIgnoreCase("Failed") && pod.getStatus().getReason()!=null) {
    				System.out.println("Container Terminated by : "+pod.getStatus().getReason());
    				ocppod.setTerminatedReason(pod.getStatus().getReason());
    			}
    			*/
	    		List<ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
	    		for (ContainerStatus contstat : containerStatuses) {
	    			
	    			
	    			if (contstat.getRestartCount()>ocppod.getCountRestart()) {
	    				ocppod.setCountRestart(contstat.getRestartCount());
	    			}
	    			
	    		}
	            for (io.fabric8.kubernetes.api.model.Container container: pod.getSpec().getContainers()) {
	     
	            	RequestsAndLimits reqLim = ServiceRequestsAndLimits.extractResourceRequirementsToRequestAndLimits(container.getResources());
	            	if (reqLim!=null) {
	            		this.setLimits_cpu(this.getLimits_cpu().add(reqLim.getLim_cpu())); 
	            		ocppod.setLimits_cpu(reqLim.getLim_cpu());
	            		this.setLimits_memory(this.getLimits_memory().add(reqLim.getLim_memory()));
	            		ocppod.setLimits_memory(reqLim.getLim_memory());
	            		this.setRequests_cpu(this.getRequests_cpu().add(reqLim.getReq_cpu()));
	            		ocppod.setRequests_cpu(reqLim.getReq_cpu());
	            		this.setRequests_memory(this.getRequests_memory().add(reqLim.getReq_memory()));
	            		ocppod.setRequests_memory(reqLim.getReq_memory());
	            	}

	            }     
  
	    	}
    	} 
    	    	
    	allocateRequestAndLimitsFromDefaultLimits(serviceKubernetes);

    }
    
    private void allocateRequestAndLimitsFromDefaultLimits(ServiceKubernetes serviceKubernetes) {
    	// setup container limits (namespace level) with default limits
    	
    			LimitRangeList limitRangeList = serviceKubernetes.getClient().limitRanges().inNamespace(this.getName()).list();
    			for (LimitRange limitRange: limitRangeList.getItems()) {
    				
    				for (LimitRangeItem limitRangeItem :limitRange.getSpec().getLimits()) {
    											
    					RequestsAndLimits reqLim = new RequestsAndLimits();
    					if (limitRangeItem.getDefaultRequest()!=null) {
    						
    						ServiceRequestsAndLimits.extractMapQuantityRequestsToRequestAndLimits(limitRangeItem.getDefaultRequest(),reqLim);
    					}
    					if (limitRangeItem.getDefault()!=null) {
    						ServiceRequestsAndLimits.extractMapQuantityLimitsToRequestAndLimits(limitRangeItem.getDefault(),reqLim);
    						
    					}
    			
    					this.setContainer_cpu_request(reqLim.getReq_cpu());
    					this.setContainer_cpu_limit(reqLim.getLim_cpu());
    					this.setContainer_memory_request(reqLim.getReq_memory());
    					this.setContainer_memory_limit(reqLim.getLim_memory());
    					
    				}
    			}    	
    }
    
    private void allocateResourceFromQuotas(ServiceKubernetes serviceKubernetes) {
    	
    	ResourceQuotaList resourceQuotaList = serviceKubernetes.getClient().resourceQuotas().inNamespace(this.getName()).list();
        if (resourceQuotaList.getItems()!=null && !resourceQuotaList.getItems().isEmpty()) {
	        for (ResourceQuota quota: resourceQuotaList.getItems()) {
	   
	        	RequestsAndLimits reqLim = new RequestsAndLimits();
	        	try {	        		
	        		
		        	if (quota.getSpec()!=null && quota.getSpec().getHard()!=null) {
		        		ServiceRequestsAndLimits.extractMapQuantityLimitsToRequestAndLimits(quota.getSpec().getHard(),reqLim);
		        		ServiceRequestsAndLimits.extractMapQuantityRequestsToRequestAndLimits(quota.getSpec().getHard(),reqLim);
		        		
		        	}
	        	} catch (Exception e) {
	        		
	        	}
	        		        
	        	this.setAllocatable_cpu(reqLim.getLim_cpu());
	        	this.setAllocatable_memory(reqLim.getLim_memory());	 
	        	this.setQuota_request_cpu(reqLim.getReq_cpu());	        	
	        	this.setQuota_request_memory(reqLim.getReq_memory());
	        	
	        	this.ocpEnvironment.setQuotaCpuLimit(this.ocpEnvironment.getQuotaCpuLimit().add(reqLim.getLim_cpu()));
	        	this.ocpEnvironment.setQuotaMemoryLimit(this.ocpEnvironment.getQuotaMemoryLimit().add(reqLim.getLim_memory()));
	        	this.ocpEnvironment.setQuotaCpuRequest(this.ocpEnvironment.getQuotaCpuRequest().add(reqLim.getReq_cpu()));
	        	this.ocpEnvironment.setQuotaMemoryRequest(this.ocpEnvironment.getQuotaMemoryRequest().add(reqLim.getReq_memory()));
	        	
	        	this.ocpEnvironment.getOcpCluster().setTotQuotaCpuRequest(this.ocpEnvironment.getOcpCluster().getTotQuotaCpuRequest().add(reqLim.getReq_cpu()));
	        	this.ocpEnvironment.getOcpCluster().setTotQuotaCpuLimit(this.ocpEnvironment.getOcpCluster().getTotQuotaCpuLimit().add(reqLim.getLim_cpu()));
	        	this.ocpEnvironment.getOcpCluster().setTotQuotaMemoryRequest(this.ocpEnvironment.getOcpCluster().getTotQuotaMemoryRequest().add(reqLim.getReq_memory()));
	        	this.ocpEnvironment.getOcpCluster().setTotQuotaMemoryLimit(this.ocpEnvironment.getOcpCluster().getTotQuotaMemoryLimit().add(reqLim.getLim_memory()));
	        }
	        this.setProtectedByQuotas(true);
        } else {
        	this.setProtectedByQuotas(false);
        }
        
   }

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		OcpNamespace.logger = logger;
	}

	public OcpEnvironment getOcpEnvironment() {
		return ocpEnvironment;
	}

	public void setOcpEnvironment(OcpEnvironment ocpEnvironment) {
		this.ocpEnvironment = ocpEnvironment;
	}

	public boolean isProtectedByLimits() {
		return protectedByLimits;
	}

	public void setProtectedByLimits(boolean protectedByLimits) {
		this.protectedByLimits = protectedByLimits;
	}

	public boolean isProtectedByQuotas() {
		return protectedByQuotas;
	}

	public void setProtectedByQuotas(boolean protectedByQuotas) {
		this.protectedByQuotas = protectedByQuotas;
	}

	public boolean isRollingUpdate() {
		return this.getNbrPods()+this.getNbrPodPossibleToDeploy()>=3;
	}

	public void setRollingUpdate(boolean rollingUpdate) {
		this.rollingUpdate = rollingUpdate;
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

	public BigDecimal getNewRequest_cpu() {
		return newRequest_cpu;
	}

	public void setNewRequest_cpu(BigDecimal newRequest_cpu) {
		this.newRequest_cpu = newRequest_cpu;
	}

	public BigDecimal getNewLimit_cpu() {
		return newLimit_cpu;
	}

	public void setNewLimit_cpu(BigDecimal newLimit_cpu) {
		this.newLimit_cpu = newLimit_cpu;
	}

	public BigDecimal getNewRequest_memory() {
		return newRequest_memory;
	}

	public void setNewRequest_memory(BigDecimal newRequest_memory) {
		this.newRequest_memory = newRequest_memory;
	}

	public BigDecimal getNewLimit_memory() {
		return newLimit_memory;
	}

	public void setNewLimit_memory(BigDecimal newLimit_memory) {
		this.newLimit_memory = newLimit_memory;
	}

	public BigDecimal getPer_newRequest_cpu() {
		return per_newRequest_cpu;
	}

	public void setPer_newRequest_cpu(BigDecimal per_newRequest_cpu) {
		this.per_newRequest_cpu = per_newRequest_cpu;
	}

	public BigDecimal getPer_newLimit_cpu() {
		return per_newLimit_cpu;
	}

	public void setPer_newLimit_cpu(BigDecimal per_newLimit_cpu) {
		this.per_newLimit_cpu = per_newLimit_cpu;
	}

	public BigDecimal getPer_newRequest_memory() {
		return per_newRequest_memory;
	}

	public void setPer_newRequest_memory(BigDecimal per_newRequest_memory) {
		this.per_newRequest_memory = per_newRequest_memory;
	}

	public BigDecimal getPer_newLimit_memory() {
		return per_newLimit_memory;
	}

	public void setPer_newLimit_memory(BigDecimal per_newLimit_memory) {
		this.per_newLimit_memory = per_newLimit_memory;
	}

	public int getNbrPodPossibleToDeploy() {
		return nbrPodPossibleToDeploy;
	}

	public void setNbrPodPossibleToDeploy(int nbrPodPossibleToDeploy) {
		this.nbrPodPossibleToDeploy = nbrPodPossibleToDeploy;
	}

	public int getNbrPodSimulation() {
		return nbrPodSimulation;
	}

	public void setNbrPodSimulation(int nbrPodSimulation) {
		this.nbrPodSimulation = nbrPodSimulation;
	}

	public String getLimitsRecommendations() {
		return limitsRecommendations;
	}

	public void setLimitsRecommendations(String limitsRecommendations) {
		this.limitsRecommendations = limitsRecommendations;
	}

	public boolean isContainsRedis() {
		return containsRedis;
	}

	public void setContainsRedis(boolean containsRedis) {
		this.containsRedis = containsRedis;
	}

	public String getTeam() {
		return team.toUpperCase();
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getValueChain() {
		return valueChain;
	}

	public void setValueChain(String valueChain) {
		this.valueChain = valueChain;
	}

	public String getTeamEmail() {
		return teamEmail;
	}

	public void setTeamEmail(String teamEmail) {
		this.teamEmail = teamEmail;
	}
    
	
    
}
