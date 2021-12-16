package top.meethigher.logmonitorembedded.service.impl;


import top.meethigher.logmonitorembedded.constant.ResponseEnum;
import top.meethigher.logmonitorembedded.dto.logmonitor.DownloadLogRequest;
import top.meethigher.logmonitorembedded.dto.logmonitor.FileResponse;
import top.meethigher.logmonitorembedded.dto.logmonitor.QueryLogRequest;
import top.meethigher.logmonitorembedded.exception.CommonException;
import top.meethigher.logmonitorembedded.service.LogMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chenchuancheng
 * @since 2021/12/6 9:38
 */
@Service
public class LogMonitorServiceImpl implements LogMonitorService {

    private final Logger log = LoggerFactory.getLogger(LogMonitorServiceImpl.class);

    /**
     * 是否按照更新时间查询
     * 否则是按照日志名称范围查询
     */
    @Value("${log.monitor.queryByFileTime}")
    private boolean queryByFileTime = false;

    /**
     * 正则剔除的无效词
     */
    private final String notContainsKey = "log-info-|log-error-|log-warn-|.log";

    /**
     * 日志根目录、默认目录
     */
    @Value("${log.monitor.defaultPath}")
    private String logRootPath;

    /**
     * 查询集群中所有节点
     */
    @Value("${log.clusterNode}")
    private String clusterNode;

    /**
     * serverName
     */
    @Value("${cas.server-name}")
    private String casServerName;

    /**
     * context-path
     */
    @Autowired
    private ServerProperties serverProperties;

    private static final ThreadLocal<SimpleDateFormat> sdfThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd-HH"));


    /**
     * 获取路径
     *
     * @param logPath
     * @return
     */
    private String getLogPath(String logPath) throws CommonException {
        String path = null;
        if (ObjectUtils.isEmpty(logPath)) {
            path = logRootPath;
        } else {
            if (!logPath.contains(logRootPath)) {
                throw new CommonException(ResponseEnum.NO_ACCESS_FOR_THIS_PATH);
            }
            path = logPath;
        }
        return path;
    }

    /**
     * 按照时间查询日志
     *
     * @param startTime
     * @param endTime
     * @param dir
     * @return
     */
    private List<FileResponse> queryLogByTime(Long startTime, Long endTime, File dir) {
        List<FileResponse> files = new LinkedList<>();
        for (String s : Objects.requireNonNull(dir.list())) {
            FileResponse fileResponse = new FileResponse();
            File file = new File(dir, s);
            /**
             * 如果是按照文件更新时间范围查询，isFileTime为true
             * 如果是按照日志名称命名的时间范围查询，isFileTime为false
             */
            if (queryByFileTime) {
                long lastModified = file.lastModified();
                if (startTime <= lastModified && endTime >= lastModified) {
                    fileResponse.setLogPath(file.getAbsolutePath().replaceAll("\\\\", "/"));
                    fileResponse.setUpdateTime(lastModified);
                    fileResponse.setIsFile(file.isFile() ? 1 : 0);
                    files.add(fileResponse);
                }
            } else {
                SimpleDateFormat format = sdfThreadLocal.get();
                List<String> hourListRange = getHourListRange(format.format(new Date(startTime)), format.format(new Date(endTime)));
                //逻辑很蠢。这个地方就是按照时间查询，但是这个时间是文件名称的通过正则剔除无效词后的时间
                if (!ObjectUtils.isEmpty(hourListRange)) {
                    String name = file.getName();
                    String timeName = name.replaceAll(notContainsKey, "");
                    if (file.isFile()) {
                        if (hourListRange.contains(timeName)) {
                            fileResponse.setLogPath(file.getAbsolutePath().replaceAll("\\\\", "/"));
                            fileResponse.setUpdateTime(file.lastModified());
                            fileResponse.setIsFile(file.isFile() ? 1 : 0);
                            files.add(fileResponse);
                        }
                    }
                }
            }

        }
        log.info("queryLogByTime");
        return files;
    }


    /**
     * 获取时间范围
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private List<String> getHourListRange(String startTime, String endTime) {
        try {
            SimpleDateFormat format = sdfThreadLocal.get();
            List<String> list = new ArrayList<>();
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(startTime));
            Calendar ec = Calendar.getInstance();
            ec.setTime(format.parse(endTime));
            while (true) {
                if (ec.before(c)) {
                    list.add(format.format(c.getTime()));
                    break;
                }
                list.add(format.format(c.getTime()));
                c.add(Calendar.HOUR_OF_DAY, 1);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有日志
     *
     * @param dir
     * @return
     */
    private List<FileResponse> queryLogWithoutTime(File dir) {
        List<FileResponse> files = new LinkedList<>();
        for (String s : Objects.requireNonNull(dir.list())) {
            File file = new File(dir, s);
            FileResponse fileResponse = new FileResponse();
            fileResponse.setLogPath(file.getAbsolutePath().replaceAll("\\\\", "/"));
            fileResponse.setUpdateTime(file.lastModified());
            fileResponse.setIsFile(file.isFile() ? 1 : 0);
            files.add(fileResponse);
        }
        log.info("queryLogWithoutTime");
        return files;
    }


    @Override
    public List<FileResponse> queryLog(QueryLogRequest request) throws CommonException {
        String logPath = getLogPath(request.getLogPath());
        File dir = new File(logPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new CommonException(ResponseEnum.DIR_NOT_EXIST_OR_DIR_IS_A_FILE);
        }
        if (!ObjectUtils.isEmpty(request.getStartTime()) && !ObjectUtils.isEmpty(request.getEndTime())) {
            return queryLogByTime(request.getStartTime(), request.getEndTime(), dir);
        } else {
            return queryLogWithoutTime(dir);
        }
    }

    @Override
    public String downloadLog(DownloadLogRequest request, HttpServletResponse response) throws CommonException {
        String logPath = getLogPath(request.getLogPath());
        File file = new File(logPath);
        if (!file.exists() || !file.isFile()) {
            throw new CommonException(ResponseEnum.FILE_NOT_EXIST_OR_FILE_IS_DIRECTORY);
        }
        // 实现文件下载
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(request.getDownloadName(), "UTF-8"));
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            log.info("下载文件成功!");
        } catch (Exception e) {
            log.info("下载文件失败!");
            throw new CommonException(ResponseEnum.DOWNLOAD_FILE_FAILED);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public List<String> queryClusterNode() {
        String[] split = clusterNode.split(",");
        return Arrays.asList(split);
    }

    @Override
    public String currentPath() {
        if (ObjectUtils.isEmpty(serverProperties.getServlet().getContextPath())) {
            return casServerName;
        } else {
            return casServerName + serverProperties.getServlet().getContextPath();
        }

    }
}
