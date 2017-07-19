package org.archipelago.test.domain.school;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archipelago.core.annotations.ArchipelId;
import org.archipelago.core.annotations.Island;

@Island
public class Lesson {

    @ArchipelId
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("hourPerWeek", hourPerWeek)
                .toString();
    }
}
