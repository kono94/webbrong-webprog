package de.hsbremerhaven.pongservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsbremerhaven.pongservice.domain.EventDummy;
import de.hsbremerhaven.pongservice.domain.MovementMessage;
import de.hsbremerhaven.pongservice.domain.NewPaddleDrawn;
import de.hsbremerhaven.pongservice.engine.BreakoutEngine;
import de.hsbremerhaven.pongservice.engine.GameEngine;
import de.hsbremerhaven.pongservice.engine.PongEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class MyWebSocketHandler extends TextWebSocketHandler {

  private static final Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);
  private Map<String, GameEngineRunnable> openRooms = new HashMap<>();
  protected HandlerType type;

  public MyWebSocketHandler(HandlerType type) {
    this.type = type;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    String roomID = (String) session.getAttributes().get("roomID");
    String userName = (String) session.getAttributes().get("username");
    if (!openRooms.containsKey(roomID)) {
      openRooms.put(roomID, createSpecificEngine(roomID));
      new Thread(openRooms.get(roomID)).start();
      openRooms.get(roomID).setAdmin(userName);

    }
    openRooms.get(roomID).addNewPlayer(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
      if((Boolean) session.getAttributes().get("isSpectating")){
          return;
      }
      GameEngineRunnable room  = openRooms.get(session.getAttributes().get("roomID"));
      System.out.println(message.getPayload());
      switch (((new ObjectMapper().readValue(message.getPayload(), EventDummy.class))).getEvent()){
          case PADDLE_MOVEMENT:
              room.changePlayerMovement(session,
                      new ObjectMapper().readValue(message.getPayload(), MovementMessage.class));
              break;
          case NEW_CUSTOM_PADDLE:
              room.newCustomPaddle(session,
                      new ObjectMapper().readValue(message.getPayload(), NewPaddleDrawn.class));
              break;
          case RESET_CUSTOM_PADDLE:
              room.resetCustomPaddlePosition(session);
              break;
          case START_MATCH:
              room.startNewMatch(session);
              break;
          default:
              break;
      }
  }


  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    String roomID = (String) session.getAttributes().get("roomID");
    String username = (String) session.getAttributes().get("username");

    if (username != null) {
            logger.info("User: {} disconnected from room: {}", username, roomID);
            if((Boolean) session.getAttributes().get("isSpectating")){
                openRooms.get(roomID).removeSpectator(session);
            }else{
                openRooms.get(roomID).removePlayer(session);
                if (openRooms.get(roomID).getPlayerCount() == 0) {
                    openRooms.get(roomID).stop();
                    openRooms.remove(roomID);
                    RestHandler.getInstance().removeGameInstanceInfo(type.toString(), roomID);
                }
            }
        }
  }
  private GameEngine createSpecificEngine(String roomID){
    switch (type) {
      case PONG:
        return new PongEngine(roomID);
      case BREAKOUT:
        return new BreakoutEngine(roomID);
    }
    return null;
  }
}
