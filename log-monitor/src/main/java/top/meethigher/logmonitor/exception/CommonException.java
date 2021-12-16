package top.meethigher.logmonitor.exception;


import top.meethigher.logmonitor.constant.ResponseEnum;

/**
 * @author hanpeng
 * @date 2020/7/27 16:40
 */
public class CommonException extends Exception {
    private ResponseEnum responseEnum;

    public CommonException(ResponseEnum responseEnum) {
        this.responseEnum=responseEnum;
    }

    public CommonException(String message) {
        super(message);
    }

    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public void setResponseEnum(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }
}
