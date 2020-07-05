package de.hsbremerhaven.pongservice.domain;

import de.hsbremerhaven.pongservice.domain.ball.Ball;
import de.hsbremerhaven.pongservice.domain.Player.Player;
import de.hsbremerhaven.pongservice.domain.obstacle.Obstacle;

import java.util.Map;
import java.util.Set;

public class GameState {
    private Event event = Event.GAME_TICK;
    private Map<String, Player> players;
    private Ball ball;
    private Playground playground;
    private Set<Obstacle> obstacles;
    private String admin;
    private Set<String> spectators;

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers( Map<String, Player> players) {
        this.players = players;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public Playground getPlayground() {
        return playground;
    }

    public void setPlayground(Playground playground) {
        this.playground = playground;
    }

    public Set<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(Set<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public Event getEvent() {
        return event;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public Set<String> getSpectators() {
        return spectators;
    }

    public void setSpectators(Set<String> spectators) {
        this.spectators = spectators;
    }
}
