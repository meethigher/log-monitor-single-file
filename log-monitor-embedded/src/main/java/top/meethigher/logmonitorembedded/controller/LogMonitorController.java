package top.meethigher.logmonitorembedded.controller;


import top.meethigher.logmonitorembedded.dto.BaseResponse;
import top.meethigher.logmonitorembedded.dto.logmonitor.DownloadLogRequest;
import top.meethigher.logmonitorembedded.dto.logmonitor.FileResponse;
import top.meethigher.logmonitorembedded.dto.logmonitor.QueryLogRequest;
import top.meethigher.logmonitorembedded.exception.CommonException;
import top.meethigher.logmonitorembedded.service.LogMonitorService;
import top.meethigher.logmonitorembedded.utils.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author chenchuancheng
 * @since 2021/12/6 9:10
 */
@RestController
@Api(value = "LogController", tags = "日志监控")
@RequestMapping(value = "/log")
public class LogMonitorController {

    @Autowired
    private LogMonitorService logMonitorService;


    @PostMapping(value = "/queryLog")
    @ApiOperation(value = "查询当前目录下内容", notes = "查询当前目录下内容\n" +
            "logPath不传，默认查询项目监控的根目录，传的话是查询指定目录\n" +
            "startTime与endTime不传是查所有，传的话，是查指定时间范围内\n" +
            "isFile 是否为文件类型，0否 1是")
    public BaseResponse<List<FileResponse>> queryLog(@RequestBody @Validated QueryLogRequest request) throws CommonException {
        return ResponseUtils.getSuccessResponse(logMonitorService.queryLog(request));
    }

    @PostMapping(value = "/downloadLog")
    @ApiOperation(value = "下载日志", notes = "下载日志\ndownloadName是自定义的要下载后的名称\nlogPath是要下载的文件路径")
    public String downloadLog(@RequestBody @Validated DownloadLogRequest request, HttpServletResponse response) throws CommonException, UnsupportedEncodingException {
        return logMonitorService.downloadLog(request, response);
    }

    @GetMapping(value = "/queryClusterNode")
    @ApiOperation(value = "查询所有的集群节点", notes = "查询所有的集群节点")
    public List<String> queryClusterNode() {
        return logMonitorService.queryClusterNode();
    }

    @GetMapping(value = "/currentPath")
    @ApiOperation(value = "查询当前的系统的路径", notes = "查询当前的系统路径")
    public String currentPath() {
        return logMonitorService.currentPath();
    }




}