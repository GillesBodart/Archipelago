package org.archipelago.core.builder;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public abstract class QueryBuilder {


    protected boolean relation = false;
    protected Object from;
    protected Object to;
    protected Object descriptor;
    protected boolean biDirectionnal = false;

    public static QueryBuilder init() {
        throw new UnsupportedOperationException("QueryBuilder is abstract and can not be implemented");
    }


    public QueryBuilder linkObjects() {
        this.relation = true;
        return this;
    }

    public QueryBuilder from(Object from) {
        this.from = from;
        return this;
    }

    public QueryBuilder to(Object to) {
        this.to = to;
        return this;
    }

    public QueryBuilder biDirectionnal() {
        this.biDirectionnal = biDirectionnal;
        return this;
    }

    public abstract QueryBuilder getObject();

    public QueryBuilder with(Object descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public abstract QueryBuilder of(Class<?> element);

    public abstract QueryBuilder where(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract QueryBuilder and(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract QueryBuilder or(QueryElement element, ConditionQualifier conditionQualifier);

    public abstract ArchipelagoQuery build();
}
