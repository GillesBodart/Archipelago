package org.archipelago.test.domain.school;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Lesson {


    private Integer id;

    private String name;
    private Long hourPerWeek;

    public Lesson() {
    }

    public Lesson(String name, Long hourPerWeek) {
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

    public Long getHourPerWeek() {
        return hourPerWeek;
    }

    public void setHourPerWeek(Long hourPerWeek) {
        this.hourPerWeek = hourPerWeek;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("hourPerWeek", hourPerWeek)
                .toString();
    }
}
