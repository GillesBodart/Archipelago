package org.archipelago.core.builder;

import org.archipelago.core.util.ArchipelagoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public class Neo4JQueryImpl extends QueryBuilder {

    private StringBuilder pending = new StringBuilder();
    private Class<?> target;

    private List<String> elementToReturn = new ArrayList<>();

    public static QueryBuilder init() {
        return new Neo4JQueryImpl();
    }

    @Override
    public QueryBuilder getObject() {
        pending.append("MATCH ");
        return this;
    }

    @Override
    public QueryBuilder of(Class<?> clazz) {
        pending.append(String.format("p=(n:%s)-[rel]-() ", clazz.getSimpleName()));
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
        Object formatedValue = ArchipelagoUtils.formatQueryValue(element.getValue());
        pending.append(String.format(" %s %s %s %s",
                logicSym,
                "n." + element.getKey(),
                conditionQualifier.getSymbol(),
                element.getValue() instanceof String ? "\"" + formatedValue + "\"" : formatedValue));
    }

    @Override
    public ArchipelagoQuery build() {
        pending.append(" RETURN p");
        return new ArchipelagoQuery(pending.toString(), elementToReturn, target, relation, from, to, descriptor, biDirectionnal);
    }
}
