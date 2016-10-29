package org.archipelago.test.domain;

import java.util.List;

public class Form {
    private String test;
    private ClassOne c1;

    private List<String> names;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public ClassOne getC1() {
        return c1;
    }

    public void setC1(ClassOne c1) {
        this.c1 = c1;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

}
