package com.its4u.buildfactory.ocp;

public abstract class OcpAlert {
	
	private String namespaceName;
	
	private String link;

	public OcpAlert(String namespaceName, String link) {
		super();
		this.namespaceName = namespaceName;
		this.link = link;
	}

	public String getNamespaceName() {
		return namespaceName;
	}

	public void setNamespaceName(String namespaceName) {
		this.namespaceName = namespaceName;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	
	
}
