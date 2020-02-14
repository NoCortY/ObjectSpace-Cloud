package cn.objectspace.componentcenter.pojo.dto.daemon;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
* @Description: 网卡信息Dto
* @Author: NoCortY
* @Date: 2020/1/19
*/
public class NetDto implements Serializable {
    private static final long serialVersionUID = -4795035738569949242L;

    private String netServerIp;
    private Integer netServerUser;

    //名
    private String netName;
    //IP
    private String netIp;
    //子网掩码
    private String netMask;
    //接包数
    private Long rxPackets;
    //发包数
    private Long txPackets;
    //接收到的总字节数
    private Long rxBytes;
    //发送的总字节数
    private Long txBytes;
    //接收到的错误包数
    private Long rxErrors;
    //发送的错误包数
    private Long txErrors;
    //接收丢包数
    private Long rxDropped;
    //发送丢包数
    private Long txDropped;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date recordTime;

    public String getNetServerIp() {
        return netServerIp;
    }

    public void setNetServerIp(String netServerIp) {
        this.netServerIp = netServerIp;
    }

    public Integer getNetServerUser() {
        return netServerUser;
    }

    public void setNetServerUser(Integer netServerUser) {
        this.netServerUser = netServerUser;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public String getNetIp() {
        return netIp;
    }

    public void setNetIp(String netIp) {
        this.netIp = netIp;
    }

    public String getNetMask() {
        return netMask;
    }

    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }

    public Long getRxPackets() {
        return rxPackets;
    }

    public void setRxPackets(Long rxPackets) {
        this.rxPackets = rxPackets;
    }

    public Long getTxPackets() {
        return txPackets;
    }

    public void setTxPackets(Long txPackets) {
        this.txPackets = txPackets;
    }

    public Long getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(Long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public Long getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(Long txBytes) {
        this.txBytes = txBytes;
    }

    public Long getRxErrors() {
        return rxErrors;
    }

    public void setRxErrors(Long rxErrors) {
        this.rxErrors = rxErrors;
    }

    public Long getTxErrors() {
        return txErrors;
    }

    public void setTxErrors(Long txErrors) {
        this.txErrors = txErrors;
    }

    public Long getRxDropped() {
        return rxDropped;
    }

    public void setRxDropped(Long rxDropped) {
        this.rxDropped = rxDropped;
    }

    public Long getTxDropped() {
        return txDropped;
    }

    public void setTxDropped(Long txDropped) {
        this.txDropped = txDropped;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }


    @Override
    public String toString() {
        return "NetDto{" +
                "netServerIp='" + netServerIp + '\'' +
                ", netServerUser=" + netServerUser +
                ", netName='" + netName + '\'' +
                ", netIp='" + netIp + '\'' +
                ", netMask='" + netMask + '\'' +
                ", rxPackets=" + rxPackets +
                ", txPackets=" + txPackets +
                ", rxBytes=" + rxBytes +
                ", txBytes=" + txBytes +
                ", rxErrors=" + rxErrors +
                ", txErrors=" + txErrors +
                ", rxDropped=" + rxDropped +
                ", txDropped=" + txDropped +
                ", recordTime=" + recordTime +
                '}';
    }
}
