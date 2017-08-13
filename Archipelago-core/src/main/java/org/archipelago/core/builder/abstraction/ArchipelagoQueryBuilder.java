package org.archipelago.core.builder.abstraction;

import org.archipelago.core.builder.commons.ArchipelagoQuery;
import org.archipelago.core.builder.commons.ConditionQualifier;
import org.archipelago.core.builder.commons.QueryElement;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public abstract class ArchipelagoQueryBuilder {

    public static ArchipelagoQueryBuilder init() {
        throw new UnsupportedOperationException("ArchipelagoQueryBuilder is abstract and can not be implemented");
    }

    public abstract ArchipelagoQueryBuilder of(Class<?> element);

    public abstract ArchipelagoQueryBuilder where(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract ArchipelagoQueryBuilder and(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract ArchipelagoQueryBuilder or(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract ArchipelagoQuery build();
}
