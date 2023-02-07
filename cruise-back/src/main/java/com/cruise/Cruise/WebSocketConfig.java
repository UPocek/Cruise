package com.cruise.Cruise;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.ride.Controllers.AndroidRideRequestController;
import com.cruise.Cruise.ride.Services.IFindDriverService;
import com.cruise.Cruise.ride.Services.IRideRequestService;
import com.cruise.Cruise.user.Controllers.AndroidChatController;
import com.cruise.Cruise.vehicle.Controllers.AndroidVehicleController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IRideRequestService rideRequestService;
    @Autowired
    private IFindDriverService findDriverService;
    @Autowired
    private IDriverRepository driverRepository;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket", "/socket/**")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/socket-in")
                .enableSimpleBroker("/socket-out", "/socket-out-invite");
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new AndroidRideRequestController(objectMapper, rideRequestService, findDriverService, driverRepository), "/websocket");
        registry.addHandler(new AndroidChatController(), "/chat");
        registry.addHandler(new AndroidVehicleController(), "/vehicle-locations");
    }
}
