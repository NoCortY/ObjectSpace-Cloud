package cn.objectspace.componentcenter.pojo.dto.record;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: Cpu运行记录
 * @Author: NoCortY
 * @Date: 2020/3/4
 */
public class CpuRecordDto implements Serializable {
    private static final long serialVersionUID = -8162476547812511830L;
    private Double userUsed;
    private Double systemUsed;
    private Double combine;
    private Date recordTime;


    public Double getUserUsed() {
        return userUsed;
    }

    public void setUserUsed(Double userUsed) {
        this.userUsed = userUsed * 100;
    }

    public Double getSystemUsed() {
        return systemUsed;
    }

    public void setSystemUsed(Double systemUsed) {
        this.systemUsed = systemUsed * 100;
    }

    public Double getCombine() {
        return combine;
    }

    public void setCombine(Double combine) {
        this.combine = combine * 100;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "CpuRecordDto{" +
                "userUsed=" + userUsed +
                ", systemUsed=" + systemUsed +
                ", combine=" + combine +
                ", recordTime=" + recordTime +
                '}';
    }
}
