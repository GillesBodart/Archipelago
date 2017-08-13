package org.archipelago.core.builder.commons;

/**
 * Created by Gilles Bodart on 19/07/2017.
 */
public enum ConditionQualifier {
    EQUAL("="), NOT_EQUAL("!="), LESS_THAN("<="), MORE_THAN(">="), STRICT_LESS_THAN("<"), STRICT_MORE_THAN(">");

    private String symbol;

    private ConditionQualifier(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
