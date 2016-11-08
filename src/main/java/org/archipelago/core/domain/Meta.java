package org.archipelago.core.domain;

import java.util.Collection;
import java.util.Map;

import org.archipelago.core.interfaces.MetaElement;
import org.archipelago.core.interfaces.SimpleElement;
import org.archipelago.core.interfaces.UnderMetaElement;

/**
 * Created by Gilles Bodart on 18/08/2016.
 */
public class Meta<T extends UnderMetaElement> extends MetaControl implements MetaElement {

    private Collection<T> details;
    private Map<String, Collection<UnderMetaElement>> metaProperties;
    private Map<String, Collection<SimpleElement>> simplesProperties;
    private String name;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Collection<T> getDetails() {
        return details;
    }

    public void setDetails(final Collection<T> details) {
        this.details = details;
    }

    public Map<String, Collection<UnderMetaElement>> getMetaProperties() {
        return metaProperties;
    }

    public void setMetaProperties(final Map<String, Collection<UnderMetaElement>> metaProperties) {
        this.metaProperties = metaProperties;
    }

    public Map<String, Collection<SimpleElement>> getSimplesProperties() {
        return simplesProperties;
    }

    public void setSimplesProperties(final Map<String, Collection<SimpleElement>> simplesProperties) {
        this.simplesProperties = simplesProperties;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Meta)) {
            return false;
        }

        final Meta<?> meta = (Meta<?>) o;

        if (!getName().equals(meta.getName())) {
            return false;
        }
        return getId().equals(meta.getId());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getId().hashCode();
        return result;
    }
}
