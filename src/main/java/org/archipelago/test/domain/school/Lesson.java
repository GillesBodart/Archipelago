package org.archipelago.test.domain.school;

import org.archipelago.core.annotations.Island;

@Island
public class Lesson {

    private String name;
    private int hourPerWeek;

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
