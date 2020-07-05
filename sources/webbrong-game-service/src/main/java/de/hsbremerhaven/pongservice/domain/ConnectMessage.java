package de.hsbremerhaven.pongservice.domain;

public class ConnectMessage {
    private String username;
    private boolean usingCustomPaddle;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isUsingCustomPaddle() {
        return usingCustomPaddle;
    }

    public void setUsingCustomPaddle(boolean usingCustomPaddle) {
        this.usingCustomPaddle = usingCustomPaddle;
    }
}
