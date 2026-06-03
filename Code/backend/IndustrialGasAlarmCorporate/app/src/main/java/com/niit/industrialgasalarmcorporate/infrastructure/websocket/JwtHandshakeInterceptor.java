package com.niit.industrialgasalarmcorporate.infrastructure.websocket;

import com.niit.industrialgasalarmcorporate.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String query = request.getURI().getQuery();
        if (query == null) {
            log.warn("WebSocket 握手拒绝: 缺少 token 参数, remote={}", request.getRemoteAddress());
            return false;
        }
        String token = null;
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                token = param.substring(6);
                break;
            }
        }
        if (token == null || token.isEmpty()) {
            log.warn("WebSocket 握手拒绝: token 为空, remote={}", request.getRemoteAddress());
            return false;
        }
        try {
            var claims = jwtUtil.parseToken(token);
            attributes.put("userUuid", claims.getSubject());
            attributes.put("username", claims.get("username", String.class));
            attributes.put("role", claims.get("role", String.class));
            return true;
        } catch (Exception e) {
            log.warn("WebSocket 握手拒绝: token 无效, remote={}", request.getRemoteAddress());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
