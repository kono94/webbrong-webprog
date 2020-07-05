package de.hsbremerhaven.pongservice.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

public class MyHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    String path = request.getURI().getPath();
    int index = path.lastIndexOf("/");
    String[] params = path.substring(index+1).split(":");
    attributes.put("roomID", params[0]);
    attributes.put("username", params[1]);
    attributes.put("customPaddle", Boolean.parseBoolean(params[2]));
    attributes.put("isSpectating", Boolean.parseBoolean(params[3]));
    return true;
  }
}
