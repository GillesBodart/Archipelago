package org.archipelago.core.domain;

import org.archipelago.core.annotations.Bridge;

import java.time.LocalDate;

/**
 * Created by Gilles Bodart on 23/07/2017.
 */
public class DescriptorWrapper {

    @Bridge(descriptor = "")
    private String name;
    private LocalDate created;

    public DescriptorWrapper() {
        this.created = LocalDate.now();
    }

    public DescriptorWrapper(String descriptorName) {
        this();
        this.name = descriptorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }
}
