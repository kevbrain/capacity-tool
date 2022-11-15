package com.its4u.buildfactory.ocp;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.its4u.buildfactory.model.ChartDataModel;
import com.its4u.buildfactory.services.PrometheusService;
import com.its4u.buildfactory.services.ServiceKubernetes;
import com.its4u.buildfactory.services.ServiceWriter;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.PodList;



public class OcpCluster {
	
	
	private String name;
	
	private String server;
    
	@JsonIgnore
	private String token;
	@JsonIgnore
	private String ha;
	@JsonIgnore
	private int gonogoLevelWarning;
	@JsonIgnore
	private int gonogoLevelBlock;	
	@JsonIgnore
	private String[] envs = {"dev","tst","int","others"};
	@JsonIgnore
	private int NbrPodsForReserve;
	@JsonIgnore
	private HashMap<String,OcpResource> nodes;
	@JsonIgnore
	private HashMap<String,OcpEnvironment> environment;
	@JsonIgnore
	private HashMap<String, ProjectTeam> bilTeams;
	@JsonIgnore
	private HashMap<String, ProjectValueChain> bilValueChains;
	@JsonIgnore
	private HashMap<String,String> bilNamespaces;
	@JsonIgnore
	private List<OcpAlertPodRestart> alertsPodRestarts;
	@JsonIgnore
	private List<OcpAlertPodHightCpu> alertsPodHigthCpu;
	@JsonIgnore
	private List<OcpAlertPodHightMemory> alertsPodHightMemory;
	@JsonIgnore
	private List<OcpAlertProjectWhithoutlimits> alertsPodwithoutLimits;
	@JsonIgnore
	private List<OcpAlertProjectWhithoutquotas> alertsPodwithoutQuotas;
    
	private BigDecimal cluster_cpu = BigDecimal.ZERO;
    
	private BigDecimal cluster_memory = BigDecimal.ZERO;
    
	private int tot_pods;
    
    
	private BigDecimal tot_usage_cpu = BigDecimal.ZERO;
    
	private BigDecimal tot_request_cpu = BigDecimal.ZERO;
    
	private BigDecimal tot_lim_cpu = BigDecimal.ZERO;
    
	private BigDecimal tot_usage_memory = BigDecimal.ZERO;
    
	private BigDecimal tot_request_memory = BigDecimal.ZERO;
    
	private BigDecimal tot_limit_memory = BigDecimal.ZERO;
    
  
    
	private BigDecimal sim_tot_request_cpu = BigDecimal.ZERO;
    
	private BigDecimal sim_tot_lim_cpu = BigDecimal.ZERO;
         
	private BigDecimal sim_tot_request_memory = BigDecimal.ZERO;
    
	private BigDecimal sim_tot_limit_memory = BigDecimal.ZERO;
    
    
	private BigDecimal totQuotaCpuRequest = BigDecimal.ZERO;
    
	private BigDecimal totQuotaCpuLimit = BigDecimal.ZERO;
    
	private BigDecimal totQuotaMemoryRequest = BigDecimal.ZERO;
    
	private BigDecimal totQuotaMemoryLimit = BigDecimal.ZERO;
	
	
	private int prc_totCpu = 0;
	
	private int prc_totMem = 0;
    
	private int prc_totCpuRequest = 0;
	
	private int prc_totCpuLimit = 0 ;
	
    private int prc_totMemRequest = 0;
    
    private int prc_totMemlimit = 0;
    
    private int sim_prc_totCpuRequest = 0;
	
	private int sim_prc_totCpuLimit = 0 ;
	
    private int sim_prc_totMemRequest = 0;
    
    private int sim_prc_totMemlimit = 0;
  
	private BigDecimal default_request_cpu;
    
	private BigDecimal default_limit_cpu ;
    
	private BigDecimal default_request_memory;
    
	private BigDecimal default_limit_memory;
    
	private BigDecimal alertPercentageConsumptionResourceThreshold;
        
	@JsonIgnore
	private String prometheus_url;
	@JsonIgnore
	private String prometheus_query_maxOverTime;
	@JsonIgnore
	private String namespace_env_label;
	@JsonIgnore
	private String groupView_label_1;
	@JsonIgnore
	private String groupView_label_2;
	
