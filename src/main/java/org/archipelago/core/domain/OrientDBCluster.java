package org.archipelago.core.domain;

/**
 * 
 * @author Gilles Bodart
 *
 */
public class OrientDBCluster {

    private Integer clusterAmount;
    private String clusterName;

    public OrientDBCluster() {

    }

    public OrientDBCluster(final Integer clusterAmount, final String clusterName) {
        super();
        this.clusterAmount = clusterAmount;
        this.clusterName = clusterName;
    }

    public Integer getClusterAmount() {
        return clusterAmount;
    }

    public void setClusterAmount(final Integer clusterAmount) {
        this.clusterAmount = clusterAmount;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(final String clusterName) {
        this.clusterName = clusterName;
    }

}
