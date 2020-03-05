package cn.objectspace.componentcenter.pojo.dto.daemon;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* @Description: 服务器信息和健康状况DTO
* @Author: NoCortY
* @Date: 2020/1/19
*/
public class ServerInfoDto implements Serializable {
    private static final long serialVersionUID = 7277973986550822660L;
    /*服务器基本信息*/
    //服务器对应的用户id
    private Integer serverUser;
    //主机名
    private String computerName;
    //域名
    private String userDomain;
    //本机IP
    private String ip;
    //本地主机名
    private String hostName;
    //操作系统名
    private String osName;
    //操作系统架构
    private String osArch;
    //操作系统版本
    private String osVersion;

    /*内存基本信息*/
    //内存总量
    private Long memTotal;
    //使用量
    private Long memUsed;
    //剩余量
    private Long memFree;
    //内存使用率
    private Double memUsedPercent;
    //交换区（虚拟内存）总量
    private Long swapTotal;
    //交换区使用量
    private Long swapUsed;
    //交换区剩余量
    private Long swapFree;
    //交换区使用率
    private Double swapUsedPercent;

    //CPU
    private List<CpuDto> cpuList;

    //Disk
    private List<DiskDto> diskList;

    //NetDto
    private List<NetDto> netList;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm", timezone = "GMT+8")
    private Date recordTime;

    public Integer getServerUser() {
        return serverUser;
    }

    public void setServerUser(Integer serverUser) {
        this.serverUser = serverUser;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public Long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(Long memTotal) {
        this.memTotal = memTotal;
    }

    public Long getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(Long memUsed) {
        this.memUsed = memUsed;
    }

    public Long getMemFree() {
        return memFree;
    }

    public void setMemFree(Long memFree) {
        this.memFree = memFree;
    }

    public Double getMemUsedPercent() {
        return memUsedPercent;
    }

    public void setMemUsedPercent(Double memUsedPercent) {
        this.memUsedPercent = memUsedPercent;
    }

    public Long getSwapTotal() {
        return swapTotal;
    }

    public void setSwapTotal(Long swapTotal) {
        this.swapTotal = swapTotal;
    }

    public Long getSwapUsed() {
        return swapUsed;
    }

    public void setSwapUsed(Long swapUsed) {
        this.swapUsed = swapUsed;
    }

    public Long getSwapFree() {
        return swapFree;
    }

    public void setSwapFree(Long swapFree) {
        this.swapFree = swapFree;
    }

    public Double getSwapUsedPercent() {
        return swapUsedPercent;
    }

    public void setSwapUsedPercent(Double swapUsedPercent) {
        this.swapUsedPercent = swapUsedPercent;
    }

    public List<CpuDto> getCpuList() {
        return cpuList;
    }

    public void setCpuList(List<CpuDto> cpuList) {
        this.cpuList = cpuList;
    }

    public List<DiskDto> getDiskList() {
        return diskList;
    }

    public void setDiskList(List<DiskDto> diskList) {
        this.diskList = diskList;
    }

    public List<NetDto> getNetList() {
        return netList;
    }

    public void setNetList(List<NetDto> netList) {
        this.netList = netList;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "ServerInfoDto{" +
                "serverUser=" + serverUser +
                ", computerName='" + computerName + '\'' +
                ", userDomain='" + userDomain + '\'' +
                ", ip='" + ip + '\'' +
                ", hostName='" + hostName + '\'' +
                ", osName='" + osName + '\'' +
                ", osArch='" + osArch + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", memTotal=" + memTotal +
                ", memUsed=" + memUsed +
                ", memFree=" + memFree +
                ", memUsedPercent=" + memUsedPercent +
                ", swapTotal=" + swapTotal +
                ", swapUsed=" + swapUsed +
                ", swapFree=" + swapFree +
                ", swapUsedPercent=" + swapUsedPercent +
                ", cpuList=" + cpuList +
                ", diskList=" + diskList +
                ", netList=" + netList +
                ", recordTime=" + recordTime +
                '}';
    }
}
