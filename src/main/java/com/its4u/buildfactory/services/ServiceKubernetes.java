package com.its4u.buildfactory.services;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceKubernetes {
	
	private String server;
	
	private String token;
	
	private KubernetesClient client;
	
		

	public ServiceKubernetes(String server, String token) {
		super();
		this.server = server;
		this.token = token;
		connection(server,token);
	}

	public  PodList getPodsRunning(String name) {
		
		return this.client.pods().inNamespace(name).withField("status.phase", "Running").list();
		
	}
		
	public void connection(String server,String token) {
						
		Config config = new ConfigBuilder().withMasterUrl(server)
				.withDisableHostnameVerification(true)
				.withOauthToken(token)
				.withTrustCerts(false)
				.build();
		
		this. client= new DefaultKubernetesClient(config);
		System.out.println("Kube version = "+client.getVersion());
	
    }
}