	private String consoleUrl;
	@JsonIgnore
	private DonutChartModel teamsRepartionPods;
	@JsonIgnore
	private HorizontalBarChartModel hbarModelReparitionCpu;
	@JsonIgnore
	private ChartDataModel chartDataModelPodRepartition;
	@JsonIgnore
	private ChartDataModel chartDataModelCpuRepartition;
	@JsonIgnore
	private String[] avColors = {"rgb(139, 108, 174)","rgb(10, 101, 116)",
			"rgb(184, 145, 10)","rgb(219, 228, 100)",
			"rgb(168, 85, 139)","rgb(21, 158, 219)",
			"rgb(243, 105, 162)","rgb(34, 40, 219)",
			"rgb(244, 132, 87)","rgb(158, 238, 144)",
			"rgb(89, 168, 108)","rgb(47, 116, 172)",
			"rgb(116, 189, 184)","rgb(77, 189, 50) ",
			"rgb(76, 38, 87)"};
	
	private int availablePodsInCurrentUsage;
	
	private int availablePodsInFullWorkload;
	@JsonIgnore
	private String workerPrefix;
	
	public OcpCluster(String name) {
		super();
		this.name = name.toUpperCase();
	}

    public OcpCluster(String name, String server, String token ) {
                   super();
                   this.token = token;
                   this.name = name.toUpperCase();
                   this.server = server;
                   this.nodes = new HashMap<String,OcpResource>();
                   this.environment = new HashMap<String,OcpEnvironment>();                                             
                   this.bilNamespaces = new HashMap<String,String>();
                   this.alertsPodRestarts = new ArrayList<OcpAlertPodRestart>();
                   this.alertsPodHightMemory = new ArrayList<OcpAlertPodHightMemory>();
                   this.alertsPodHigthCpu = new ArrayList<OcpAlertPodHightCpu>();
                   this.alertsPodwithoutLimits = new ArrayList<OcpAlertProjectWhithoutlimits>();
                   this.alertsPodwithoutQuotas = new ArrayList<OcpAlertProjectWhithoutquotas>();
                   this.bilTeams = new HashMap<String,ProjectTeam>();
                   this.bilValueChains = new HashMap<String, ProjectValueChain>();
    }
    
    public OcpCluster loadPropertiesFromEnv() {
    	this.setName(System.getenv("app.ocp.instance.name"));
    	this.setDefault_request_cpu(new BigDecimal(System.getenv("project.default.request.cpu")));
		this.setDefault_limit_cpu(new BigDecimal(System.getenv("project.default.limit.cpu")));
		this.setDefault_request_memory(new BigDecimal(System.getenv("project.default.request.memory")));
		this.setDefault_limit_memory(new BigDecimal(System.getenv("project.default.limit.memory")));
		String[] envConfReplicas = System.getenv("app.envs").split(",");
		this.envs = new String[envConfReplicas.length];
				
		for (int i=0;i<envConfReplicas.length;i++) {
			String envName=envConfReplicas[i].split(":")[0];
			this.envs[i]=envName;
			int replicas = Integer.valueOf(envConfReplicas[i].split(":")[1]);
			this.getEnvironment().put(envName,new OcpEnvironment(this,envName,new ArrayList<OcpNamespace>(),new ArrayList<OcpResource>(),replicas,"default"));
		}
		
		this.setEnvs(this.envs);
					
		this.getEnvironment().put("others",new OcpEnvironment(this,"others",new ArrayList<OcpNamespace>(),new ArrayList<OcpResource>(),1,"default"));
		this.setPrometheus_url(System.getenv("prometheus.url"));
		this.setConsoleUrl(System.getenv("app.ocp.console.url"));
		this.setPrometheus_query_maxOverTime(System.getenv("prometheus.query.maxovertime"));
		this.setNamespace_env_label(System.getenv("app.envs.namespace.label"));
		this.setAlertPercentageConsumptionResourceThreshold(new BigDecimal(System.getenv("alert.percentage.consumption.resource.threshold")));
		this.setNbrPodsForReserve(Integer.valueOf(System.getenv("app.simulator.reserve.pods")));	
		return this;
    }
    
