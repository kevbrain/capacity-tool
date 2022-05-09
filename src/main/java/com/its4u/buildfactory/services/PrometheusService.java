package com.its4u.buildfactory.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.its4u.buildfactory.ocp.OcpCluster;
import com.its4u.buildfactory.ocp.OcpNamespace;
import com.its4u.buildfactory.ocp.OcpPod;

import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.client.KubernetesClient;


public class PrometheusService {
	
	private static Logger logger = LoggerFactory.getLogger(PrometheusService.class);

	public static void loadMetricsNodes(KubernetesClient client,OcpCluster clusterOcp) {
    	
        List <NodeMetrics> nodemetricsList = client.top().nodes().metrics().getItems();
        for (NodeMetrics nodeMetrics : nodemetricsList) {
        	
        	if (clusterOcp.getNodes().get(nodeMetrics.getMetadata().getName())!=null) {
        		clusterOcp.getNodes().get(nodeMetrics.getMetadata().getName()).setCurrent_cpu(new BigDecimal(nodeMetrics.getUsage().get("cpu").getAmount()));
        		clusterOcp.getNodes().get(nodeMetrics.getMetadata().getName()).setCurrent_memory(new BigDecimal(nodeMetrics.getUsage().get("memory").getAmount()).divide(new BigDecimal(1024)));
        	}
        	
        }
    }
	
    public static BigDecimal getMaxConsumptionLast2wOfService(OcpCluster clusterOcp,OcpNamespace namespaceOcp ,OcpPod pod,String type) {
    	
    	BigDecimal maxConsumptionLast2w = BigDecimal.ZERO ;               
	    	URL url;
			try {
				
				String stringUrl=clusterOcp.getPrometheus_url()+"/api/v1/query?query=max(max_over_time(pod:container_"+type+":sum{namespace=\""+namespaceOcp.getName()+"\",pod=\""+pod.getName()+"\"}["+clusterOcp.getPrometheus_query_maxOverTime()+"]))";				
				url = new URL(stringUrl);
				HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
		    	conn.setRequestProperty("Authorization","Bearer "+clusterOcp.getToken());
		    	conn.setRequestProperty("Content-type","application/json");
		    	conn.setRequestMethod("GET");
		    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        String output;
		
		        StringBuffer response = new StringBuffer();
		        while ((output = in.readLine()) != null) {
		            response.append(output);
		        }
		
		        in.close();
		        
		        String jsonString = response.toString() ; 
		        JSONObject obj = new JSONObject(jsonString);
		        JSONArray data = (JSONArray) ((JSONObject)obj.get("data")).get("result");     
		        JSONObject result = (JSONObject)data.get(0);       
		        JSONArray values = (JSONArray)result.get("value");      
		        BigDecimal val = new BigDecimal(values.getString(1));
		        if (type.equalsIgnoreCase("memory_usage_bytes")) {
		        	maxConsumptionLast2w = val.divide(new BigDecimal("1024")).divide(new BigDecimal("1024")).setScale(0, BigDecimal.ROUND_HALF_UP);
		        	
		        } else {
		        	maxConsumptionLast2w = val.multiply(new BigDecimal("1000")).setScale(0, BigDecimal.ROUND_HALF_UP);
		        	
		        }	
			} catch (MalformedURLException e) {				
				logger.error(e.getMessage());
			} catch (ProtocolException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}    
			  
			return maxConsumptionLast2w;
    }
  
}
