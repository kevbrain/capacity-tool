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
		
		//token="eyJhbGciOiJSUzI1NiIsImtpZCI6IjJ4N3YwTlB1NDFLRGxrQmF2aWNrdTdjcHpId1k5M1Bua3g1RFViMGdPZmsifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJjYXBhY2l0eS10b29sLWRldiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJjYXBhY2l0eS10b29sLXRva2VuLWM4OGt6Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImNhcGFjaXR5LXRvb2wiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJiNWMxOGJiZi02YjAwLTRhZTEtYTE4MC04NjRjYzhkZDM2MmQiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6Y2FwYWNpdHktdG9vbC1kZXY6Y2FwYWNpdHktdG9vbCJ9.O4g3Cq3hEtdzR7cxPEuBq3PUsFsLTVKQpYNsHIlOTt3PxclvyS5y-A40GYt1Wemrgbs68MYE_aHhIgJZcDu_ZJQANPpTsjiK0UCidItc-r7-aVGcjyKnNusywpgctuAaI0xgRAApNh1-U0ndeEDuy_2X5ECB3AaKyo6QRR_ovFxmk-IEKtVdiIaN8aaYSgGDhVMV6eE9xSokDU5GlSPvadcwroEbR3uEC90n1lpZ0hcWooZ7RrSHzF6OcGsNh9sQdrXEVkaw3YFTjPymyKnM71XkNj6Ql9KtlfBXWkDvFLrpg5a7VVfIaJdmX_cK3O3C0rLYMOu-zCMMjmjCfiO3IQ";
		
		Config config = new ConfigBuilder().withMasterUrl(server)
				.withDisableHostnameVerification(true)
				.withOauthToken("")
				.withTrustCerts(false)
				.build();
		
		this. client= new DefaultKubernetesClient(config);
		System.out.println("Kube version = "+client.getVersion());
	
    }
}
