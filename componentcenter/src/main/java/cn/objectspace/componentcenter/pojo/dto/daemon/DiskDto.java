package cn.objectspace.componentcenter.pojo.dto.daemon;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
* @Description: 硬盘信息Dto
* @Author: NoCortY
* @Date: 2020/1/19
*/
public class DiskDto implements Serializable {
    private static final long serialVersionUID = -333899734218316019L;
    private String diskServerIp;
    private Integer diskServerUser;
    //盘符名称
    private String diskName;
    //盘符类型
    private String diskType;
    //总大小
    private Long total;
    //剩余大小
    private Long free;
    //可用大小
    private  Long avail;
    //已经使用量
    private Long used;
    //使用率
    private Double usePercent;
    //读写
    private Long readDisk;
    private Long writeDisk;

    //读写速率
    private Double readRate;
    private Double writeRate;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm", timezone = "GMT+8")
    private Date recordTime;

    public String getDiskServerIp() {
        return diskServerIp;
    }

    public void setDiskServerIp(String diskServerIp) {
        this.diskServerIp = diskServerIp;
    }

    public Integer getDiskServerUser() {
        return diskServerUser;
    }

    public void setDiskServerUser(Integer diskServerUser) {
        this.diskServerUser = diskServerUser;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    public Long getAvail() {
        return avail;
    }

    public void setAvail(Long avail) {
        this.avail = avail;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Double getUsePercent() {
        return usePercent;
    }

    public void setUsePercent(Double usePercent) {
        this.usePercent = usePercent;
    }

    public Long getReadDisk() {
        return readDisk;
    }

    public void setReadDisk(Long readDisk) {
        this.readDisk = readDisk;
    }

    public Long getWriteDisk() {
        return writeDisk;
    }

    public void setWriteDisk(Long writeDisk) {
        this.writeDisk = writeDisk;
    }

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
        return "DiskDto{" +
                "diskServerIp='" + diskServerIp + '\'' +
                ", diskServerUser=" + diskServerUser +
                ", diskName='" + diskName + '\'' +
                ", diskType='" + diskType + '\'' +
                ", total=" + total +
                ", free=" + free +
                ", avail=" + avail +
                ", used=" + used +
                ", usePercent=" + usePercent +
                ", readDisk=" + readDisk +
                ", writeDisk=" + writeDisk +
                ", readRate=" + readRate +
                ", writeRate=" + writeRate +
                ", recordTime=" + recordTime +
                '}';
    }
}
