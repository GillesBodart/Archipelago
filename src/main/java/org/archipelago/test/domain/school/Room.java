package org.archipelago.test.domain.school;

import org.archipelago.core.annotations.Continent;

@Continent
public abstract class Room {

    private String roomName;

    public Room() {
    }

    public Room(String roomName) {
        super();
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

}
