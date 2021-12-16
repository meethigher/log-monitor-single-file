package top.meethigher.logmonitorembedded.exception;

import top.meethigher.logmonitorembedded.dto.BaseResponse;
import top.meethigher.logmonitorembedded.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 捕获全局CommonException异常
 * 嵌入其他项目时，按照最高优先级处理异常
 *
 * @author chenchuancheng
 * @since 2021/12/7 14:47
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogMonitorExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(LogMonitorExceptionHandler.class);


    @ExceptionHandler(CommonException.class)
    public BaseResponse handleCommonException(CommonException e) {
        e.printStackTrace();
        log.error("CommonException :{} ", e.getResponseEnum().desc);
        return ResponseUtils.getResponseCode(e.getResponseEnum());
    }

}

