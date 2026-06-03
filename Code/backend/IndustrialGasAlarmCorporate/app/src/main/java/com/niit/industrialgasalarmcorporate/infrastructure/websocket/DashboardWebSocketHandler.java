package com.niit.industrialgasalarmcorporate.infrastructure.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DashboardWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userUuid = (String) session.getAttributes().get("userUuid");
        sessions.put(session.getId(), session);
        if (userUuid != null) {
            sessionUserMap.put(session.getId(), userUuid);
        }
        log.debug("WebSocket 连接建立: sessionId={}, userUuid={}, 当前连接数={}",
                session.getId(), userUuid, sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        sessionUserMap.remove(session.getId());
        log.debug("WebSocket 连接关闭: sessionId={}, 当前连接数={}", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    }

    /** Broadcast to all connected sessions (admin-only — full data). */
    public void broadcastToAdmins(String payload) {
        TextMessage message = new TextMessage(payload);
        for (var entry : sessions.entrySet()) {
            String userUuid = sessionUserMap.get(entry.getKey());
            WebSocketSession session = entry.getValue();
            if (session.isOpen() && isAdminSession(entry.getKey())) {
                sendMessage(session, message);
            }
        }
    }

    /** Push to a specific user identified by userUuid. */
    public void sendToUser(String userUuid, String payload) {
        TextMessage message = new TextMessage(payload);
        for (var entry : sessions.entrySet()) {
            if (userUuid.equals(sessionUserMap.get(entry.getKey()))) {
                WebSocketSession session = entry.getValue();
                if (session.isOpen()) {
                    sendMessage(session, message);
                }
            }
        }
    }

    /** Push a scoped payload to every connected session based on their userUuid and role. */
    public void broadcastScoped(java.util.function.BiFunction<String, String, String> payloadResolver) {
        for (var entry : sessions.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (!session.isOpen()) continue;
            String userUuid = sessionUserMap.get(entry.getKey());
            if (userUuid == null) continue;
            String role = (String) session.getAttributes().get("role");
            String payload = payloadResolver.apply(userUuid, role);
            if (payload != null) {
                sendMessage(session, new TextMessage(payload));
            }
        }
    }

    private boolean isAdminSession(String sessionId) {
        String userUuid = sessionUserMap.get(sessionId);
        if (userUuid == null) return false;
        WebSocketSession session = sessions.get(sessionId);
        if (session == null) return false;
        String role = (String) session.getAttributes().get("role");
        return "ADMIN".equals(role);
    }

    private void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            synchronized (session) {
                session.sendMessage(message);
            }
        } catch (IOException e) {
            log.warn("WebSocket 推送失败: sessionId={}", session.getId(), e);
        }
    }

    public int getConnectionCount() {
        return sessions.size();
    }
}
