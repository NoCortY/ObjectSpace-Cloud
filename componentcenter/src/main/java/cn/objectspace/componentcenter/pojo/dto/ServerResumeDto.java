package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

/**
 * @Description: 服务器当前状态概要DTO
 * @Author: NoCortY
 * @Date: 2020/2/18
 */
public class ServerResumeDto implements Serializable {
    private static final long serialVersionUID = -7276110951378523716L;
    //CPU使用率
    private Double cpuUsedPercent;
    //内存使用率
    private Double memUsedPercent;
    //硬盘使用率
    private Double diskUsedPercent;
    //虚拟内存使用率
    private Double swapUsedPercent;

    //发包数
    private Long sendPackageTotal;
    //接包数
    private Long recPackageTotal;

    private Boolean onlineStatus;

    public Boolean getOnlineStatus() {
        return onlineStatus;
    }

    public Double getSwapUsedPercent() {
        return swapUsedPercent;
    }

    public void setSwapUsedPercent(Double swapUsedPercent) {
        this.swapUsedPercent = swapUsedPercent;
    }

    public void setOnlineStatus(Boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Double getCpuUsedPercent() {
        return cpuUsedPercent;
    }

    public void setCpuUsedPercent(Double cpuUsedPercent) {
        this.cpuUsedPercent = cpuUsedPercent;
    }

    public Double getMemUsedPercent() {
        return memUsedPercent;
    }

    public void setMemUsedPercent(Double memUsedPercent) {
        this.memUsedPercent = memUsedPercent;
    }

    public Double getDiskUsedPercent() {
        return diskUsedPercent;
    }

    public void setDiskUsedPercent(Double diskUsedPercent) {
        this.diskUsedPercent = diskUsedPercent;
    }

    public Long getSendPackageTotal() {
        return sendPackageTotal;
    }

    public void setSendPackageTotal(Long sendPackageTotal) {
        this.sendPackageTotal = sendPackageTotal;
    }

    public Long getRecPackageTotal() {
        return recPackageTotal;
    }

    public void setRecPackageTotal(Long recPackageTotal) {
        this.recPackageTotal = recPackageTotal;
    }
}
