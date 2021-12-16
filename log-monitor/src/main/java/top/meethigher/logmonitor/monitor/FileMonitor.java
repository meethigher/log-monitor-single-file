package top.meethigher.logmonitor.monitor;

import top.meethigher.logmonitor.utils.WebSocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenchuancheng
 * @since 2021/12/6 11:46
 */
public class FileMonitor {

    private static final Logger log = LoggerFactory.getLogger(FileMonitor.class);



    /**
     * 绑定的监控日志路径
     */
    private String logPath;

    /**
     * 监控时间间隔，单位ms
     */
    private Long monitorDelay;

    public FileMonitor(String logPath) {
       this.logPath=logPath;
        this.monitorDelay = 500L;
        startFileMonitor(monitorDelay);
    }

    public FileMonitor(String logPath,Long monitorDelay) {
        this.logPath=logPath;
        this.monitorDelay = monitorDelay;
        startFileMonitor(monitorDelay);
    }

    private void startFileMonitor(Long monitorDelay) {
        Thread thread = new Thread(new FileMonitorRunnable(logPath, monitorDelay));
        thread.start();
        WebSocketUtils.setMonitorThread(thread);
    }


}
