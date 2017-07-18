package org.archipelago.core.configurator;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ABM589 on 18/07/2017.
 */
public class ArchipelagoConfig {

    @JsonProperty()
    private DatabaseConfig database;

    public ArchipelagoConfig() {
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }
}
