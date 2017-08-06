package org.archipelago.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Gilles Bodart on 21/07/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrientDBRelationWrapper {

    @JsonProperty("@type")
    private String type;
    @JsonProperty("@rid")
    private String id;
    @JsonProperty("@version")
    private String version;
    @JsonProperty("in")
    private String in;
    @JsonProperty("out")
    private String out;

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

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
