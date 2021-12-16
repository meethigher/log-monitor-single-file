package top.meethigher.logmonitor.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Api(value = "WebSocketMonitorController", tags = "日志监控")
@RequestMapping("/log")
public class WebSocketMonitorController {

    /**
     * 前端页面
     */
    private final String FRONT_PAGE = "logmonitor/index.html";

    /**
     * context-path
     */
    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private WebMvcProperties webMvcProperties;


    @Value("${cas.server-name}")
    private String serverName;

    @GetMapping(value = "/monitor")
    @ApiOperation(value = "访问监控页面", notes = "访问监控页面")
    public void monitor(HttpServletResponse response) throws IOException {
        String staticPathPattern = webMvcProperties.getStaticPathPattern();
        String staticPath = staticPathPattern.replaceAll("\\**", "");
        if (ObjectUtils.isEmpty(serverProperties.getServlet().getContextPath())) {
            String url = serverName + staticPath;
            String interfaceUrl = serverName;
            response.sendRedirect(staticPath + FRONT_PAGE +
                    "?url=" + url +
                    "&interface=" + interfaceUrl);
        } else {
            String url = serverName + serverProperties.getServlet().getContextPath() + staticPath;
            String interfaceUrl = serverName + serverProperties.getServlet().getContextPath();
            response.sendRedirect(serverProperties.getServlet().getContextPath() + staticPath + FRONT_PAGE +
                            "?url=" + url +
                            "&interface=" + interfaceUrl);
        }

    }
}