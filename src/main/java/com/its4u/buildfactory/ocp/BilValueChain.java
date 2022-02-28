package com.its4u.buildfactory.ocp;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BilValueChain {

	private String vcName;
	
	private List<OcpNamespace> namespaces;

	public BilValueChain(String vcName, List<OcpNamespace> namespaces) {
		super();
		this.vcName = vcName;
		this.namespaces = namespaces;
	}
	
	
}
