package org.archipelago.test.domain.school;

import org.archipelago.core.annotations.Island;

@Island
public class Lesson {

    private String name;
    private int hourPerWeek;

    public Lesson() {
    }
    public Lesson(String name, int hourPerWeek) {
        super();
        this.name = name;
        this.hourPerWeek = hourPerWeek;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHourPerWeek() {
        return hourPerWeek;
    }

    public void setHourPerWeek(int hourPerWeek) {
        this.hourPerWeek = hourPerWeek;
    }

}
