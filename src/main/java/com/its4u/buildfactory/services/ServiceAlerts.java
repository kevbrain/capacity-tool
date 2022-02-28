package com.its4u.buildfactory.services;

import java.math.BigDecimal;

import com.its4u.buildfactory.ocp.OcpAlertPodHightCpu;
import com.its4u.buildfactory.ocp.OcpAlertPodHightMemory;
import com.its4u.buildfactory.ocp.OcpAlertPodRestart;
import com.its4u.buildfactory.ocp.OcpAlertProjectWhithoutlimits;
import com.its4u.buildfactory.ocp.OcpAlertProjectWhithoutquotas;
import com.its4u.buildfactory.ocp.OcpCluster;
import com.its4u.buildfactory.ocp.OcpEnvironment;
import com.its4u.buildfactory.ocp.OcpNamespace;
import com.its4u.buildfactory.ocp.OcpPod;

import io.fabric8.kubernetes.api.model.Pod;



public class ServiceAlerts {
	
	public ServiceAlerts() {
	    throw new IllegalStateException("Service class");
	  }
	
	public static StringBuilder alertAll(OcpCluster clusterOcp,StringBuilder txtMail,ServiceKubernetes serviceKubernetes) {
				
    	txtMail.append("<h1>Alertes</h1>");
    	alertOfARedisPresence(clusterOcp,txtMail);
		alertProjectsWithoutQuotas(clusterOcp,txtMail,serviceKubernetes);
		alertProjectsWithoutDefaultLimits(clusterOcp,txtMail,serviceKubernetes);
		alertProjectsWithPodIntempestiveRestart(clusterOcp,txtMail);
		alertProjectsUnableToDoRollingUpdateWith2ReplicasRunning(clusterOcp,txtMail);
		alertPodsWithHightCpuConsumption(clusterOcp,txtMail);
		alertPodsWithHightMemoryConsumption(clusterOcp,txtMail);
		return txtMail;
	}

