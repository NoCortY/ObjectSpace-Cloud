package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

public class NetDetailDto implements Serializable {
    private static final long serialVersionUID = -4955253474690768744L;

    //网卡名
    private String netName;
    //网卡IP
    private String netIp;
    //子网掩码
    private String netMask;

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
}
