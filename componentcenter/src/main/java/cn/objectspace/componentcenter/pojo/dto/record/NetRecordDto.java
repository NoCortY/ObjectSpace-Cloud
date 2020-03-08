package cn.objectspace.componentcenter.pojo.dto.record;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 网卡运行时记录
 * @Author: NoCortY
 * @Date: 2020/3/7
 */
public class NetRecordDto implements Serializable {
    private static final long serialVersionUID = 8187721384431891292L;
    private Double rxRate;
    private Double txRate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recordTime;


    public Double getRxRate() {
        return rxRate;
    }

    public void setRxRate(Double rxRate) {
        this.rxRate = rxRate;
    }

    public Double getTxRate() {
        return txRate;
    }

    public void setTxRate(Double txRate) {
        this.txRate = txRate;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
}
