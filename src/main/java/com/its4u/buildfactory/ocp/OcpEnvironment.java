package com.its4u.buildfactory.ocp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.its4u.buildfactory.services.ServiceCluster;
import com.its4u.buildfactory.services.ServiceKubernetes;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;


public class OcpEnvironment {
	
	private OcpCluster ocpCluster;

	private String name;
	
	private NamespaceList namespaceList;
	
	private List<OcpNamespace> namespaces;
	
	private List<OcpResource> namespacesUnused;
	
	private int additionalPodWithSimulation;
	
	private int currentPods;
	
	private int newPodsWithSimulation;
	
	private BigDecimal usageCpu=BigDecimal.ZERO;
	
	private BigDecimal usageMemory=BigDecimal.ZERO;
	
	private BigDecimal requestCpu =BigDecimal.ZERO;
	
	private BigDecimal requestMemory =BigDecimal.ZERO;
	
	private BigDecimal limitCpu =BigDecimal.ZERO;
	
	private BigDecimal limitMemory =BigDecimal.ZERO;
	
	private BigDecimal currentRequestCpu =BigDecimal.ZERO;
	
	private BigDecimal currentLimitCpu =BigDecimal.ZERO;
	
	private BigDecimal currentRequestMemory =BigDecimal.ZERO;
	
	private BigDecimal curentLimitMemory =BigDecimal.ZERO;
	
	private BigDecimal newRequestCpuWithSimulation =BigDecimal.ZERO;
    
	private BigDecimal newLimitCpuWithSimulation=BigDecimal.ZERO;
    
	private BigDecimal newRequestMemoryWithSimulation=BigDecimal.ZERO;
    
	private BigDecimal newLimitMemoryWithSimulation=BigDecimal.ZERO;
    
	private BigDecimal additionalRequestCpuWithSimulation=BigDecimal.ZERO;
    
	private BigDecimal additionalLimitCpuWithSimulation=BigDecimal.ZERO;
    
	private BigDecimal additionalRequestMemoryWithSimulation=BigDecimal.ZERO;
    
	private BigDecimal additionalLimitMemoryWithSimulation=BigDecimal.ZERO;
    
	private BigDecimal quotaMemoryLimit=BigDecimal.ZERO;
    
	private BigDecimal quotaCpuLimit=BigDecimal.ZERO;
    
	private BigDecimal quotaMemoryRequest=BigDecimal.ZERO;
    
	private BigDecimal quotaCpuRequest=BigDecimal.ZERO;
		
	private BigDecimal per_lim_memory = BigDecimal.ZERO;
	
	private BigDecimal per_req_memory = BigDecimal.ZERO;
	
	private BigDecimal per_req_cpu = BigDecimal.ZERO;
	
	private BigDecimal per_lim_cpu= BigDecimal.ZERO;
	
	private BigDecimal new_per_lim_memory = BigDecimal.ZERO;
	
	private BigDecimal new_per_req_memory = BigDecimal.ZERO;
	
	private BigDecimal new_per_req_cpu = BigDecimal.ZERO;
	
	private BigDecimal new_per_lim_cpu = BigDecimal.ZERO;
	
	private BigDecimal per_quota_lim_memory = BigDecimal.ZERO;
	
	private BigDecimal per_quota_req_memory = BigDecimal.ZERO;
	
	private BigDecimal per_quota_req_cpu = BigDecimal.ZERO;
	
	private BigDecimal per_quota_lim_cpu = BigDecimal.ZERO;
	
	private int env_size;
    
	private int defaultMaxReplicas;
    
