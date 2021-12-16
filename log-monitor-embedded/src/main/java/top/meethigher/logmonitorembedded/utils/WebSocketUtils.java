package top.meethigher.logmonitorembedded.utils;


import top.meethigher.logmonitorembedded.monitor.FileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenchuancheng
 * @since 2021/12/6 11:21
 */
public class WebSocketUtils {

    private static final Logger log = LoggerFactory.getLogger(WebSocketUtils.class);
    /**
     * 已连接的websocket
     */
    private static Map<String, WebSocketSession> onlineSession = new HashMap<>();
    /**
     * 正在执行线程
     */
    private static Thread monitorThread;

    /**
     * 添加用户
     *
     * @param session
     */
    public static void addSessoin(WebSocketSession session) {
        onlineSession.put(session.getId(), session);
        log.info("{}的用户连接websocket", session.getId());
    }

    public static void setMonitorThread(Thread thread) {
        monitorThread = thread;
    }

    /**
     * 移除用户
     *
     * @param session
     */
    public static void reduceSession(WebSocketSession session) {
        onlineSession.remove(session.getId());
        log.info("{}的用户断开websocket", session.getId());
    }


    /**
     * 开启监测
     */
    public static void startMonitor(String defaultPath, String defaultLog) {
        if (ObjectUtils.isEmpty(monitorThread) || !monitorThread.isAlive()) {
            log.info("有新的监控websocket连入，监控线程die，开启新的线程监控");
            new FileMonitor(defaultPath + defaultLog);
        } else {
            log.info("有新的监控websocket连入，监控线程alive");
        }
    }


    /**
     * 关闭监控
     * session关闭，相应线程也会关闭
     *
     * @param message
     */
    public static void endMonitor(String message) {
        sendMessageToAll(message);
        closeAllSession();
    }


    /**
     * 发送消息给所有用户
     *
     * @param message
     */
    public static void sendMessageToAll(String message) {
        onlineSession.forEach((sessionId, session) -> {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 断开所有session
     */
    public static void closeAllSession() {
        onlineSession.forEach((sessionId, session) -> {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        onlineSession.clear();
    }

}
