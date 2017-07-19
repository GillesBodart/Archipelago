package org.archipelago.core.builder;

import java.util.concurrent.locks.Condition;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public abstract class QueryBuilder {

    public static QueryBuilder init() {
        throw new UnsupportedOperationException("QueryBuilder is abstract and can not be implemented");
    }

    public abstract QueryBuilder getObject();

    public abstract QueryBuilder getId();

    public abstract QueryBuilder of(Class<?> element);

    public abstract QueryBuilder withId();

    public abstract QueryBuilder with(QueryElement element);

    public abstract QueryBuilder where(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract QueryBuilder and(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract QueryBuilder or(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract ArchipelagoQuery build();
}
