package com.its4u.buildfactory.ocp;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectValueChain {

	private String vcName;
	
	private List<OcpNamespace> namespaces;

	public ProjectValueChain(String vcName, List<OcpNamespace> namespaces) {
		super();
		this.vcName = vcName;
		this.namespaces = namespaces;
	}
	
	
}
