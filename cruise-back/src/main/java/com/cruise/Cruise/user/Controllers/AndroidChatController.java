package com.cruise.Cruise.user.Controllers;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class AndroidChatController extends AbstractWebSocketHandler {

    public static Map<Long, WebSocketSession> openSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        Long userId = Long.valueOf(session.getHandshakeHeaders().get("id").get(0));
        openSessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        Long userId = Long.valueOf(session.getHandshakeHeaders().get("id").get(0));
        openSessions.remove(userId);
    }
}
