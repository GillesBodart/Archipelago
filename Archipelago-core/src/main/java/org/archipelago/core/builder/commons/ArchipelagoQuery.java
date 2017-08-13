package org.archipelago.core.builder.commons;

import java.util.List;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public class ArchipelagoQuery {

    private final String query;
    private final List<String> keys;
    private final Class<?> target;

    public ArchipelagoQuery(String query, List<String> keys, Class<?> target) {
        this.query = query;
        this.keys = keys;
        this.target = target;
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