	private String ha;
    
    
    public static String printHeaderHtml(String title) {
		StringBuilder txt = new StringBuilder();
	
		txt.append("<h1>"+title+" By Environment :</h1>");
		txt.append("<table>");
		txt.append("<tr>");
		txt.append("<th rowspan=2 colspan=3></th>");
		txt.append("<th colspan=8 style=\"text-align: center;\">Resources consumption</th>");
		txt.append("<th colspan=8 style=\"text-align: center;\">Quotas</th>");
		txt.append("</tr>");
		txt.append("<tr>");
		txt.append("<th colspan=4 style=\"text-align: center;\">CPU</th>");
		txt.append("<th colspan=4 style=\"text-align: center;\">Memory</th>");
		txt.append("<th colspan=4 style=\"text-align: center;\">CPU</th>");
		txt.append("<th colspan=4 style=\"text-align: center;\">Memory</th>");
		txt.append("</tr>");
		txt.append("<tr>");
		txt.append("<th >Environment</th>");
		txt.append("<th>Projects</th>");
		txt.append("<th>Pods</th>");
		
		txt.append("<th>Requests</th>");
		txt.append("<th>%</th>");
		txt.append("<th>Limits</th>");
		txt.append("<th>%</th>");
		
		txt.append("<th>Requests</th>");
		txt.append("<th>%</th>");
		txt.append("<th>Limits</th>");
		txt.append("<th>%</th>");
		txt.append("<th>Requests</th>");
		txt.append("<th>%</th>");
		txt.append("<th>Limits</th>");
		txt.append("<th>%</th>");
		txt.append("<th>Requests</th>");
		txt.append("<th>%</th>");
		txt.append("<th>Limits</th>");
		txt.append("<th>%</th>");
		txt.append("</tr>");
		
		return txt.toString();
	}

    public String printItemHtml(OcpCluster clusterOcp) {
    	StringBuilder txt = new StringBuilder();
  
    	txt.append("<tr>");
    	txt.append("<td>"+name+"</td>");		
		txt.append("<td>"+namespaces.size()+"</td>");
		txt.append("<td>"+currentPods+"</td>");
		
		txt.append("<td>"+requestCpu+"</td>");
		txt.append("<td>"+getPer_req_cpu()+"</td>");
		txt.append("<td>"+limitCpu+"</td>");
		txt.append("<td>"+getPer_lim_cpu()+"</td>");
		
		txt.append("<td>"+requestMemory+"</td>");
		txt.append("<td>"+getPer_req_memory()+"</td>");
		txt.append("<td>"+limitMemory+"</td>");
		txt.append("<td>"+getPer_lim_memory()+"</td>");
		
		
		appendQuota(txt, clusterOcp);
		
    	return txt.toString();
    }
    
    public String printFullChargeItemHtml(OcpCluster clusterOcp) {
    	StringBuilder txt = new StringBuilder();
    	txt.append("<tr>");
    	txt.append("<td>"+name+"</td>");		
		txt.append("<td>"+namespaces.size()+"</td>");
		txt.append("<td>"+newPodsWithSimulation+"</td>");
		
		txt.append("<td>"+newRequestCpuWithSimulation+"</td>");
		txt.append("<td>"+newRequestCpuWithSimulation.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+newLimitCpuWithSimulation+"</td>");
		txt.append("<td>"+newLimitCpuWithSimulation.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)+"</td>");
		
