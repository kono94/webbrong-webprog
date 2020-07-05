package de.hsbremerhaven.pongservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.hsbremerhaven.pongservice.domain.MovementMessage;
import de.hsbremerhaven.pongservice.domain.NewPaddleDrawn;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface GameEngineRunnable extends Runnable {
  void addNewPlayer(WebSocketSession session) throws IOException;
  void removePlayer(WebSocketSession session);
  void removeSpectator(WebSocketSession session);
  void changePlayerMovement(WebSocketSession session, MovementMessage message);
  void newCustomPaddle(WebSocketSession session, NewPaddleDrawn p);
  void resetCustomPaddlePosition(WebSocketSession session);
  int getPlayerCount();
  void startNewMatch(WebSocketSession session);
    void setAdmin(String username);
  void stop();
}
