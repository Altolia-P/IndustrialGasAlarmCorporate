package com.niit.industrialgasalarmcorporate.infrastructure.config;

import com.niit.industrialgasalarmcorporate.infrastructure.websocket.DashboardWebSocketHandler;
import com.niit.industrialgasalarmcorporate.infrastructure.websocket.JwtHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DashboardWebSocketHandler dashboardWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(DashboardWebSocketHandler dashboardWebSocketHandler,
                           JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.dashboardWebSocketHandler = dashboardWebSocketHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(dashboardWebSocketHandler, "/ws/dashboard")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
