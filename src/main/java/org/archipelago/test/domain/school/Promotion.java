package org.archipelago.test.domain.school;

import org.archipelago.core.annotations.Island;

@Island
public class Promotion {

    private int year;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}