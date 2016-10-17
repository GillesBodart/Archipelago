package org.archipelago.core.domain;

public class OrientDBCluster {

	private Integer clusterAmount;
	private String clusterName;
	
	public OrientDBCluster() {
		
	}
		
	
	public OrientDBCluster(Integer clusterAmount, String clusterName) {
		super();
		this.clusterAmount = clusterAmount;
		this.clusterName = clusterName;
	}
	public Integer getClusterAmount() {
		return clusterAmount;
	}
	public void setClusterAmount(Integer clusterAmount) {
		this.clusterAmount = clusterAmount;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	
	
}
