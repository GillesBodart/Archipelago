package org.archipelago.core.builder;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public class QueryElement {
    private final String key;
    private final Object value;
    private final Boolean id;

    private QueryElement(String key, Object value, Boolean id) {
        this.key = key;
        this.value = value;
        this.id = id;
    }

    public static QueryElement qualifier(String qualifier) {
        QueryElement qe = new QueryElement(qualifier, "", false);
        return qe;
    }

    public static QueryElement of(String key, Object value) {
        QueryElement qe = new QueryElement(key, value, false);
        return qe;
    }

    public static QueryElement ofId(Object value) {
        QueryElement qe = new QueryElement("", value, true);
        return qe;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public Boolean isId() {
        return id;
    }
}