    public OcpCluster loadProperties() throws Exception{

    	Properties prop = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream("app.properties");
		
		
		try {
			prop.load(is);						
			this.setDefault_request_cpu(new BigDecimal(prop.getProperty("project.default.request.cpu")));
			this.setDefault_limit_cpu(new BigDecimal(prop.getProperty("project.default.limit.cpu")));
			this.setDefault_request_memory(new BigDecimal(prop.getProperty("project.default.request.memory")));
			this.setDefault_limit_memory(new BigDecimal(prop.getProperty("project.default.limit.memory")));
			this.setEnvs(prop.getProperty("app.envs").split(","));
			this.setupEnvironments(prop);
			this.setPrometheus_url(prop.getProperty("prometheus.url"));
			this.setPrometheus_query_maxOverTime(prop.getProperty("prometheus.query.maxovertime"));
			this.setNamespace_env_label(prop.getProperty("app.envs.namespace.label"));
			this.setAlertPercentageConsumptionResourceThreshold(new BigDecimal(prop.getProperty("alert.percentage.consumption.resource.threshold")));
			this.setNbrPodsForReserve(Integer.valueOf(prop.getProperty("app.simulator.reserve.pods")));	
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			throw new Exception("Could not load application properties");
		}		
		return this;
	}
    
    public void setupEnvironments(Properties prop) {
    	for (String ev : envs) {
     	   this.environment.put(ev,new OcpEnvironment(this,ev,new ArrayList<OcpNamespace>(),new ArrayList<OcpResource>(),
     			   Integer.valueOf(prop.getProperty("app.env.default.replicas."+ev)),
     					   this.ha));
     	                   			                   	   
        }   
    	// add others
    	this.environment.put("others",new OcpEnvironment(this,"others",new ArrayList<OcpNamespace>(),new ArrayList<OcpResource>(),1,this.ha));
    }
    
    public  void loadWorkers(ServiceKubernetes serviceKubernetes,List<String> logs) {
        NodeList nodeslist = serviceKubernetes.getClient().nodes().withLabel("node-role.kubernetes.io/worker","").withoutLabel("node-role.kubernetes.io/infra","").list();                       
                
        for (Node node: nodeslist.getItems()) { 
             
           NodeStatus nodeStatus = node.getStatus();
           String hostname = node.getMetadata().getLabels().get("kubernetes.io/hostname");
           logs.add("Analyse "+hostname);
           OcpNode nodeOcp= new OcpNode(hostname);
           this.getNodes().put(hostname,nodeOcp);
               
           nodeOcp.setAllocatable_cpu(new BigDecimal(nodeStatus.getAllocatable().get("cpu").getAmount()));    
           nodeOcp.setAllocatable_memory(new BigDecimal(nodeStatus.getAllocatable().get("memory").getAmount()).divide(new BigDecimal(1024)));
           
           this.setCluster_cpu(this.getCluster_cpu().add(nodeOcp.getAllocatable_cpu()));
           this.setCluster_memory(this.getCluster_memory().add(nodeOcp.getAllocatable_memory()));
                                         
           PodList podList = serviceKubernetes.getClient().pods().inAnyNamespace().withField("spec.nodeName", hostname).withField("status.phase", "Running").list();
           nodeOcp.setPodList(podList.getItems());
           nodeOcp.setNbrPods(podList.getItems().size());
           logs.add("Analyse pods of "+hostname);
           nodeOcp.analysePods();
           logs.add("Load Metrics of "+hostname);
           PrometheusService.loadMetricsNodes(serviceKubernetes.getClient(),this);
        }
        
    }
    
    public void loadEnvironments(ServiceKubernetes serviceKubernetes) {
    	
    	this.tot_pods=0;
    	this.tot_request_cpu = BigDecimal.ZERO;
    	this.tot_lim_cpu = BigDecimal.ZERO;
    	this.tot_request_memory = BigDecimal.ZERO;
    	this.tot_limit_memory = BigDecimal.ZERO;
    	this.sim_tot_request_cpu = BigDecimal.ZERO;
    	this.sim_tot_lim_cpu = BigDecimal.ZERO;
    	this.sim_tot_request_memory = BigDecimal.ZERO;
    	this.sim_tot_limit_memory = BigDecimal.ZERO;
    	
    	for (OcpEnvironment env:environment.values()) {			
			env.loadNamespaces(serviceKubernetes,env.getName());  			
			ServiceWriter.writeAnalyseOfNamespaces(this, env.getName(), true);
			ServiceWriter.writeSimulateAnalyseOfNamespaces(this, env.getName(), true);
    	}
    	
    }
    
