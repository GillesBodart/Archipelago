package org.archipelago.core.configurator;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ABM589 on 18/07/2017.
 */
public class ArchipelagoConfig {

    @JsonProperty()
    private DatabaseConfig database;

    @JsonProperty
    private Integer deepness;

    @JsonProperty
    private String domainRootPackage;

    public ArchipelagoConfig() {
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public Integer getDeepness() {
        return deepness;
    }

    public void setDeepness(Integer deepness) {
        this.deepness = deepness;
    }

    public String getDomainRootPackage() {
        return domainRootPackage;
    }

    public void setDomainRootPackage(String domainRootPackage) {
        this.domainRootPackage = domainRootPackage;
    }
}
