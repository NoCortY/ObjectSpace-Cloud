package cn.objectspace.componentcenter.pojo.dto.record;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class MemRecordDto implements Serializable {
    private static final long serialVersionUID = -5288355244893007899L;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recordTime;
    private Double memUsedPercent = 0.0;
    private Double swapUsedPercent = 0.0;

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public Double getMemUsedPercent() {
        return memUsedPercent;
    }

    public void setMemUsedPercent(Double memUsedPercent) {
        this.memUsedPercent = memUsedPercent;
    }

    public Double getSwapUsedPercent() {
        return swapUsedPercent;
    }

    public void setSwapUsedPercent(Double swapUsedPercent) {
        this.swapUsedPercent = swapUsedPercent;
    }

    @Override
    public String toString() {
        return "MemRecordDto{" +
                "recordTime=" + recordTime +
                ", memUsedPercent=" + memUsedPercent +
                ", swapUsedPercent=" + swapUsedPercent +
                '}';
    }
}
