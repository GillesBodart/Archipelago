package org.archipelago.test.domain.library;

public abstract class Person {

    private String name;
    private String lastName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Person() {

    }

    public Person(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

}
