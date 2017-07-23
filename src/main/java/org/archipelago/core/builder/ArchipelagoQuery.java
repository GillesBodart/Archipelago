package org.archipelago.core.builder;

import java.util.List;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public class ArchipelagoQuery {

    private final String query;
    private final List<String> keys;
    private final Class<?> target;
    private final boolean withId;

    private final boolean relation;
    private final Object from;
    private final Object to;
    private final Object descriptor;
    private final boolean biDirectionnal;

    public ArchipelagoQuery(String query, List<String> keys, Class<?> target, boolean withId, boolean relation, Object from, Object to, Object descriptor, boolean biDirectionnal) {
        this.query = query;
        this.keys = keys;
        this.target = target;
        this.withId = withId;
        this.from = from;
        this.to = to;
        this.relation = relation;
        this.descriptor = descriptor;
        this.biDirectionnal = biDirectionnal;
    }

    public String getQuery() {
        return query;
    }

    public List<String> getKeys() {
        return keys;
    }

    public Class<?> getTarget() {
        return target;
    }

    public boolean isWithId() {
        return withId;
    }

    public boolean isRelation() {
        return relation;
    }

    public Object getFrom() {
        return from;
    }

    public Object getTo() {
        return to;
    }

    public Object getDescriptor() {
        return descriptor;
    }

    public boolean isBiDirectionnal() {
        return biDirectionnal;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
