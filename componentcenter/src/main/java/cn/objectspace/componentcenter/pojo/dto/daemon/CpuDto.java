package cn.objectspace.componentcenter.pojo.dto.daemon;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
* @Description: Cpu信息
* @Author: NoCortY
* @Date: 2020/1/19
*/
public class CpuDto implements Serializable {
    private static final long serialVersionUID = -5686994829523765305L;
    private Integer cpuId;
    private String cpuServerIp;
    private Integer cpuServerUser;
    //频率
    private Integer frequency;
    //生产商
    private String vendor;
    //类别
    private String model;
    //用户使用率
    private Double userUsed;
    //系统使用率
    private Double systemUsed;
    //总使用率
    private Double combine;
    //空闲率
    private Double idle;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date recordTime;


    public Integer getCpuId() {
        return cpuId;
    }

    public void setCpuId(Integer cpuId) {
        this.cpuId = cpuId;
    }

    public String getCpuServerIp() {
        return cpuServerIp;
    }

    public void setCpuServerIp(String cpuServerIp) {
        this.cpuServerIp = cpuServerIp;
    }

    public Integer getCpuServerUser() {
        return cpuServerUser;
    }

    public void setCpuServerUser(Integer cpuServerUser) {
        this.cpuServerUser = cpuServerUser;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getUserUsed() {
        return userUsed;
    }

    public void setUserUsed(Double userUsed) {
        this.userUsed = userUsed;
    }

    public Double getSystemUsed() {
        return systemUsed;
    }

    public void setSystemUsed(Double systemUsed) {
        this.systemUsed = systemUsed;
    }

    public Double getCombine() {
        return combine;
    }

    public void setCombine(Double combine) {
        this.combine = combine;
    }

    public Double getIdle() {
        return idle;
    }

    public void setIdle(Double idle) {
        this.idle = idle;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "CpuDto{" +
                "cpuId=" + cpuId +
                ", cpuServerIp='" + cpuServerIp + '\'' +
                ", cpuServerUser=" + cpuServerUser +
                ", frequency=" + frequency +
                ", vendor='" + vendor + '\'' +
                ", model='" + model + '\'' +
                ", userUsed=" + userUsed +
                ", systemUsed=" + systemUsed +
                ", combine=" + combine +
                ", idle=" + idle +
                ", recordTime=" + recordTime +
                '}';
    }
}
