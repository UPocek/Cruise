package com.cruise.Cruise.ride.Controllers;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.ride.Services.IFindDriverService;
import com.cruise.Cruise.ride.Services.IRideRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.*;

@RestController
public class AndroidRideRequestController extends AbstractWebSocketHandler {

    public static Map<Long, WebSocketSession> passengerSessions = new HashMap<>();
    public static Map<Long, WebSocketSession> driverSessions = new HashMap<>();
    public static Set<Driver> driversListeningOnWebSocket = new HashSet<>();
    private ObjectMapper objectMapper;
    private IRideRequestService rideRequestService;
    private IFindDriverService findDriverService;
    private IDriverRepository driverRepository;

    public AndroidRideRequestController(ObjectMapper objectMapper, IRideRequestService rideRequestService, IFindDriverService findDriverService, IDriverRepository driverRepository) {
        this.objectMapper = objectMapper;
        this.rideRequestService = rideRequestService;
        this.findDriverService = findDriverService;
        this.driverRepository = driverRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        Long userId = Long.valueOf(session.getHandshakeHeaders().get("id").get(0));
        String userRole = session.getHandshakeHeaders().get("role").get(0);
        if (userRole.equals("ROLE_DRIVER")) {
            driverSessions.put(userId, session);
            driversListeningOnWebSocket.add(driverRepository.findById(userId).get());
        } else if (userRole.equals("ROLE_PASSENGER")) {
            passengerSessions.put(userId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        Long userId = Long.valueOf(session.getHandshakeHeaders().get("id").get(0));
        String userRole = session.getHandshakeHeaders().get("role").get(0);
        if (userRole.equals("ROLE_DRIVER")) {
            driverSessions.remove(userId);
            driversListeningOnWebSocket.removeIf(driver -> Objects.equals(driver.getId(), userId));
        } else if (userRole.equals("ROLE_PASSENGER")) {
            passengerSessions.remove(userId);
        }
    }
}
