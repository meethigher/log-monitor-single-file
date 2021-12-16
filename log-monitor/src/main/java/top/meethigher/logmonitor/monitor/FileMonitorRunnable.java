package top.meethigher.logmonitor.monitor;

import top.meethigher.logmonitor.utils.WebSocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author chenchuancheng
 * @since 2021/12/6 13:47
 */
public class FileMonitorRunnable implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FileMonitorRunnable.class);

    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 100);

    private CharBuffer charBuffer = CharBuffer.allocate(1024 * 50);

    private CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

    private String logPath;

    private Long monitorDelay;

    public FileMonitorRunnable(String logPath, Long monitorDelay) {
        this.logPath = logPath;
        this.monitorDelay = monitorDelay;
    }

    @Override
    public void run() {
        File file = new File(logPath);
        FileChannel channel = null;
        try {
            channel = new FileInputStream(file).getChannel();
            channel.position(channel.size());
        } catch (Exception e) {
            log.info("监控文件失败，检查路径{}是否正确",logPath);
            WebSocketUtils.endMonitor("<error>监控文件失败，检查路径"+logPath+"是否正确</error>");
            e.printStackTrace();
        }
        long lastModified = file.lastModified();
        //TODO: 初次连接将所有内容丢回去？这个考虑到数据如果很多先不丢
        while (true) {
            long now = file.lastModified();
//            log.info("正在通过线程{}监控{}文件",Thread.currentThread().getName(),logPath);
            if (now != lastModified) {
//                log.info("正在通过线程{}监控{}的文件update", Thread.currentThread().getName(), logPath);
                String newContent = getNewContent(channel);
                if (!ObjectUtils.isEmpty(newContent)) {
                    WebSocketUtils.sendMessageToAll(newContent);
                }
                lastModified = now;
            }
            try {
                Thread.sleep(monitorDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
                WebSocketUtils.endMonitor("<error>线程出现异常</error>");
            }

        }
    }

    private String getNewContent(FileChannel channel) {
        try {
            byteBuffer.clear();
            charBuffer.clear();
            int length = channel.read(byteBuffer);
            if (length != -1) {
                byteBuffer.flip();
                decoder.decode(byteBuffer, charBuffer, true);
                charBuffer.flip();
                return charBuffer.toString();
            } else {
                channel.position(channel.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            WebSocketUtils.endMonitor("<error>读取增量内容出现异常</error>");
        }
        return null;
    }
}
