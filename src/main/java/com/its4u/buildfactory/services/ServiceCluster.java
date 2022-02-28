package com.its4u.buildfactory.services;

import java.math.BigDecimal;

import com.its4u.buildfactory.ocp.OcpCluster;
import com.its4u.buildfactory.ocp.OcpNamespace;
import com.its4u.buildfactory.ocp.OcpNode;
import com.its4u.buildfactory.ocp.RequestsAndLimits;

import io.fabric8.kubernetes.api.model.LimitRange;
import io.fabric8.kubernetes.api.model.LimitRangeItem;
import io.fabric8.kubernetes.api.model.LimitRangeList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class ServiceCluster {
	

	
	public static KubernetesClient connection(String server,String token) {
    			
		Config config = new ConfigBuilder().withMasterUrl(server)
				.withDisableHostnameVerification(true)
				.withOauthToken(token)
				.withTrustCerts(false)
				.build();
		
		return new DefaultKubernetesClient(config);
		
	
    }
	
	public static void loadWorkers(KubernetesClient client,OcpCluster clusterOcp) {
        NodeList nodeslist = client.nodes().withLabel("node-role.kubernetes.io/worker","").withoutLabel("node-role.kubernetes.io/infra","").withoutLabel("cluster.ocs.openshift.io/openshift-storage","").list();                       
        
        
        for (Node node: nodeslist.getItems()) { 
             
           NodeStatus nodeStatus = node.getStatus();
           String hostname = node.getMetadata().getLabels().get("kubernetes.io/hostname");
           OcpNode nodeOcp= new OcpNode(hostname);
           clusterOcp.getNodes().put(hostname,nodeOcp);
               
           nodeOcp.setAllocatable_cpu(new BigDecimal(nodeStatus.getAllocatable().get("cpu").getAmount()));    
           nodeOcp.setAllocatable_memory(new BigDecimal(nodeStatus.getAllocatable().get("memory").getAmount()).divide(new BigDecimal(1024)));
           
           clusterOcp.setCluster_cpu(clusterOcp.getCluster_cpu().add(nodeOcp.getAllocatable_cpu()));
           clusterOcp.setCluster_memory(clusterOcp.getCluster_memory().add(nodeOcp.getAllocatable_memory()));
                                         
           PodList podList = client.pods().inAnyNamespace().withField("spec.nodeName", hostname).withField("status.phase", "Running").list();
           nodeOcp.setPodList(podList.getItems());
           nodeOcp.setNbrPods(podList.getItems().size());
           
           analysePods(nodeOcp);
           
           PrometheusService.loadMetricsNodes(client,clusterOcp);
        }
        
    }
	
	
	public static boolean checkIfNamespaceProtectedByLimits(KubernetesClient client,OcpCluster clusterOcp,OcpNamespace namespaceOcp) {
    	
    	boolean protectedByLimitCpuAndMemory = false;
    	LimitRangeList limitRangeList = client.limitRanges().inNamespace(namespaceOcp.getName()).list();
		for (LimitRange limitRange: limitRangeList.getItems()) {
			
			for (LimitRangeItem limitRangeItem :limitRange.getSpec().getLimits()) {
		
				if (    limitRangeItem.getDefault()!=null && limitRangeItem.getDefaultRequest()!=null 
						&& limitRangeItem.getDefault().get("memory")!=null && limitRangeItem.getDefaultRequest().get("memory")!=null
						&& limitRangeItem.getDefault().get("cpu")!=null && limitRangeItem.getDefaultRequest().get("cpu")!= null ) {
					return true;
				}
			}
		}
    	return protectedByLimitCpuAndMemory;
    }
	
	public static  void analysePods(OcpNode ocpnode) {
    	for (Pod pod: ocpnode.getPodList()) {
            for (io.fabric8.kubernetes.api.model.Container container: pod.getSpec().getContainers()) {
            	ResourceRequirements resources = container.getResources();
            	RequestsAndLimits reqLim = ServiceRequestsAndLimits.extractResourceRequirementsToRequestAndLimits(resources);
            	ocpnode.setLimits_cpu(ocpnode.getLimits_cpu().add(reqLim.getLim_cpu()));
            	ocpnode.setLimits_memory(ocpnode.getLimits_memory().add(reqLim.getLim_memory()));
            	ocpnode.setRequests_cpu(ocpnode.getRequests_cpu().add(reqLim.getReq_cpu()));
            	ocpnode.setRequests_memory(ocpnode.getRequests_memory().add(reqLim.getReq_memory()));
                }
          }
    }
		
}
