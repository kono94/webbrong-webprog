package de.hsbhv.gamegateway.data.rest.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/eureka")
public class EurekaInfoController {
    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient eurekaClient;

    private final Map<String, InstanceInfo> pongRoomServiceMap = new HashMap<>();
    private final Map<String, InstanceInfo> breakoutRoomServiceMap = new HashMap<>();

    @RequestMapping("instanceInfo/{appName}")
    public InstanceInfo getInstanceInfo(@PathVariable String appName) {
        return eurekaClient.getNextServerFromEureka(appName, false);
    }

    @RequestMapping("gameInstanceInfo/{appName}/{roomId}")
    public InstanceInfo getGameInstanceInfo(@PathVariable String appName, @PathVariable String roomId){
        InstanceInfo instanceInfo = null;
        if (appName.toLowerCase().equals("webbrong-pong")){
            instanceInfo = pongRoomServiceMap.getOrDefault(roomId, null);
            if (instanceInfo == null){
                instanceInfo = eurekaClient.getNextServerFromEureka(appName, false);
                pongRoomServiceMap.put(roomId, instanceInfo);
            }
        }else if (appName.toLowerCase().equals("webbrong-breakout")){
            instanceInfo = breakoutRoomServiceMap.getOrDefault(roomId, null);
            if (instanceInfo == null){
                instanceInfo = eurekaClient.getNextServerFromEureka(appName, false);
                breakoutRoomServiceMap.put(roomId, instanceInfo);
            }
        }
        return instanceInfo;
    }

    @RequestMapping("removeGameInstanceInfo/{appName}/{roomId}")
    public InstanceInfo removeGameInstanceInfo(@PathVariable String appName, @PathVariable String roomId){
        InstanceInfo instanceInfo = null;
        if (appName.toLowerCase().equals("pong")){
            instanceInfo = pongRoomServiceMap.remove(roomId);
        }else if (appName.toLowerCase().equals("breakout")){
            instanceInfo = breakoutRoomServiceMap.remove(roomId);
        }
        return instanceInfo;
    }
}
