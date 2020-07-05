package de.hsbremerhaven.pongservice.config;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class RestHandler {
    private static RestHandler instance;
    private String token = "";
    private String username;
    private String password;
    private String appName;

    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient eurekaClient;

    private RestHandler(@Value("${api.username}") String username,
                        @Value("${api.password}") String password,
                        @Value("${api.appName}") String appName) {
        this.username = username;
        this.password = password;
        this.appName = appName;
        instance = this;
    }

    public static RestHandler getInstance() {
        return instance;
    }

    private InstanceInfo getEurekaInstance() {
        InstanceInfo instanceInfo = null;

        try {
            instanceInfo = eurekaClient.getNextServerFromEureka(appName, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instanceInfo;
    }

    private void authorize() {
        InstanceInfo instanceInfo = getEurekaInstance();

        try {
            assert instanceInfo != null;
            String url = instanceInfo.getHomePageUrl() + String.format("api/token?username=%s&password=%s", username, password);
            System.out.println(url);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ApiResponse> resp = restTemplate.getForEntity(url, ApiResponse.class);
            if (resp.getStatusCode() == HttpStatus.OK) {
                token = resp.getHeaders().get("Authorization").get(0);
                System.out.println("token: " + token);
            } else {
                System.out.printf("failed login for %s at %s", username, appName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAuthorized() {
        //TODO: maybe parse JWT? validity time is contained in the bearer token
        return token.length() > 0;
    }

    private String sendGetRequest(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        //FIXME: we should use POST/PUT instead of GET for changing data, fix this in webbrong-orchestrator
        ResponseEntity response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return (String) response.getBody();
    }

    public void addPointForPlayer(int matchId, String username, int points) {
        if (!isAuthorized())
            authorize();
        try {
            InstanceInfo instanceInfo = getEurekaInstance();
            String url = instanceInfo.getHomePageUrl() + String.format("event/userScored/%s/%s/%s", matchId, username, points);
            System.out.println(url);
            sendGetRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String startMatch(String matchType, String usernames) throws IOException {
        if (!isAuthorized())
            authorize();
        InstanceInfo instanceInfo = getEurekaInstance();
        String url = instanceInfo.getHomePageUrl() + String.format("event/newMatch/%s/%s", matchType, usernames);
        System.out.println(url);
        return sendGetRequest(url);
    }

    public void endMatch(int matchId) {
        if (!isAuthorized())
            authorize();
        try {
            InstanceInfo instanceInfo = getEurekaInstance();
            String url = instanceInfo.getHomePageUrl() + String.format("event/matchFinished/%s", matchId);
            System.out.println(url);
            sendGetRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelMatch(int matchId) {
        if (!isAuthorized())
            authorize();
        try {
            InstanceInfo instanceInfo = getEurekaInstance();
            String url = instanceInfo.getHomePageUrl() + String.format("event/matchCancelled/%s", matchId);
            System.out.println(url);
            sendGetRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeGameInstanceInfo(String appName, String roomId) {
        try {
            InstanceInfo instanceInfo = getEurekaInstance();
            String url = instanceInfo.getHomePageUrl() + String.format("eureka/removeGameInstanceInfo/%s/%s", appName, roomId);
            System.out.println(url);
            sendGetRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
