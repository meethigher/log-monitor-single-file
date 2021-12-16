package top.meethigher.logmonitorembedded.monitor;


import top.meethigher.logmonitorembedded.utils.WebSocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

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

    private Date getTargetTime(Date currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    @Override
    public void run() {
        File file = new File(logPath);
        FileChannel channel = null;
        try {

            channel = new RandomAccessFile(file, "rw").getChannel();
            channel.position(channel.size());
        } catch (Exception e) {
            log.info("监控文件失败，检查路径{}是否正确", logPath);
            WebSocketUtils.endMonitor("<error>监控文件失败，检查路径" + logPath + "是否正确</error>");
            e.printStackTrace();
        }
        long lastModified = file.lastModified();
        //TODO: 初次连接将所有内容丢回去？这个考虑到数据如果很多先不丢
        Date currentTime = new Date();
        Date targetTime = getTargetTime(currentTime);
        while (true) {
            currentTime = new Date();
            //到了指定时间以后清空日志，sb logback，在linux上rolling文件时，读取不到新生成的文件内容，所以这边自己进行维护
            if (currentTime.getTime() >= targetTime.getTime()) {
                try {
                    log.info("每日清空日志开始--->删除前的channel大小：{},位置：{}", channel.size(), channel.position());
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write("定时清空日志");
                    fileWriter.flush();
                    fileWriter.close();
                    channel = new RandomAccessFile(file, "rw").getChannel();
                    channel.position(channel.size());
                    log.info("每日清空日志结束--->新生成的channel大小：{},位置：{}", channel.size(), channel.position());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentTime = new Date();
                targetTime = getTargetTime(currentTime);
            }
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