	public static StringBuilder alertOfARedisPresence(OcpCluster clusterOcp,StringBuilder txtMail) {
		txtMail.append("<h2>Namespaces with REDIS instance deployed</h2>");
		txtMail.append("<table>");
    	txtMail.append("<tr>").append("<th>Project</th><th>Link OCP</th>").append("</tr>");
    	String link;
    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
    		for (OcpNamespace namespace: env.getNamespaces()) {
    			
    			if (!env.getName().equalsIgnoreCase("others") && namespace.isContainsRedis()) {
    				link = clusterOcp.getConsoleUrl()+"/k8s/ns/"+namespace.getName();    				    				    			    				
    				txtMail.append("<tr><td>").append(namespace.getName()).append("</td><td><a href=\"").append(link).append("\">ocp link</a></td></tr>");
    				
    			}
    		}
    	}
    	txtMail.append("</table>");
		return txtMail;
	}
		
	
	
	public static StringBuilder alertProjectsWithoutQuotas(OcpCluster clusterOcp,StringBuilder txtMail,ServiceKubernetes serviceKubernetes) {
		
    	txtMail.append("<h2>Projects without Resources Quota</h2>");
    	txtMail.append("Please, define resource quotas for");
    	txtMail.append("<table>");
    	txtMail.append("<tr>").append("<th>Project</th><th>Link OCP</th><th>Fix</th>").append("</tr>");
    	String link;
    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
    		for (OcpNamespace namespace: env.getNamespaces()) {
    			
    			if (!namespace.isProtectedByQuotas()&& !env.getName().equalsIgnoreCase("others")) {
    				link = clusterOcp.getConsoleUrl()+"/k8s/ns/"+namespace.getName()+"/resourcequotas";    				
    				    			
    				namespace.searchRequestAndLimitsInDeployment(serviceKubernetes);
    				
    				BigDecimal hardCpuRequest = namespace.getContainer_cpu_request().multiply(new BigDecimal(3));
    				BigDecimal hardCpuLimit = namespace.getContainer_cpu_limit().multiply(new BigDecimal(3));
    				BigDecimal hardMemoryRequest = namespace.getContainer_memory_request().multiply(new BigDecimal(3));
    				BigDecimal hardMemoryLimit = namespace.getContainer_memory_limit().multiply(new BigDecimal(3));
    				
    				if (namespace.isContainsRedis()) {
    					hardCpuRequest = hardCpuRequest.add(new BigDecimal(200));
    					hardCpuLimit = hardCpuLimit.add(new BigDecimal(500));
    					hardMemoryRequest = hardMemoryRequest.add(new BigDecimal(128));
    					hardMemoryLimit = hardMemoryLimit. add(new BigDecimal(128));
    				}
    				
    				String fix = "oc create quota "+namespace.getName()+"-quota --hard=requests.cpu="+hardCpuRequest+"m,limits.cpu="+hardCpuLimit
    						+"m,requests.memory="+hardMemoryRequest+"Mi,limits.memory="+hardMemoryLimit+"Mi -n "+namespace.getName();

    				txtMail.append("<tr><td>").append(namespace.getName()).append("</td><td><a href=\"").append(link).append("\">ocp link</a></td><td>"+fix+"</td></tr>");
    				
    				OcpAlertProjectWhithoutquotas alert= new OcpAlertProjectWhithoutquotas(namespace.getName(),link);
					clusterOcp.getAlertsPodwithoutQuotas().add(alert);
    			}
    		}
    	}
    	txtMail.append("</table>");
		return txtMail;
	}
	
	public static StringBuilder alertProjectsWithoutDefaultLimits(OcpCluster clusterOcp,StringBuilder txtMail,ServiceKubernetes serviceKubernetes) {
		String link;
		txtMail.append("<h2>Projects without Default Limits</h2>");
    	txtMail.append("<table>");
    	txtMail.append("<tr>").append("<th>Project</th><th>Link OCP</th><th>fix</th>").append("</tr>");
    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
    		for (OcpNamespace namespace: env.getNamespaces()) {
    			
    			if (!namespace.isProtectedByLimits()&& !env.getName().equalsIgnoreCase("others")) {
    				link = clusterOcp.getConsoleUrl()+"/k8s/ns/"+namespace.getName()+"/limitranges";
    				
    				namespace.searchRequestAndLimitsInDeployment(serviceKubernetes);
    				
    				BigDecimal hardCpuRequest = namespace.getContainer_cpu_request().multiply(new BigDecimal(3));
    				BigDecimal hardCpuLimit = namespace.getContainer_cpu_limit().multiply(new BigDecimal(3));
    				BigDecimal hardMemoryRequest = namespace.getContainer_memory_request().multiply(new BigDecimal(3));
    				BigDecimal hardMemoryLimit = namespace.getContainer_memory_limit().multiply(new BigDecimal(3));
    				
    				if (namespace.isContainsRedis()) {
    					hardCpuRequest = hardCpuRequest.add(new BigDecimal(200));
    					hardCpuLimit = hardCpuLimit.add(new BigDecimal(500));
    					hardMemoryRequest = hardMemoryRequest.add(new BigDecimal(128));
    					hardMemoryLimit = hardMemoryLimit. add(new BigDecimal(128));
    				}
    				
    				String fix = "oc delete quota "+namespace.getName()+"-quota;oc create quota "+namespace.getName()+"-quota --hard=requests.cpu="+hardCpuRequest+"m,limits.cpu="+hardCpuLimit
    						+"m,requests.memory="+hardMemoryRequest+"Mi,limits.memory="+hardMemoryLimit+"Mi -n "+namespace.getName();

    				txtMail.append("<tr><td>").append(namespace.getName()).append("</td><td><a href=\"").append(link).append("\">ocp link</a></td><td>"+fix+"</td></tr>");
    				
    				OcpAlertProjectWhithoutlimits alert= new OcpAlertProjectWhithoutlimits(namespace.getName(),link);
					clusterOcp.getAlertsPodwithoutLimits().add(alert);
    			}
    		}
    	}
    	txtMail.append("</table>");
		return txtMail;
	}
	
	public static StringBuilder alertProjectsUnableToDoRollingUpdateWith2ReplicasRunning(OcpCluster clusterOcp,StringBuilder txtMail) {
		String link;
		txtMail.append("<h2>Projects unable to do a rolling update deployment with 2 replicas running</h2>");
    	txtMail.append("<table>");
    	txtMail.append("<tr>").append("<th>Project</th><th>Link OCP</th>").append("</tr>");
    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
    		for (OcpNamespace namespace: env.getNamespaces()) {
    			
    			if (namespace.getNbrPods()+namespace.getNbrPodPossibleToDeploy()<3 && !env.getName().equalsIgnoreCase("others")) {
    				link = clusterOcp.getConsoleUrl()+"/k8s/ns/"+namespace.getName()+"/limitranges";
    				txtMail.append("<tr><td>").append(namespace.getName()).append("</td><td><a href=\"").append(link).append("\">ocp link</a></td></tr>");
    			}
    		}
    	}
    	txtMail.append("</table>");
		return txtMail;
	}
	
	public static StringBuilder alertProjectsWithPodIntempestiveRestart(OcpCluster clusterOcp,StringBuilder txtMail) {
		String link;
		txtMail.append("<h2>Pods with intempestive restart</h2>");
    	txtMail.append("Please, check the reason");
    	txtMail.append("<table>");
    	txtMail.append("<tr>").append("<th>Project</th><th>Pod</th><th>Count Restart</th><th>Reason</th><th>Link OCP</th>").append("</tr>");
    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
    		for (OcpNamespace namespace: env.getNamespaces()) {
    			if (!env.getName().equalsIgnoreCase("others")) {
    				for (OcpPod pod : namespace.getOcpPods().values()) {
    					if (pod.getCountRestart()>0) {    						    						
    						link = clusterOcp.getConsoleUrl()+"/k8s/ns/"+namespace.getName()+"/pods/"+pod.getName();
    						txtMail.append("<tr><td>").append(namespace.getName())
    						     .append("</td><td>").append(pod.getName())
    						     .append("</td><td>").append(pod.getCountRestart())
    						     .append("</td><td>").append(pod.getTerminatedReason())
    							 .append("</td><td><a href=\"").append(link).append("\">ocp link</a></td></tr>");
    						
    						OcpAlertPodRestart alert= new OcpAlertPodRestart(namespace.getName(),link,pod.getName(),pod.getCountRestart(),pod);
    						alert.setReason(pod.getTerminatedReason());
    						alert.setNamespace(namespace);
    						clusterOcp.getAlertsPodRestarts().add(alert);
    					}	    							    			
    				}	 
    				for (Pod pod: namespace.getPodListFailed()) {
    					if (pod.getStatus().getReason()!=null && pod.getStatus().getReason().equalsIgnoreCase("Evicted")) {
    						OcpPod ocpPod = new OcpPod(pod.getMetadata().getName());
    						ocpPod.setTerminatedReason("Evicted : "+pod.getStatus().getMessage());
    						ocpPod.setCountRestart(0);
    						link = clusterOcp.getConsoleUrl()+"/k8s/ns/"+namespace.getName()+"/pods/"+ocpPod.getName();
    						txtMail.append("<tr><td>").append(namespace.getName())
    						     .append("</td><td>").append(ocpPod.getName())
    						     .append("</td><td>").append(ocpPod.getCountRestart())
    						     .append("</td><td>").append(ocpPod.getTerminatedReason())
    							 .append("</td><td><a href=\"").append(link).append("\">ocp link</a></td></tr>");
    						
    						OcpAlertPodRestart alert= new OcpAlertPodRestart(namespace.getName(),link,ocpPod.getName(),ocpPod.getCountRestart(),ocpPod);
    						alert.setReason(ocpPod.getTerminatedReason());
    						alert.setNamespace(namespace);
    						clusterOcp.getAlertsPodRestarts().add(alert);
    					}
    				}
    			}
    		}
    	}
    	txtMail.append("</table>");
		return txtMail;
	}
	
	
	public static StringBuilder alertPodsWithHightCpuConsumption(OcpCluster clusterOcp,StringBuilder txtMail) {
		String link;
		txtMail.append("<h2>Pods with hight cpu consumption</h2>");
    	txtMail.append("Please, check the reason");
    	txtMail.append("<table>");
    	txtMail.append("<tr>").append("<th>Project</th><th>Pod</th><th>CPU %</th><th>Link Prometheus</th>").append("</tr>");
    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
    		for (OcpNamespace namespace: env.getNamespaces()) {
    			if (!env.getName().equalsIgnoreCase("others")) {
    				for (OcpPod pod : namespace.getOcpPods().values()) {
    					if (pod.getPercentageCurrentCpu().compareTo(clusterOcp.getAlertPercentageConsumptionResourceThreshold())>0) {
    						link = clusterOcp.getPrometheus_url()+"/graph?g0.range_input=2w&g0.stacked=0&g0.expr=pod%3Acontainer_cpu_usage%3Asum%7Bnamespace%3D%22"+namespace.getName()+"%22%2Cpod%3D%22"+pod.getName()+"%22%7D&g0.tab=0";
    						
    						txtMail.append("<tr><td>").append(namespace.getName())
    						     .append("</td><td>").append(pod.getName())
    						     .append("</td><td>").append(pod.getPercentageCurrentCpu())
    							 .append("</td><td><a href=\"").append(link).append("\">prometheus link</a></td></tr>");
    						
    						OcpAlertPodHightCpu alert= new OcpAlertPodHightCpu(namespace.getName(),link,pod.getName(),pod.getPercentageCurrentCpu().intValue());
    						clusterOcp.getAlertsPodHigthCpu().add(alert);
    					}	    							    			
    				}	    					    			
    			}
    		}
    	}
    	txtMail.append("</table>");
		return txtMail;
	}
	
	public static StringBuilder alertPodsWithHightMemoryConsumption(OcpCluster clusterOcp,StringBuilder txtMail) {
		String link;
		txtMail.append("<h2>Pods with hight memory consumption</h2>");
    	txtMail.append("Please, check the reason");
    	txtMail.append("<table>");
    	txtMail.append("<tr>").append("<th>Project</th><th>Pod</th><th>MEM %</th><th>Link Prometheus</th>").append("</tr>");
    	for (OcpEnvironment env:clusterOcp.getEnvironment().values()) {
    		for (OcpNamespace namespace: env.getNamespaces()) {
    			if (!env.getName().equalsIgnoreCase("others")) {
    				for (OcpPod pod : namespace.getOcpPods().values()) {
    					if (pod.getPercentageCurrentMemory().compareTo(clusterOcp.getAlertPercentageConsumptionResourceThreshold())>0) {
    						link = clusterOcp.getPrometheus_url()+"/graph?g0.range_input=2w&g0.stacked=0&g0.expr=pod%3Acontainer_memory_usage_bytes%3Asum%7Bnamespace%3D%22"+namespace.getName()+"%22%2Cpod%3D%22"+pod.getName()+"%22%7D&g0.tab=0";
    						txtMail.append("<tr><td>").append(namespace.getName())
    						     .append("</td><td>").append(pod.getName())
    						     .append("</td><td>").append(pod.getPercentageCurrentMemory())
    							 .append("</td><td><a href=\"").append(link).append("\">prometheus link</a></td></tr>");
    						OcpAlertPodHightMemory alert= new OcpAlertPodHightMemory(namespace.getName(),link,pod.getName(),pod.getPercentageCurrentCpu().intValue());
    						clusterOcp.getAlertsPodHightMemory().add(alert);
    					}	    							    			
    				}	    					    			
    			}
    		}
    	}
    	txtMail.append("</table>");
		return txtMail;
	}
}