		txt.append("<td>"+newRequestMemoryWithSimulation+"</td>");
		txt.append("<td>"+newRequestMemoryWithSimulation.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+newLimitMemoryWithSimulation+"</td>");
		txt.append("<td>"+newLimitMemoryWithSimulation.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)+"</td>");
		
		appendQuota(txt, clusterOcp);
			
    	return txt.toString();
    }
    
    public  void appendQuota(StringBuilder txt,OcpCluster clusterOcp) {
    	txt.append("<td>"+quotaCpuRequest+"</td>");
		txt.append("<td>"+quotaCpuRequest.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+quotaCpuLimit+"</td>");
		txt.append("<td>"+quotaCpuLimit.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+quotaMemoryRequest+"</td>");
		txt.append("<td>"+quotaMemoryRequest.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("<td>"+quotaMemoryLimit+"</td>");
		txt.append("<td>"+quotaMemoryLimit.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)+"</td>");
		txt.append("</tr>");
    }
    
    public static String printFooterHtml() {
    	StringBuilder txt = new StringBuilder();
    	txt.append("</table>").append("</body>").append("</html>");
    	return txt.toString();
    }

	public OcpEnvironment(OcpCluster ocpcluster,String name, List<OcpNamespace> namespaces, List<OcpResource> namespacesUnused, int defaultMaxReplicas,String ha) {
		super();
		this.ocpCluster = ocpcluster;
		this.name = name;
		this.namespaces = namespaces;
		this.namespacesUnused = namespacesUnused;
		this.quotaMemoryLimit = BigDecimal.ZERO;
		this.quotaCpuLimit = BigDecimal.ZERO;
		this.quotaMemoryRequest = BigDecimal.ZERO;
		this.quotaCpuRequest = BigDecimal.ZERO;
		this.defaultMaxReplicas = defaultMaxReplicas;
		this.ha = ha;
	}
	
	public String printAdditionalValues() {
		return
    			format("Additional Pods in "+ this.ha.toUpperCase() +" charge "+this.name.toUpperCase()+" ["+this.defaultMaxReplicas + " Replicas] " ,50) +
    		  
     			format(""+this.additionalPodWithSimulation,5) +
     			" | " +    			
     			format("",13) +     			
     			format("",4)
     			+
     			format("+ "+additionalRequestCpuWithSimulation,13) +
     			format("",4)
     			+     			
     			format("+ "+additionalLimitCpuWithSimulation,13) +
     			format("",4)
     			+
     			format("",16) +
     			format("",4)
     			+     			
     			format("+ "+additionalRequestMemoryWithSimulation,16) +
     			format("",4)
     			+     			
     			format("+ "+additionalLimitMemoryWithSimulation,16) +
     			format("",4) ; 
	}
	


	
	public static void printFooter(OcpCluster clusterOcp,int newTotPods,BigDecimal newTotRequestCpu,BigDecimal newTotLimitCpu, BigDecimal newTotRequestMemory,BigDecimal newTotLimitMemory) {
		
		    	    	
    	BigDecimal per_lim_memory = newTotLimitMemory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	BigDecimal per_req_memory = newTotRequestMemory.divide(clusterOcp.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	
    	BigDecimal per_req_cpu = newTotRequestCpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));    	
    	BigDecimal per_lim_cpu = newTotLimitCpu.divide(clusterOcp.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    	
		String footer =
				format("TOTAL FULL WORKLOAD ",50) +
	    		  
     			format(""+newTotPods,5) +
     			" | " +    			
     			format("",13) +     			
     			format("",4)
     			+
     			format(newTotRequestCpu+"/"+clusterOcp.getCluster_cpu(),13) +
     			format(""+per_req_cpu.setScale(0, RoundingMode.HALF_UP),4)
     			+     			
     			format(newTotLimitCpu+"/"+clusterOcp.getCluster_cpu(),13) +
     			format(""+per_lim_cpu.setScale(0, RoundingMode.HALF_UP),4)
     			+
     			format("",16) +
     			format("",4)
     			+     			
     			format(newTotRequestMemory.setScale(0, RoundingMode.HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, RoundingMode.HALF_UP),16) +
     			format(""+per_req_memory.setScale(0, RoundingMode.HALF_UP),4)
     			+     			
     			format(newTotLimitMemory.setScale(0, RoundingMode.HALF_UP)+"/"+clusterOcp.getCluster_memory().setScale(0, RoundingMode.HALF_UP),16) +
     			format(""+per_lim_memory.setScale(0, RoundingMode.HALF_UP),4) ;     			
     			
		underline(footer);
		System.out.println(footer);
		underline(footer);
    	
	}
	
	private static String format(String str,int length) {
    	return String.format("%1$"+length+ "s", str);
    	
    }
	
	private static void underline(String str) {
		 String und="";
		 for (int i=0;i<str.length();i++) {
			 und=und+"-";
		 }
		 System.out.println(und);
	}
	
	public  void loadNamespaces(ServiceKubernetes serviceKubernetes,String env) {
    	if (env.equalsIgnoreCase("others")) {
    		loadOthersNamespaces(serviceKubernetes);
    	} else {
    		this.namespaceList = serviceKubernetes.getClient().namespaces().withLabel(this.getOcpCluster().getNamespace_env_label(), name).list();
    		loadDetailsNamespace(serviceKubernetes,this.namespaceList.getItems());
    	}
                
    }
	
	private void loadDetailsNamespace(ServiceKubernetes serviceKubernetes,List<Namespace> namespaces) {
		 int nbrPod= 0;
	        for (Namespace nsp: namespaces) {
	            String name= nsp.getMetadata().getName();
	            OcpNamespace namespaceOcp = new OcpNamespace(this,name);
	            
	            
	            if (nsp.getMetadata()!=null && nsp.getMetadata().getLabels()!=null 
	            		&& nsp.getMetadata().getLabels().get("its4u.com/team")!=null
	            		&& nsp.getMetadata().getLabels().get("its4u.com/value-chain")!=null) {
	            	String teamEmail=null;
	            	if (nsp.getMetadata().getAnnotations()!=null) {
	            		teamEmail= nsp.getMetadata().getAnnotations().get("its4u.com/team-mail");
	            	}
	            	
	            	String team = nsp.getMetadata().getLabels().get("its4u.com/team").toLowerCase();
	            	String vc = nsp.getMetadata().getLabels().get("its4u.com/value-chain").toLowerCase();
	            	
	            	namespaceOcp.setTeam(team);
	            	namespaceOcp.setTeamEmail(teamEmail);
	            	
	            	if (ocpCluster.getBilTeams().get(team)!=null) {
	            		ocpCluster.getBilTeams().get(team).getNamespaces().add(namespaceOcp);
	            	} else {
	            		ProjectTeam bilTeam = new ProjectTeam(team,new ArrayList<>());
	            		bilTeam.getNamespaces().add(namespaceOcp);
	            		ocpCluster.getBilTeams().put(team, bilTeam);
	            	}
	            	if (ocpCluster.getBilValueChains().get(vc)!=null) {
	            		ocpCluster.getBilValueChains().get(vc).getNamespaces().add(namespaceOcp);
	            	} else {
	            		ProjectValueChain bilValueChain= new ProjectValueChain(vc,new ArrayList<>());
	            		bilValueChain.getNamespaces().add(namespaceOcp);
	            		ocpCluster.getBilValueChains().put(vc, bilValueChain);
	            	}
	            }
	            
	            
	            // we take pod in consideration only if the pods is Running
	            PodList podList = serviceKubernetes.getClient().pods().inNamespace(name).withField("status.phase", "Running").list();
	            
	            PodList podListFailed = serviceKubernetes.getClient().pods().inNamespace(name).withField("status.phase", "Failed").list();
	            
	            List<Pod> pods = new ArrayList<>();
	            
	            // we take pod in consideration only if the host is a worker
	            for (Pod pod:podList.getItems()) {
	            	if(pod.getSpec().getNodeName().startsWith(ocpCluster.getWorkerPrefix())) {
	            		pods.add(pod);
	            		nbrPod++;
	            	}
	            }
	            System.out.println("Pods Running : "+pods.size());
	            List<Pod> podsFailed = new ArrayList<>();
	            
	            // we take pod in consideration only if the host is a worker
	            for (Pod pod:podListFailed.getItems()) {
	            	if(pod.getSpec().getNodeName().startsWith(ocpCluster.getWorkerPrefix())) {
	            		podsFailed.add(pod);	            		
	            	}
	            }	
	                                   
	            namespaceOcp.setPodList(pods);
	            namespaceOcp.setPodListFailed(podsFailed);
	            namespaceOcp.setNbrPods(pods.size());              
	            namespaceOcp.setProtectedByLimits(ServiceCluster.checkIfNamespaceProtectedByLimits(serviceKubernetes.getClient(),this.getOcpCluster(),namespaceOcp));
	            namespaceOcp.analyse(serviceKubernetes);	      
	            namespaceOcp.simulate(serviceKubernetes);
	            this.getNamespaces().add(namespaceOcp);
	            this.ocpCluster.getBilNamespaces().put(name,name);
	        }
	}
	
	public void relaunchSimulation(ServiceKubernetes serviceKubernetes,String ha) {
		for (OcpNamespace namespaceOcp: namespaces) {			
			if (namespaceOcp.isProtectedByQuotas() && namespaceOcp.isProtectedByLimits() && !this.getName().equalsIgnoreCase("others")) {
				namespaceOcp.simuleChargeWorkload(serviceKubernetes);
			}			
		}
	}
	
	private  void loadOthersNamespaces(ServiceKubernetes serviceKubernetes) {
    	List<Namespace> othersNamespace = new ArrayList<>();
    	this.namespaceList = serviceKubernetes.getClient().namespaces().list();
    	for (Namespace namespace : namespaceList.getItems()) {
    		if (this.ocpCluster.getBilNamespaces().get(namespace.getMetadata().getName())==null) {
    			othersNamespace.add(namespace);
    		}
    	}
    	loadDetailsNamespace(serviceKubernetes,othersNamespace);
    }

	public OcpCluster getOcpCluster() {
		return ocpCluster;
	}

	public void setOcpCluster(OcpCluster ocpCluster) {
		this.ocpCluster = ocpCluster;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NamespaceList getNamespaceList() {
		return namespaceList;
	}

	public void setNamespaceList(NamespaceList namespaceList) {
		this.namespaceList = namespaceList;
	}

	public List<OcpNamespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<OcpNamespace> namespaces) {
		this.namespaces = namespaces;
	}

	public List<OcpResource> getNamespacesUnused() {
		return namespacesUnused;
	}

	public void setNamespacesUnused(List<OcpResource> namespacesUnused) {
		this.namespacesUnused = namespacesUnused;
	}

	public int getAdditionalPodWithSimulation() {
		return additionalPodWithSimulation;
	}

	public void setAdditionalPodWithSimulation(int additionalPodWithSimulation) {
		this.additionalPodWithSimulation = additionalPodWithSimulation;
	}

	public int getCurrentPods() {
		return currentPods;
	}

	public void setCurrentPods(int currentPods) {
		this.currentPods = currentPods;
	}

	public int getNewPodsWithSimulation() {
		return newPodsWithSimulation;
	}

	public void setNewPodsWithSimulation(int newPodsWithSimulation) {
		this.newPodsWithSimulation = newPodsWithSimulation;
	}

	public BigDecimal getUsageCpu() {
		return usageCpu;
	}

	public void setUsageCpu(BigDecimal usageCpu) {
		this.usageCpu = usageCpu;
	}

	public BigDecimal getUsageMemory() {
		return usageMemory;
	}

	public void setUsageMemory(BigDecimal usageMemory) {
		this.usageMemory = usageMemory;
	}

	public BigDecimal getRequestCpu() {
		return requestCpu;
	}

	public void setRequestCpu(BigDecimal requestCpu) {
		this.requestCpu = requestCpu;
	}

	public BigDecimal getRequestMemory() {
		return requestMemory;
	}

	public void setRequestMemory(BigDecimal requestMemory) {
		this.requestMemory = requestMemory;
	}

	public BigDecimal getLimitCpu() {
		return limitCpu;
	}

	public void setLimitCpu(BigDecimal limitCpu) {
		this.limitCpu = limitCpu;
	}

	public BigDecimal getLimitMemory() {
		return limitMemory;
	}

	public void setLimitMemory(BigDecimal limitMemory) {
		this.limitMemory = limitMemory;
	}

	public BigDecimal getCurrentRequestCpu() {
		return currentRequestCpu;
	}

	public void setCurrentRequestCpu(BigDecimal currentRequestCpu) {
		this.currentRequestCpu = currentRequestCpu;
	}

	public BigDecimal getCurrentLimitCpu() {
		return currentLimitCpu;
	}

	public void setCurrentLimitCpu(BigDecimal currentLimitCpu) {
		this.currentLimitCpu = currentLimitCpu;
	}

	public BigDecimal getCurrentRequestMemory() {
		return currentRequestMemory;
	}

	public void setCurrentRequestMemory(BigDecimal currentRequestMemory) {
		this.currentRequestMemory = currentRequestMemory;
	}

	public BigDecimal getCurentLimitMemory() {
		return curentLimitMemory;
	}

	public void setCurentLimitMemory(BigDecimal curentLimitMemory) {
		this.curentLimitMemory = curentLimitMemory;
	}

	public BigDecimal getNewRequestCpuWithSimulation() {
		return newRequestCpuWithSimulation;
	}

	public void setNewRequestCpuWithSimulation(BigDecimal newRequestCpuWithSimulation) {
		this.newRequestCpuWithSimulation = newRequestCpuWithSimulation;
	}

	public BigDecimal getNewLimitCpuWithSimulation() {
		return newLimitCpuWithSimulation;
	}

	public void setNewLimitCpuWithSimulation(BigDecimal newLimitCpuWithSimulation) {
		this.newLimitCpuWithSimulation = newLimitCpuWithSimulation;
	}

	public BigDecimal getNewRequestMemoryWithSimulation() {
		return newRequestMemoryWithSimulation;
	}

	public void setNewRequestMemoryWithSimulation(BigDecimal newRequestMemoryWithSimulation) {
		this.newRequestMemoryWithSimulation = newRequestMemoryWithSimulation;
	}

	public BigDecimal getNewLimitMemoryWithSimulation() {
		return newLimitMemoryWithSimulation;
	}

	public void setNewLimitMemoryWithSimulation(BigDecimal newLimitMemoryWithSimulation) {
		this.newLimitMemoryWithSimulation = newLimitMemoryWithSimulation;
	}

	public BigDecimal getAdditionalRequestCpuWithSimulation() {
		return additionalRequestCpuWithSimulation;
	}

	public void setAdditionalRequestCpuWithSimulation(BigDecimal additionalRequestCpuWithSimulation) {
		this.additionalRequestCpuWithSimulation = additionalRequestCpuWithSimulation;
	}

	public BigDecimal getAdditionalLimitCpuWithSimulation() {
		return additionalLimitCpuWithSimulation;
	}

	public void setAdditionalLimitCpuWithSimulation(BigDecimal additionalLimitCpuWithSimulation) {
		this.additionalLimitCpuWithSimulation = additionalLimitCpuWithSimulation;
	}

	public BigDecimal getAdditionalRequestMemoryWithSimulation() {
		return additionalRequestMemoryWithSimulation;
	}

	public void setAdditionalRequestMemoryWithSimulation(BigDecimal additionalRequestMemoryWithSimulation) {
		this.additionalRequestMemoryWithSimulation = additionalRequestMemoryWithSimulation;
	}

	public BigDecimal getAdditionalLimitMemoryWithSimulation() {
		return additionalLimitMemoryWithSimulation;
	}

	public void setAdditionalLimitMemoryWithSimulation(BigDecimal additionalLimitMemoryWithSimulation) {
		this.additionalLimitMemoryWithSimulation = additionalLimitMemoryWithSimulation;
	}

	public BigDecimal getQuotaMemoryLimit() {
		return quotaMemoryLimit;
	}

	public void setQuotaMemoryLimit(BigDecimal quotaMemoryLimit) {
		this.quotaMemoryLimit = quotaMemoryLimit;
	}

	public BigDecimal getQuotaCpuLimit() {
		return quotaCpuLimit;
	}

	public void setQuotaCpuLimit(BigDecimal quotaCpuLimit) {
		this.quotaCpuLimit = quotaCpuLimit;
	}

	public BigDecimal getQuotaMemoryRequest() {
		return quotaMemoryRequest;
	}

	public void setQuotaMemoryRequest(BigDecimal quotaMemoryRequest) {
		this.quotaMemoryRequest = quotaMemoryRequest;
	}

	public BigDecimal getQuotaCpuRequest() {
		return quotaCpuRequest;
	}

	public void setQuotaCpuRequest(BigDecimal quotaCpuRequest) {
		this.quotaCpuRequest = quotaCpuRequest;
	}

	public BigDecimal getPer_lim_memory() {
		return limitMemory.divide(this.ocpCluster.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setPer_lim_memory(BigDecimal per_lim_memory) {		
		this.per_lim_memory = per_lim_memory;
	}

	public BigDecimal getPer_req_memory() {
		return requestMemory.divide(this.ocpCluster.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);		
	}

	public void setPer_req_memory(BigDecimal per_req_memory) {
		this.per_req_memory = per_req_memory;
	}

	public BigDecimal getPer_req_cpu() {
		return requestCpu.divide(this.ocpCluster.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);    	
	}

	public void setPer_req_cpu(BigDecimal per_req_cpu) {
		this.per_req_cpu = per_req_cpu;
	}

	public BigDecimal getPer_lim_cpu() {
		return limitCpu.divide(this.ocpCluster.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setPer_lim_cpu(BigDecimal per_lim_cpu) {
		this.per_lim_cpu = per_lim_cpu;
	}

	public int getDefaultMaxReplicas() {
		return defaultMaxReplicas;
	}

	public void setDefaultMaxReplicas(int defaultMaxReplicas) {
		this.defaultMaxReplicas = defaultMaxReplicas;
	}

	public String getHa() {
		return ha;
	}

	public void setHa(String ha) {
		this.ha = ha;
	}

	public int getEnv_size() {
		return namespaces.size();
	}

	public void setEnv_size(int env_size) {
		this.env_size = env_size;
	}

	public BigDecimal getPer_quota_lim_memory() {
		return quotaMemoryLimit.divide(ocpCluster.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setPer_quota_lim_memory(BigDecimal per_quota_lim_memory) {
		this.per_quota_lim_memory = per_quota_lim_memory;
	}

	public BigDecimal getPer_quota_req_memory() {
		return quotaMemoryRequest.divide(ocpCluster.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setPer_quota_req_memory(BigDecimal per_quota_req_memory) {
		this.per_quota_req_memory = per_quota_req_memory;
	}

	public BigDecimal getPer_quota_req_cpu() {
		return quotaCpuLimit.divide(ocpCluster.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setPer_quota_req_cpu(BigDecimal per_quota_req_cpu) {
		this.per_quota_req_cpu = per_quota_req_cpu;
	}

	public BigDecimal getPer_quota_lim_cpu() {
		return quotaCpuLimit.divide(ocpCluster.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setPer_quota_lim_cpu(BigDecimal per_quota_lim_cpu) {
		this.per_quota_lim_cpu = per_quota_lim_cpu;
	}

	public BigDecimal getNew_per_lim_memory() {
		return newLimitMemoryWithSimulation.divide(ocpCluster.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setNew_per_lim_memory(BigDecimal new_per_lim_memory) {
		this.new_per_lim_memory = new_per_lim_memory;
	}

	public BigDecimal getNew_per_req_memory() {
		return newRequestMemoryWithSimulation.divide(ocpCluster.getCluster_memory(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setNew_per_req_memory(BigDecimal new_per_req_memory) {
		this.new_per_req_memory = new_per_req_memory;
	}

	public BigDecimal getNew_per_req_cpu() {
		return newRequestCpuWithSimulation.divide(ocpCluster.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setNew_per_req_cpu(BigDecimal new_per_req_cpu) {
		this.new_per_req_cpu = new_per_req_cpu;
	}

	public BigDecimal getNew_per_lim_cpu() {
		return newLimitCpuWithSimulation.divide(ocpCluster.getCluster_cpu(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
	}

	public void setNew_per_lim_cpu(BigDecimal new_per_lim_cpu) {
		this.new_per_lim_cpu = new_per_lim_cpu;
	}
    
	
	
}
