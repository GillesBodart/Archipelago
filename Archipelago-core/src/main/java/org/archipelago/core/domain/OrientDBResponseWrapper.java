package org.archipelago.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Gilles Bodart on 21/07/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrientDBResponseWrapper {

    @JsonProperty("@type")
    private String type;
    @JsonProperty("@rid")
    private String id;
    @JsonProperty("@version")
    private String version;
    @JsonProperty("@class")
    private String className;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
