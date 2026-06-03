package com.niit.industrialgasalarmcorporate.infrastructure.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DashboardWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.debug("WebSocket 连接建立: sessionId={}, 当前连接数={}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.debug("WebSocket 连接关闭: sessionId={}, 当前连接数={}", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 前端可发送心跳或设备订阅，当前版本忽略
    }

    public void broadcast(String payload) {
        TextMessage message = new TextMessage(payload);
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    synchronized (session) {
                        session.sendMessage(message);
                    }
                } catch (IOException e) {
                    log.warn("WebSocket 推送失败: sessionId={}", session.getId(), e);
                }
            }
        }
    }

    public int getConnectionCount() {
        return sessions.size();
    }
}
