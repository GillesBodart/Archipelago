package org.archipelago.test.domain.school;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ClassRoom extends Room {


    private Integer id;

    private Integer availableSeat;

    private Boolean television;
    private Boolean computer;
    private Boolean beamer;

    public ClassRoom() {
    }

    /**
     * @param roomName
     * @param availableSeat
     * @param television
     * @param computer
     * @param beamer
     */
    public ClassRoom(String roomName, Integer availableSeat, Boolean television, Boolean computer, Boolean beamer) {
        super(roomName);
        this.availableSeat = availableSeat;
        this.television = television;
        this.computer = computer;
        this.beamer = beamer;
    }

    public Integer getAvailableSeat() {
        return availableSeat;
    }

    public void setAvailableSeat(Integer availableSeat) {
        this.availableSeat = availableSeat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isTelevision() {
        return television;
    }

    public void setTelevision(Boolean television) {
        this.television = television;
    }

    public Boolean isComputer() {
        return computer;
    }

    public void setComputer(Boolean computer) {
        this.computer = computer;
    }

    public Boolean isBeamer() {
        return beamer;
    }

    public void setBeamer(Boolean beamer) {
        this.beamer = beamer;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("availableSeat", availableSeat)
                .append("television", television)
                .append("computer", computer)
                .append("beamer", beamer)
                .toString();
    }
}
