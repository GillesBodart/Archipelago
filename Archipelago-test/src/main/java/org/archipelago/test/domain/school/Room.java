package org.archipelago.test.domain.school;

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
