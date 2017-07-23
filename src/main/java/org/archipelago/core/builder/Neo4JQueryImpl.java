package org.archipelago.core.builder;

import org.archipelago.core.connection.Archipelago;
import org.archipelago.core.util.ArchipelagoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public class Neo4JQueryImpl extends QueryBuilder {

    private StringBuilder pending = new StringBuilder();
    private Class<?> target;
    private boolean creation = false;
    private boolean wantObject = false;
    private boolean wantId = false;


    private List<QueryElement> conditionElements = new ArrayList<>();

    private List<String> elementToReturn = new ArrayList<>();

    public static QueryBuilder init() {
        return new Neo4JQueryImpl();
    }

    @Override
    public QueryBuilder getObject() {
        this.wantObject = true;
        pending.append("MATCH ");
        return this;
    }


    @Override
    public QueryBuilder getId() {
        this.wantId = true;
        pending.append("MATCH ");
        return this;
    }

    @Override
    public QueryBuilder of(Class<?> clazz) {
        pending.append(String.format("(n:%s) ", clazz.getSimpleName()));
        this.target = clazz;
        ArchipelagoUtils.getAllFields(clazz).stream().forEach(field -> elementToReturn.add(field.getName()));
        return this;
    }

    @Override
    public QueryBuilder withId() {
        this.wantId = true;
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

        pending.append(String.format("\n\t%s %s %s %s", logicSym,
                element.isId() ? "ID(n)" : "n." + element.getKey(), conditionQualifier.getSymbol(), ArchipelagoUtils.formatQueryValue(element.getValue())));
    }

    @Override
    public ArchipelagoQuery build() {
        pending.append("\nRETURN");
        if (wantObject) {
            elementToReturn.stream().forEach(element -> pending.append(String.format("\n\tn.%s AS %s,", element, element)));
        }
        if (wantId) {
            elementToReturn.add(Archipelago.ARCHIPELAGO_ID);
            pending.append(String.format("\n\tID(n) as %s", Archipelago.ARCHIPELAGO_ID));
        } else {
            //remove the last ",\n"
            pending.setLength(pending.length() - 1);
        }
        return new ArchipelagoQuery(pending.toString(), elementToReturn, target, wantId, relation, from, to, descriptor, biDirectionnal);
    }
}
