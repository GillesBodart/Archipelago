package org.archipelago.core.builder.OrientDB;

import org.archipelago.core.builder.commons.ArchipelagoQuery;
import org.archipelago.core.builder.abstraction.ArchipelagoQueryBuilder;
import org.archipelago.core.builder.commons.ConditionQualifier;
import org.archipelago.core.builder.commons.QueryElement;
import org.archipelago.core.util.ArchipelagoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public class OrientDBArchipelagoQueryImpl extends ArchipelagoQueryBuilder {

    private StringBuilder pending = new StringBuilder();
    private Class<?> target;

    private List<String> elementToReturn = new ArrayList<>();

    public static ArchipelagoQueryBuilder init() {
        OrientDBArchipelagoQueryImpl impl = new OrientDBArchipelagoQueryImpl();
        impl.pending.append("SELECT ");
        return impl;
    }

    @Override
    public ArchipelagoQueryBuilder of(Class<?> clazz) {
        pending.append(String.format(" FROM %s ", clazz.getSimpleName()));
        this.target = clazz;
        ArchipelagoUtils.getAllFields(clazz).stream().forEach(field -> elementToReturn.add(field.getName()));
        return this;
    }


    @Override
    public ArchipelagoQueryBuilder where(QueryElement element, ConditionQualifier conditionQualifier) {
        condition(element, conditionQualifier, "WHERE");
        return this;
    }

    @Override
    public ArchipelagoQueryBuilder and(QueryElement element, ConditionQualifier conditionQualifier) {
        condition(element, conditionQualifier, "AND");
        return this;
    }

    @Override
    public ArchipelagoQueryBuilder or(QueryElement element, ConditionQualifier conditionQualifier) {
        condition(element, conditionQualifier, "OR");
        return this;
    }

    private void condition(QueryElement element, ConditionQualifier conditionQualifier, String logicSym) {

        pending.append(String.format(" %s %s %s %s",
                logicSym,
                "" + element.getKey(),
                conditionQualifier.getSymbol(),
                ArchipelagoUtils.formatQueryValue(element.getValue(), true, true)));
    }

    @Override
    public ArchipelagoQuery build() {
        return new ArchipelagoQuery(pending.toString(), elementToReturn, target);
    }
}
