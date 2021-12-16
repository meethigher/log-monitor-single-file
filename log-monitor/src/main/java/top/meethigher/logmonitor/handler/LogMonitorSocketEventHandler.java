package top.meethigher.logmonitor.handler;

import top.meethigher.logmonitor.utils.WebSocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * @author chenchuancheng
 * @since 2021/12/6 11:03
 */
@Component
public class LogMonitorSocketEventHandler extends AbstractWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(LogMonitorSocketEventHandler.class);

    @Value("${log.monitor.defaultPath}")
    private String defaultPath;

    @Value("${log.monitor.defaultLog}")
    private String defaultLog;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("进行连接");
        WebSocketUtils.addSessoin(session);
        WebSocketUtils.startMonitor(defaultPath,defaultLog);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("关闭连接");
        WebSocketUtils.reduceSession(session);
    }
}
