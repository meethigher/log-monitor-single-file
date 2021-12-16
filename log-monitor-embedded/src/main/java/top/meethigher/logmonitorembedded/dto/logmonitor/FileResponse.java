package top.meethigher.logmonitorembedded.dto.logmonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @author chenchuancheng
 * @since 2021/12/8 13:32
 */
@ApiModel(value = "FileResponse")
public class FileResponse {
    @ApiModelProperty(value = "日志路径")
    private String logPath;

    @ApiModelProperty(value = "是否是文件，0否1是")
    private Integer isFile;

    @ApiModelProperty(value = "更新时间")
    private Long updateTime;

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public Integer getIsFile() {
        return isFile;
    }

    public void setIsFile(Integer isFile) {
        this.isFile = isFile;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}