    public void relaunchSimulation(ServiceKubernetes serviceKubernetes,String ha) {
    	this.ha=ha;
    	for (OcpEnvironment env:environment.values()) {	
    		env.setHa(ha);
    		env.relaunchSimulation(serviceKubernetes, ha);
    	}
    }
   
    

    public  void analyseByQuotas() {
    	BigDecimal clusterQuotaCpuLimit = BigDecimal.ZERO;
    	BigDecimal clusterQuotaMemoryLimit = BigDecimal.ZERO;
    	BigDecimal clusterQuotaCpuRequest = BigDecimal.ZERO;
    	BigDecimal clusterQuotaMemoryRequest = BigDecimal.ZERO;
    	
    	BigDecimal estimatedRequestMemory = BigDecimal.ZERO;
    	
    	int nbrEnv = this.getEnvironment().size();
    	
    	for (OcpEnvironment env: this.getEnvironment().values()) {
    		estimatedRequestMemory = BigDecimal.ZERO;
    		clusterQuotaCpuRequest = clusterQuotaCpuRequest.add(env.getQuotaCpuRequest());
    		clusterQuotaMemoryRequest = clusterQuotaMemoryRequest.add(env.getQuotaMemoryRequest());
    		clusterQuotaCpuLimit = clusterQuotaCpuLimit.add(env.getQuotaCpuLimit());
    		clusterQuotaMemoryLimit = clusterQuotaMemoryLimit.add(env.getQuotaMemoryLimit());
    		
    		estimatedRequestMemory = estimatedRequestMemory.add( (env.getQuotaCpuRequest().divide(new BigDecimal(nbrEnv), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(env.getDefaultMaxReplicas()))));
    		System.out.println(env.getName()+ " Quotas Request: CPU = "+env.getQuotaCpuRequest()+ "  MEM = "+env.getQuotaMemoryRequest()); 
    		System.out.println(env.getName()+ " Quotas Limit: CPU = "+env.getQuotaCpuLimit()+ "  MEM = "+env.getQuotaMemoryLimit());
    		System.out.println("-->"+estimatedRequestMemory.setScale(0, RoundingMode.HALF_UP));
    	}
    	System.out.println("TOTAL ENVS =  Quotas Requests: CPU = "+clusterQuotaCpuRequest+ "  MEM = "+clusterQuotaMemoryRequest); 
    	System.out.println("TOTAL ENVS =  Quotas Limit:    CPU = "+clusterQuotaCpuLimit+ "  MEM = "+clusterQuotaMemoryLimit);    		
    }
   
