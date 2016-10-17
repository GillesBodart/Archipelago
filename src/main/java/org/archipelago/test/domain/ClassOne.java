package org.archipelago.test.domain;

import java.util.List;

import org.archipelago.core.annotations.Island;

/**
 * Created by GJULESGB on 19/08/2016.
 */
@Island(tableName = "ISLAND")
public class ClassOne {

    private List<ClassTwo> parts;

    private Long id;

    public List<ClassTwo> getParts() {
        return parts;
    }

    public void setParts(List<ClassTwo> parts) {
        this.parts = parts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
