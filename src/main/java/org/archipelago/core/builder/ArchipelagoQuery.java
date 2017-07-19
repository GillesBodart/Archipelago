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

    public ArchipelagoQuery(String query, List<String> keys, Class<?> target,boolean withId) {
        this.query = query;
        this.keys = keys;
        this.target = target;
        this.withId = withId;
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
