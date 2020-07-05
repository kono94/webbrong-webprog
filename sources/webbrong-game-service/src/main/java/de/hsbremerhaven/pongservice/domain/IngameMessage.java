package de.hsbremerhaven.pongservice.domain;

public class IngameMessage {
    private Event event = Event.INGAME_MESSAGE;
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
