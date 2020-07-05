package de.hsbremerhaven.pongservice.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableScheduling
@EnableWebSocket
public class WebSocketServerConfiguration implements WebSocketConfigurer {

  @Value("${gamemode}")
  private HandlerType handlerType;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    MyWebSocketHandler webSocketHandler = new MyWebSocketHandler(handlerType);
    String endpoint;
    switch (handlerType){
      case PONG:
        endpoint = "/pong-room/*";
        break;
      case BREAKOUT:
        endpoint = "/breakout-room/*";
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + handlerType);
    }
    registry.addHandler(webSocketHandler, endpoint)
      .addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*");
  }
}
