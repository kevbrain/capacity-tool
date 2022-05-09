package com.its4u.buildfactory.beans;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.primefaces.model.chart.MeterGaugeChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.its4u.buildfactory.ScheduleService;
import com.its4u.buildfactory.ocp.OcpAlertPodRestart;
import com.its4u.buildfactory.ocp.OcpCluster;
import com.its4u.buildfactory.ocp.OcpEnvironment;
import com.its4u.buildfactory.ocp.OcpNamespace;
import com.its4u.buildfactory.ocp.OcpResource;
import com.its4u.buildfactory.services.ServiceAlerts;
import com.its4u.buildfactory.services.ServiceKubernetes;
import com.its4u.buildfactory.templateModel.MailAlertModel;
import com.its4u.buildfactory.utils.MailUtils;
import com.its4u.buildfactory.utils.TemplateGenerator;

import freemarker.template.TemplateException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
@Data
@Component
@ManagedBean(name = "clusterCapacityViewerBean")
@SessionScoped
public class ClusterCapacityViewerBean {
	
	private static Logger logger = LoggerFactory.getLogger(ClusterCapacityViewerBean.class);
	
	@Autowired
	private ScheduleService schedulerService;
	
	@Value("${app.ocp.instance.name}")
	private String ocpInstanceName;
	
	@Value("${app.ocp.console.url}")
	private String consoleUrl;
	
	@Value("${app.ocp.server.url}")
	private String server;
	
	@Value("${app.ocp.server.token}")
	private String token;
	
	@Value("${project.default.request.cpu}")
	private String defaultRequestCpu;
	
	@Value("${project.default.limit.cpu}")
	private String defaultLimitCpu;
	
	@Value("${project.default.request.memory}")
	private String defaultRequestMemory;
	
	@Value("${project.default.limit.memory}")
	private String defaultLimitMemory;
	
	@Value("${app.envs}")
	private String appEnvs;
	
	@Value("${prometheus.url}")
	private String prometheusUrl;
	
	@Value("${prometheus.query.maxovertime}")
	private String prometheusQueryMaxovertime;
	
	@Value("${app.envs.namespace.label}")
	private String appEnvsNamespaceLabel;
	
	@Value("${alert.percentage.consumption.resource.threshold}")
	private String alertPercentageConsumptionResourceThreshold;
	
	@Value("${app.simulator.reserve.pods}")
	private String appSimulatorReservePods;
			
	private OcpCluster clusterOcp ;  
		
	private ServiceKubernetes serviceKubernetes;
		
	private boolean displayWorkers;
	
	private boolean displayenvironments;
	
	private boolean displayResume;
	
	private boolean displayAlerts;
	
	private boolean displayTeams;
	
	private boolean displayValueChains;
	
	private boolean workersLoaded=false;
	
	private boolean environmentsLoaded=false;
	
	private boolean resumeLoaded=false;
	
	private List<String> logs;
	
	private MeterGaugeChartModel meterGaugeRequestCPUCurrentUsage;
	
	private MeterGaugeChartModel meterGaugeRequestMEMCurrentUsage;
	
	private MeterGaugeChartModel meterGaugeRequestCPUFullUsage;
	
	private MeterGaugeChartModel meterGaugeRequestMEMFullUsage;
	
	private MeterGaugeChartModel meterGaugeRequestSimulateCPUFullUsage;
	
	private MeterGaugeChartModel meterGaugeRequestSimulateMEMFullUsage;
	
	private int nbrWorkersToAdd=0;
	
	private int newWorkerCpu=7500;
	
	private int newWorkerMemory=63293;
	
	private String mode="default";
	
	private TemplateGenerator generator;
	
	private String pathTemplate="/app/resources/templates";
	
	@Getter(AccessLevel.NONE) private String displayOcpInstanceName;
	
	@PostConstruct
    public void init() {
		try {
			
			logger.info("Try to connect to "+server);
			clusterOcp = new OcpCluster(ocpInstanceName,server,token);
			
			loadProperties();    		    	
	    	serviceKubernetes = new ServiceKubernetes(this.server,this.token);
	   
	    	logs = new ArrayList<String>();
	    	
	    	meterGaugeRequestCPUCurrentUsage = createMeterGauges("% Cpu Request");
	    	meterGaugeRequestMEMCurrentUsage = createMeterGauges("% Memory Request");
	    	
	    	meterGaugeRequestCPUFullUsage = createMeterGauges("% Cpu Request");
	    	meterGaugeRequestMEMFullUsage = createMeterGauges("% Memory Request");
	    	
	    	meterGaugeRequestSimulateCPUFullUsage = createMeterGauges("% Cpu Request");
	    	meterGaugeRequestSimulateMEMFullUsage = createMeterGauges("% Memory Request");
	    	
	    	this.generator = new TemplateGenerator(pathTemplate);
	    		    	
	    	
		} catch (Exception e) {			
			
		}   				
    }
	
