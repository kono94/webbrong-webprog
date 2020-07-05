package de.hsbremerhaven.pongservice.domain;

public class RoomEntryErrorMessage {
    private Event event = Event.ROOM_ENTRY_ERROR_MESSAGE;
    private String message;

    public Event getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
