package top.meethigher.logmonitor.config;

import top.meethigher.logmonitor.handler.LogMonitorSocketEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author chenchuancheng
 * @since 2021/12/6 9:13
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${log.websocket}")
    private String path;

    @Autowired
    private LogMonitorSocketEventHandler logMonitorSocketEventHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(logMonitorSocketEventHandler, path).setAllowedOrigins("*");
    }
}