	public void reload() {
		logger.info("Start reload");
		this.clusterOcp = schedulerService.getClusterOcp();
		while (schedulerService.getClusterOcp()==null) {			
				try {
					TimeUnit.SECONDS.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Thread.currentThread().interrupt();
				}			
		}
		meterGaugeRequestCPUFullUsage.setValue(clusterOcp.getSim_tot_request_cpu().divide(clusterOcp.getCluster_cpu(),3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue());
		meterGaugeRequestMEMFullUsage.setValue(clusterOcp.getSim_tot_request_memory().divide(clusterOcp.getCluster_memory(),3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue());
		
		meterGaugeRequestCPUCurrentUsage.setValue(clusterOcp.getPrc_totCpuRequest());
		meterGaugeRequestMEMCurrentUsage.setValue(clusterOcp.getPrc_totMemRequest());
		logger.info(clusterOcp.getName()+ " reloaded");
		
		this.environmentsLoaded=true;
		this.displayWorkers=true;
		
	}
	
	private MeterGaugeChartModel createMeterGauges(String title) {
		MeterGaugeChartModel model = initMeterGaugeModel(0);
		model.setTitle(title);
		model.setGaugeLabel("%");
		model.setGaugeLabelPosition("bottom");
		model.setSeriesColors("66cc66,93b75f,E7E658,cc6666");
		return model;
	}
	
	private MeterGaugeChartModel initMeterGaugeModel(int percentage) {
		List<Number> intervals = new ArrayList<Number>() ;
		intervals.add(50);
		intervals.add(80);
		intervals.add(100);
		intervals.add(150);	
		return new MeterGaugeChartModel(percentage,intervals);		
	}
	
	public void startAnalyse() {
		logger.info("Start analyse");
		clusterOcp.loadWorkers(serviceKubernetes,logs);
		this.workersLoaded=true;		
		clusterOcp.loadEnvironments(serviceKubernetes);		
		meterGaugeRequestCPUFullUsage.setValue(clusterOcp.getSim_tot_request_cpu().divide(clusterOcp.getCluster_cpu(),3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue());
		meterGaugeRequestMEMFullUsage.setValue(clusterOcp.getSim_tot_request_memory().divide(clusterOcp.getCluster_memory(),3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue());
		
		meterGaugeRequestCPUCurrentUsage.setValue(clusterOcp.getPrc_totCpuRequest());
		meterGaugeRequestMEMCurrentUsage.setValue(clusterOcp.getPrc_totMemRequest());
						
		ServiceAlerts.alertAll(clusterOcp, new StringBuilder(), serviceKubernetes);
		this.environmentsLoaded=true;
	}
	
	public void simulateAddWorkers() {
		logger.info("Simulate add "+nbrWorkersToAdd+" workers");
		// compute new percentage cpu
		BigDecimal new_cluster_cpu = clusterOcp.getCluster_cpu().add(new BigDecimal(newWorkerCpu*nbrWorkersToAdd));
		BigDecimal new_cluster_memory = clusterOcp.getCluster_memory().add(new BigDecimal(newWorkerMemory*nbrWorkersToAdd));
		
		BigDecimal new_percent_request_cpu = clusterOcp.getSim_tot_request_cpu().divide(new_cluster_cpu,3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
		BigDecimal new_percent_request_memory = clusterOcp.getSim_tot_request_memory().divide(new_cluster_memory,3, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
		
		meterGaugeRequestCPUFullUsage.setValue(new_percent_request_cpu.intValue());
		meterGaugeRequestMEMFullUsage.setValue(new_percent_request_memory.intValue());
		
		logger.info("new % request CPU "+new_percent_request_cpu);
		logger.info("new % request MEMORY "+new_percent_request_memory);
	}
		
	public void displayWorkers() {
		displayWorkers = true;
		displayenvironments = false;
		displayResume = false;
		displayAlerts = false;	
		displayTeams = false;
		displayValueChains = false;
	}
	
	public void displayEnvironments() {
		
		displayWorkers = false;
		displayenvironments = true;
		displayResume = false;
		displayAlerts = false;
		displayTeams = false;
		displayValueChains = false;
	}
	
	public void displayResume() {
	
		displayWorkers = false;
		displayenvironments = false;
		displayResume = true;
		displayAlerts = false;
		displayTeams = false;
		displayValueChains = false;
	}
	
	public void displayTeams() {
		
		displayWorkers = false;
		displayenvironments = false;
		displayResume = false;
		displayAlerts = false;
		displayTeams = true;
		displayValueChains = false;
	}
	
	public void displayAlerts() {
		displayWorkers = false;
		displayenvironments = false;
		displayResume = false;
		displayAlerts = true;
		displayTeams = false;
		displayValueChains = false;
	}
	
	public void displayValueChains() {
		displayWorkers = false;
		displayenvironments = false;
		displayResume = false;
		displayAlerts = false;
		displayTeams = false;
		displayValueChains = true;
	}
	
	public void refreshLogs() {
		
	}
	
	private void loadProperties() {
		clusterOcp.setDefault_request_cpu(new BigDecimal(defaultRequestCpu));
		clusterOcp.setDefault_limit_cpu(new BigDecimal(defaultLimitCpu));
		clusterOcp.setDefault_request_memory(new BigDecimal(defaultRequestMemory));
		clusterOcp.setDefault_limit_memory(new BigDecimal(defaultLimitMemory));
		
		String[] envConfReplicas = appEnvs.split(",");
		String[] envs = new String[envConfReplicas.length];
				
		for (int i=0;i<envConfReplicas.length;i++) {
			String envName=envConfReplicas[i].split(":")[0];
			envs[i]=envName;
			int replicas = Integer.valueOf(envConfReplicas[i].split(":")[1]);
			clusterOcp.getEnvironment().put(envName,new OcpEnvironment(clusterOcp,envName,new ArrayList<OcpNamespace>(),new ArrayList<OcpResource>(),replicas,mode));
		}
						
		clusterOcp.setEnvs(envs);				
		clusterOcp.getEnvironment().put("others",new OcpEnvironment(clusterOcp,"others",new ArrayList<OcpNamespace>(),new ArrayList<OcpResource>(),1,mode));
		clusterOcp.setPrometheus_url(prometheusUrl);
		clusterOcp.setPrometheus_query_maxOverTime(prometheusQueryMaxovertime);
		clusterOcp.setNamespace_env_label(appEnvsNamespaceLabel);
		clusterOcp.setAlertPercentageConsumptionResourceThreshold(new BigDecimal(alertPercentageConsumptionResourceThreshold));
		clusterOcp.setNbrPodsForReserve(Integer.valueOf(appSimulatorReservePods));
		
	}
	
	public void sendTeamNotificationOOMKill() {
		
		System.out.println("Send Email notification for OOMKill ");
		String defaultTeamMail="kevyn.schrondweiler@external-staff.com";
		MailAlertModel model = new MailAlertModel();
		HashMap <String,HashMap <String,OcpAlertPodRestart>> podOOMKillAlertsByTeam = new HashMap<String, HashMap<String,OcpAlertPodRestart>>();
		HashMap <String,OcpAlertPodRestart> podOOMKill = new HashMap<String,OcpAlertPodRestart>();
		podOOMKillAlertsByTeam.put(defaultTeamMail, new HashMap<String,OcpAlertPodRestart>());
		
		for (OcpAlertPodRestart al:clusterOcp.getAlertsPodRestarts()) {
		
			if (al.getNamespace().getTeamEmail()!=null) {
				if (podOOMKillAlertsByTeam.get(al.getNamespace().getTeamEmail())!=null) {
					podOOMKill = podOOMKillAlertsByTeam.get(al.getNamespace().getTeamEmail());
				} else {
					podOOMKill = new HashMap<String,OcpAlertPodRestart>();
					podOOMKillAlertsByTeam.put(al.getNamespace().getTeamEmail(),podOOMKill);
				}				
			} else  {
				podOOMKill = podOOMKillAlertsByTeam.get(defaultTeamMail);
			}
			
			
			if (al.getReason().equalsIgnoreCase("OOMKilled") && !podOOMKill.containsKey(al.getNamespaceName())) {

				podOOMKill.put(al.getNamespaceName(),al);
			}
		}
		
		for (String recipient:podOOMKillAlertsByTeam.keySet()) {
			model.setAlertsPodRestarts(podOOMKillAlertsByTeam.get(recipient));
			String mailContent=null;
			try {
				mailContent=generator.generateAlertEmail(model, generator.getMailOOMKillAlert());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				
			}
			
			if (mailContent!=null && !podOOMKillAlertsByTeam.get(recipient).isEmpty()) {
				try {
					System.out.println("Send email alert to "+recipient);
					//System.out.println(mailContent);
					MailUtils.SendMail(mailContent, "kevyn.schrondweiler@external-staff.com",clusterOcp.getName()+" OOMKilled Alerts");
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					
				}
			}
			
		}
	}

	public String getDisplayOcpInstanceName() {
		return ocpInstanceName.toUpperCase();
	}

}
