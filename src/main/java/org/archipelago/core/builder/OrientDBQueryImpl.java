package org.archipelago.core.builder;

import org.archipelago.core.util.ArchipelagoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public class OrientDBQueryImpl extends QueryBuilder {

    private StringBuilder pending = new StringBuilder();
    private Class<?> target;

    private List<String> elementToReturn = new ArrayList<>();

    public static QueryBuilder init() {
        return new OrientDBQueryImpl();
    }

    @Override
    public QueryBuilder getObject() {
        pending.append("SELECT ");
        return this;
    }

    @Override
    public QueryBuilder of(Class<?> clazz) {
        pending.append(String.format(" FROM %s ", clazz.getSimpleName()));
        this.target = clazz;
        ArchipelagoUtils.getAllFields(clazz).stream().forEach(field -> elementToReturn.add(field.getName()));
        return this;
    }



    @Override
    public QueryBuilder where(QueryElement element, ConditionQualifier conditionQualifier) {
        condition(element, conditionQualifier, "WHERE");
        return this;
    }

    @Override
    public QueryBuilder and(QueryElement element, ConditionQualifier conditionQualifier) {
        condition(element, conditionQualifier, "AND");
        return this;
    }

    @Override
    public QueryBuilder or(QueryElement element, ConditionQualifier conditionQualifier) {
        condition(element, conditionQualifier, "OR");
        return this;
    }

    private void condition(QueryElement element, ConditionQualifier conditionQualifier, String logicSym) {

        pending.append(String.format(" %s %s %s %s", logicSym, "" + element.getKey(), conditionQualifier.getSymbol(), ArchipelagoUtils.formatQueryValue(element.getValue())));
    }

    @Override
    public ArchipelagoQuery build() {
        return new ArchipelagoQuery(pending.toString(), elementToReturn, target, relation, from, to, descriptor, biDirectionnal);
    }
}
