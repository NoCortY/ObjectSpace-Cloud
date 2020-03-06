package cn.objectspace.componentcenter.pojo.dto.record;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 硬盘监控记录Dto
 * @Author: NoCortY
 * @Date: 2020/3/5
 */
public class DiskRecordDto implements Serializable {
    private static final long serialVersionUID = 8549910409851032858L;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date recordTime;
    private Double readRate;
    private Double writeRate;


    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }


    public Double getReadRate() {
        return readRate;
    }

    public void setReadRate(Double readRate) {
        this.readRate = readRate;
    }

    public Double getWriteRate() {
        return writeRate;
    }

    public void setWriteRate(Double writeRate) {
        this.writeRate = writeRate;
    }

    @Override
    public String toString() {
        return "DiskRecordDto{" +
                ", recordTime=" + recordTime +
                ", readRate=" + readRate +
                ", writeRate=" + writeRate +
                '}';
    }
}