    public int calculateCapacityForNewPodByCluster(BigDecimal newRequest_cpu,BigDecimal newLimit_cpu,BigDecimal newRequest_memory,BigDecimal newLimit_memory) {
    	int nbrPodPossibleToDeploy = 1;
    	boolean tryToDeploy= true;
    	
        	
    	while (tryToDeploy) {
  		  
    		newRequest_cpu = newRequest_cpu.add(this.getDefault_request_cpu());
    		newLimit_cpu = newLimit_cpu.add(this.getDefault_limit_cpu());
    		
    		newRequest_memory = newRequest_memory.add(this.getDefault_request_memory());
    		newLimit_memory = newLimit_memory.add(this.getDefault_limit_memory());
    		   	 		    		        	
    		
    		if (     newRequest_cpu.compareTo(BigDecimal.ZERO)==0 ||
    				 newLimit_cpu.compareTo(BigDecimal.ZERO)==0 ||
    				 newRequest_memory.compareTo(BigDecimal.ZERO)==0 ||
    				 newLimit_memory.compareTo(BigDecimal.ZERO)==0 ||    			  
    				 newRequest_cpu.compareTo(this.getCluster_cpu())>0 ||
    				 newRequest_memory.compareTo(this.getCluster_memory())>0

    				)
    		{  		
    			nbrPodPossibleToDeploy--;
    			tryToDeploy= false;    			
    		} else {    			
    			nbrPodPossibleToDeploy++;    			
    		}
    	}
    	return nbrPodPossibleToDeploy;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getHa() {
		return ha;
	}

	public void setHa(String ha) {
		this.ha = ha;
	}

	public String[] getEnvs() {
		return envs;
	}

	public void setEnvs(String[] envs) {
		this.envs = envs;
	}

	public int getNbrPodsForReserve() {
		return NbrPodsForReserve;
	}

	public void setNbrPodsForReserve(int nbrPodsForReserve) {
		NbrPodsForReserve = nbrPodsForReserve;
	}

	public HashMap<String, OcpResource> getNodes() {
		return nodes;
	}

	public void setNodes(HashMap<String, OcpResource> nodes) {
		this.nodes = nodes;
	}

	public HashMap<String, OcpEnvironment> getEnvironment() {
		return environment;
	}

	public void setEnvironment(HashMap<String, OcpEnvironment> environment) {
		this.environment = environment;
	}

	public HashMap<String, String> getBilNamespaces() {
		return bilNamespaces;
	}

	public void setBilNamespaces(HashMap<String, String> bilNamespaces) {
		this.bilNamespaces = bilNamespaces;
	}

	public BigDecimal getCluster_cpu() {
		return cluster_cpu;
	}

	public void setCluster_cpu(BigDecimal cluster_cpu) {
		this.cluster_cpu = cluster_cpu;
	}

	public BigDecimal getCluster_memory() {
		return cluster_memory;
	}

	public void setCluster_memory(BigDecimal cluster_memory) {
		this.cluster_memory = cluster_memory;
	}

	public int getTot_pods() {
		return tot_pods;
	}

	public void setTot_pods(int tot_pods) {
		this.tot_pods = tot_pods;
	}

	public BigDecimal getTot_usage_cpu() {
		return tot_usage_cpu;
	}

	public void setTot_usage_cpu(BigDecimal tot_usage_cpu) {
		this.tot_usage_cpu = tot_usage_cpu;
	}

	public BigDecimal getTot_request_cpu() {
		return tot_request_cpu;
	}

	public void setTot_request_cpu(BigDecimal tot_request_cpu) {
		this.tot_request_cpu = tot_request_cpu;
	}

	public BigDecimal getTot_lim_cpu() {
		return tot_lim_cpu;
	}

	public void setTot_lim_cpu(BigDecimal tot_lim_cpu) {
		this.tot_lim_cpu = tot_lim_cpu;
	}

	public BigDecimal getTot_usage_memory() {
		return tot_usage_memory;
	}

	public void setTot_usage_memory(BigDecimal tot_usage_memory) {
		this.tot_usage_memory = tot_usage_memory;
	}

	public BigDecimal getTot_request_memory() {
		return tot_request_memory;
	}

	public void setTot_request_memory(BigDecimal tot_request_memory) {
		this.tot_request_memory = tot_request_memory;
	}

	public BigDecimal getTot_limit_memory() {
		return tot_limit_memory;
	}

	public void setTot_limit_memory(BigDecimal tot_limit_memory) {
		this.tot_limit_memory = tot_limit_memory;
	}

	public BigDecimal getSim_tot_request_cpu() {
		return sim_tot_request_cpu;
	}

	public void setSim_tot_request_cpu(BigDecimal sim_tot_request_cpu) {
		this.sim_tot_request_cpu = sim_tot_request_cpu;
	}

	public BigDecimal getSim_tot_lim_cpu() {
		return sim_tot_lim_cpu;
	}

	public void setSim_tot_lim_cpu(BigDecimal sim_tot_lim_cpu) {
		this.sim_tot_lim_cpu = sim_tot_lim_cpu;
	}

	public BigDecimal getSim_tot_request_memory() {
		return sim_tot_request_memory;
	}

	public void setSim_tot_request_memory(BigDecimal sim_tot_request_memory) {
		this.sim_tot_request_memory = sim_tot_request_memory;
	}

	public BigDecimal getSim_tot_limit_memory() {
		return sim_tot_limit_memory;
	}

	public void setSim_tot_limit_memory(BigDecimal sim_tot_limit_memory) {
		this.sim_tot_limit_memory = sim_tot_limit_memory;
	}

	public BigDecimal getTotQuotaCpuRequest() {
		return totQuotaCpuRequest;
	}

	public void setTotQuotaCpuRequest(BigDecimal totQuotaCpuRequest) {
		this.totQuotaCpuRequest = totQuotaCpuRequest;
	}

	public BigDecimal getTotQuotaCpuLimit() {
		return totQuotaCpuLimit;
	}

	public void setTotQuotaCpuLimit(BigDecimal totQuotaCpuLimit) {
		this.totQuotaCpuLimit = totQuotaCpuLimit;
	}

	public BigDecimal getTotQuotaMemoryRequest() {
		return totQuotaMemoryRequest;
	}

	public void setTotQuotaMemoryRequest(BigDecimal totQuotaMemoryRequest) {
		this.totQuotaMemoryRequest = totQuotaMemoryRequest;
	}

	public BigDecimal getTotQuotaMemoryLimit() {
		return totQuotaMemoryLimit;
	}

	public void setTotQuotaMemoryLimit(BigDecimal totQuotaMemoryLimit) {
		this.totQuotaMemoryLimit = totQuotaMemoryLimit;
	}

	public int getPrc_totCpuRequest() {
		return prc_totCpuRequest;
	}

	public void setPrc_totCpuRequest(int prc_totCpuRequest) {
		this.prc_totCpuRequest = prc_totCpuRequest;
	}

	public int getPrc_totCpuLimit() {
		return prc_totCpuLimit;
	}

	public void setPrc_totCpuLimit(int prc_totCpuLimit) {
		this.prc_totCpuLimit = prc_totCpuLimit;
	}

	public int getPrc_totMemRequest() {
		return prc_totMemRequest;
	}

	public void setPrc_totMemRequest(int prc_totMemRequest) {
		this.prc_totMemRequest = prc_totMemRequest;
	}

	public int getPrc_totMemlimit() {
		return prc_totMemlimit;
	}

	public void setPrc_totMemlimit(int prc_totMemlimit) {
		this.prc_totMemlimit = prc_totMemlimit;
	}

	public BigDecimal getDefault_request_cpu() {
		return default_request_cpu;
	}

	public void setDefault_request_cpu(BigDecimal default_request_cpu) {
		this.default_request_cpu = default_request_cpu;
	}

	public BigDecimal getDefault_limit_cpu() {
		return default_limit_cpu;
	}

	public void setDefault_limit_cpu(BigDecimal default_limit_cpu) {
		this.default_limit_cpu = default_limit_cpu;
	}

	public BigDecimal getDefault_request_memory() {
		return default_request_memory;
	}

	public void setDefault_request_memory(BigDecimal default_request_memory) {
		this.default_request_memory = default_request_memory;
	}

	public BigDecimal getDefault_limit_memory() {
		return default_limit_memory;
	}

	public void setDefault_limit_memory(BigDecimal default_limit_memory) {
		this.default_limit_memory = default_limit_memory;
	}

	public BigDecimal getAlertPercentageConsumptionResourceThreshold() {
		return alertPercentageConsumptionResourceThreshold;
	}

	public void setAlertPercentageConsumptionResourceThreshold(BigDecimal alertPercentageConsumptionResourceThreshold) {
		this.alertPercentageConsumptionResourceThreshold = alertPercentageConsumptionResourceThreshold;
	}

	public String getPrometheus_url() {
		return prometheus_url;
	}

	public void setPrometheus_url(String prometheus_url) {
		this.prometheus_url = prometheus_url;
	}

	public String getPrometheus_query_maxOverTime() {
		return prometheus_query_maxOverTime;
	}

	public void setPrometheus_query_maxOverTime(String prometheus_query_maxOverTime) {
		this.prometheus_query_maxOverTime = prometheus_query_maxOverTime;
	}

	public String getNamespace_env_label() {
		return namespace_env_label;
	}

	public void setNamespace_env_label(String namespace_env_label) {
		this.namespace_env_label = namespace_env_label;
	}

	public int getSim_prc_totCpuRequest() {
		return sim_prc_totCpuRequest;
	}

	public void setSim_prc_totCpuRequest(int sim_prc_totCpuRequest) {					
		this.sim_prc_totCpuRequest = sim_prc_totCpuRequest;
	}

	public int getSim_prc_totCpuLimit() {
		return sim_prc_totCpuLimit;
	}

	public void setSim_prc_totCpuLimit(int sim_prc_totCpuLimit) {
		this.sim_prc_totCpuLimit = sim_prc_totCpuLimit;
	}

	public int getSim_prc_totMemRequest() {
		return sim_prc_totMemRequest;
	}

	public void setSim_prc_totMemRequest(int sim_prc_totMemRequest) {
		this.sim_prc_totMemRequest = sim_prc_totMemRequest;
	}

	public int getSim_prc_totMemlimit() {
		return sim_prc_totMemlimit;
	}

	public void setSim_prc_totMemlimit(int sim_prc_totMemlimit) {
		this.sim_prc_totMemlimit = sim_prc_totMemlimit;
	}

	public List<OcpAlertPodRestart> getAlertsPodRestarts() {
		return alertsPodRestarts;
	}

	public void setAlertsPodRestarts(List<OcpAlertPodRestart> alertsPodRestarts) {
		this.alertsPodRestarts = alertsPodRestarts;
	}

	public List<OcpAlertPodHightCpu> getAlertsPodHigthCpu() {
		return alertsPodHigthCpu;
	}

	public void setAlertsPodHigthCpu(List<OcpAlertPodHightCpu> alertsPodHigthCpu) {
		this.alertsPodHigthCpu = alertsPodHigthCpu;
	}

	public List<OcpAlertPodHightMemory> getAlertsPodHightMemory() {
		return alertsPodHightMemory;
	}

	public void setAlertsPodHightMemory(List<OcpAlertPodHightMemory> alertsPodHightMemory) {
		this.alertsPodHightMemory = alertsPodHightMemory;
	}

	public List<OcpAlertProjectWhithoutlimits> getAlertsPodwithoutLimits() {
		return alertsPodwithoutLimits;
	}

	public void setAlertsPodwithoutLimits(List<OcpAlertProjectWhithoutlimits> alertsPodwithoutLimits) {
		this.alertsPodwithoutLimits = alertsPodwithoutLimits;
	}

	public List<OcpAlertProjectWhithoutquotas> getAlertsPodwithoutQuotas() {
		return alertsPodwithoutQuotas;
	}

	public void setAlertsPodwithoutQuotas(List<OcpAlertProjectWhithoutquotas> alertsPodwithoutQuotas) {
		this.alertsPodwithoutQuotas = alertsPodwithoutQuotas;
	}

	public HashMap<String, ProjectTeam> getBilTeams() {
		return bilTeams;
	}

	public void setBilTeams(HashMap<String, ProjectTeam> bilTeams) {
		this.bilTeams = bilTeams;
	}

	public HashMap<String, ProjectValueChain> getBilValueChains() {
		return bilValueChains;
	}

	public void setBilValueChains(HashMap<String, ProjectValueChain> bilValueChains) {
		this.bilValueChains = bilValueChains;
	}

	@JsonIgnore
	public ChartDataModel getDataValueForPodsRepartition() {
		if (chartDataModelPodRepartition==null) {
			chartDataModelPodRepartition = new ChartDataModel();
			int i=0;
			for (ProjectTeam team:bilTeams.values()) {
				chartDataModelPodRepartition.getValues().add(team.getPods());
				chartDataModelPodRepartition.getLabels().add(team.getTeamName());
				chartDataModelPodRepartition.getColors().add(avColors[i]);
				i++;
			}
		}
		return chartDataModelPodRepartition;		
	}
	
	@JsonIgnore
	public ChartDataModel getDataValueForCPURepartition() {
		if (chartDataModelCpuRepartition==null) {
			chartDataModelCpuRepartition = new ChartDataModel();
			int i=0;
			for (ProjectTeam team:bilTeams.values()) {
				chartDataModelCpuRepartition.getValues().add(team.getCurrentReqCpu());
				chartDataModelCpuRepartition.getLabels().add(team.getTeamName());
				chartDataModelCpuRepartition.getColors().add(avColors[i]);
				i++;
			}
		}
		return chartDataModelCpuRepartition;		
	}
	
	public DonutChartModel getTeamsRepartionPods() {
		teamsRepartionPods = new DonutChartModel();
		ChartData data = new ChartData();		
		DonutChartDataSet dataSet = new DonutChartDataSet();
		ChartDataModel chartDataModelRepartitionPods = getDataValueForPodsRepartition();
		dataSet.setData(chartDataModelRepartitionPods.getValues());
		dataSet.setBackgroundColor(chartDataModelRepartitionPods.getColors());
		data.addChartDataSet(dataSet);		
		data.setLabels(chartDataModelRepartitionPods.getLabels());
		teamsRepartionPods.setData(data);				
		return teamsRepartionPods;
	}


	public void setTeamsRepartionPods(DonutChartModel teamsRepartionPods) {
		this.teamsRepartionPods = teamsRepartionPods;		
	}

	public HorizontalBarChartModel getHbarModelReparitionCpu() {
		hbarModelReparitionCpu = new HorizontalBarChartModel();
		ChartData data = new ChartData();
		HorizontalBarChartDataSet dataSet = new HorizontalBarChartDataSet();
		ChartDataModel chartDataModelRepartitionCpu = getDataValueForCPURepartition();
		dataSet.setData(chartDataModelRepartitionCpu.getValues());
		dataSet.setBackgroundColor(chartDataModelRepartitionCpu.getColors());
		data.addChartDataSet(dataSet);		
		data.setLabels(chartDataModelRepartitionCpu.getLabels());
		hbarModelReparitionCpu.setData(data);
		return hbarModelReparitionCpu;
	}

	public void setHbarModelReparitionCpu(HorizontalBarChartModel hbarModelReparitionCpu) {
		this.hbarModelReparitionCpu = hbarModelReparitionCpu;
	}

	public int getPrc_totCpu() {
		return tot_usage_cpu.divide(cluster_cpu, 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue(); 
	}

	public void setPrc_totCpu(int prc_totCpu) {
		this.prc_totCpu = prc_totCpu;
	}

	public int getPrc_totMem() {
		return tot_usage_memory.divide(cluster_memory, 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue(); 
	}

	public void setPrc_totMem(int prc_totMem) {
		this.prc_totMem = prc_totMem;
	}

	public int getAvailablePodsInCurrentUsage() {		 	
    	return calculateCapacityForNewPodByCluster(tot_request_cpu,tot_lim_cpu,tot_request_memory,tot_limit_memory);		
	}

	public void setAvailablePodsInCurrentUsage(int availablePodsInCurrentUsage) {
		this.availablePodsInCurrentUsage = availablePodsInCurrentUsage;
	}

	public int getAvailablePodsInFullWorkload() {
    	return calculateCapacityForNewPodByCluster(sim_tot_request_cpu,sim_tot_lim_cpu,sim_tot_request_memory,sim_tot_limit_memory);
	}

	public void setAvailablePodsInFullWorkload(int availablePodsInFullWorkload) {
		this.availablePodsInFullWorkload = availablePodsInFullWorkload;
	}

	public int getGonogoLevelWarning() {
		return gonogoLevelWarning;
	}

	public void setGonogoLevelWarning(int gonogoLevelWarning) {
		this.gonogoLevelWarning = gonogoLevelWarning;
	}

	public int getGonogoLevelBlock() {
		return gonogoLevelBlock;
	}

	public void setGonogoLevelBlock(int gonogoLevelBlock) {
		this.gonogoLevelBlock = gonogoLevelBlock;
	}

	public String getConsoleUrl() {
		return consoleUrl;
	}

	public void setConsoleUrl(String consoleUrl) {
		this.consoleUrl = consoleUrl;
	}

	public String getWorkerPrefix() {
		return workerPrefix;
	}

	public void setWorkerPrefix(String workerPrefix) {
		this.workerPrefix = workerPrefix;
	}

	public String getGroupView_label_1() {
		return groupView_label_1;
	}

	public void setGroupView_label_1(String groupView_label_1) {
		this.groupView_label_1 = groupView_label_1;
	}

	public String getGroupView_label_2() {
		return groupView_label_2;
	}

	public void setGroupView_label_2(String groupView_label_2) {
		this.groupView_label_2 = groupView_label_2;
	}

	
}
