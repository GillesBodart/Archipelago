package org.archipelago.core.configurator;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ABM589 on 18/07/2017.
 */
public class DatabaseConfig {

    @JsonProperty
    private DatabaseType type;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private String url;

    @JsonProperty
    private int port;

    @JsonProperty
    private Boolean embedded;

    @JsonProperty
    private String name;

    public DatabaseConfig() {
    }

    public DatabaseType getType() {
        return type;
    }

    public void setType(DatabaseType type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getEmbedded() {
        return embedded;
    }

    public void setEmbedded(Boolean embedded) {
        this.embedded = embedded;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
