package org.archipelago.test.domain.school;

import org.archipelago.core.annotations.Archipel;

@Archipel
public class ClassRoom extends Room {

    private int availableSeat;

    private boolean television;
    private boolean computer;
    private boolean beamer;

    public int getAvailableSeat() {
        return availableSeat;
    }

    public void setAvailableSeat(int availableSeat) {
        this.availableSeat = availableSeat;
    }

    public boolean isTelevision() {
        return television;
    }

    public void setTelevision(boolean television) {
        this.television = television;
    }

    public boolean isComputer() {
        return computer;
    }

    public void setComputer(boolean computer) {
        this.computer = computer;
    }

    public boolean isBeamer() {
        return beamer;
    }

    public void setBeamer(boolean beamer) {
        this.beamer = beamer;
    }

}
