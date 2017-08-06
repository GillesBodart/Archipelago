package org.archipelago.core.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Gilles Bodart on 23/07/2017.
 */
public class RelationWrapper {

    private String name;
    private Object to;
    private boolean biDirectionnal;

    public Object getTo() {
        return to;
    }

    public void setTo(Object to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBiDirectionnal() {
        return biDirectionnal;
    }

    public void setBiDirectionnal(boolean biDirectionnal) {
        this.biDirectionnal = biDirectionnal;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("to", to)
                .append("biDirectionnal", biDirectionnal)
                .toString();
    }
}
