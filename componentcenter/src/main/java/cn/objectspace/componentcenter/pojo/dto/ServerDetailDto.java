package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;
import java.util.List;

public class ServerDetailDto implements Serializable {
    private static final long serialVersionUID = -6707664504175691477L;
    private String computerName;
    private String ip;
    private String osName;
    private String osVersion;
    private String osArch;
    private String memTotal;
    //虚拟内存/交换区总量
    private String swapTotal;
    private List<CpuDetailDto> cpuDetailDtos;
    private List<DiskDetailDto> diskDetailDtos;
    private List<NetDetailDto> netDetailDtos;

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(String memTotal) {
        this.memTotal = memTotal;
    }

    public String getSwapTotal() {
        return swapTotal;
    }

    public void setSwapTotal(String swapTotal) {
        this.swapTotal = swapTotal;
    }

    public List<CpuDetailDto> getCpuDetailDtos() {
        return cpuDetailDtos;
    }

    public void setCpuDetailDtos(List<CpuDetailDto> cpuDetailDtos) {
        this.cpuDetailDtos = cpuDetailDtos;
    }

    public List<DiskDetailDto> getDiskDetailDtos() {
        return diskDetailDtos;
    }

    public void setDiskDetailDtos(List<DiskDetailDto> diskDetailDtos) {
        this.diskDetailDtos = diskDetailDtos;
    }

    public List<NetDetailDto> getNetDetailDtos() {
        return netDetailDtos;
    }

    public void setNetDetailDtos(List<NetDetailDto> netDetailDtos) {
        this.netDetailDtos = netDetailDtos;
    }
}
