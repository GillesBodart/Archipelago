package org.archipelago.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by Gilles Bodart on 21/07/2017.
 */
public class OrientDBResultWrapper {

    @JsonProperty
    private List<Map<String, Object>> result;

    public OrientDBResultWrapper() {
        this.result = result;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }
}
