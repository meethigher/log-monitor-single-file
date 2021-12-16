package top.meethigher.logmonitor.exception;


import top.meethigher.logmonitor.constant.ResponseEnum;

/**
 * @author guomaofei
 * @date 2021/3/18 11:13
 */
public class CustomDescException extends Exception {
    private ResponseEnum responseEnum;

    private String desc;

    public CustomDescException() {
    }

    public CustomDescException(ResponseEnum responseEnum, String desc) {
        this.responseEnum = responseEnum;
        this.desc = desc;
    }


    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public void setResponseEnum(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

