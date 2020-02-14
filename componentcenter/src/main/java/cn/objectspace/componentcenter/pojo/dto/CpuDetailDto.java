package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

public class CpuDetailDto implements Serializable {

    private static final long serialVersionUID = 6865707260543273680L;
    //核心号
    private Integer cpuId;
    //频率
    private Integer frequency;
    //厂商
    private String vendor;
    //型号
    private String model;

    public Integer getCpuId() {
        return cpuId;
    }

    public void setCpuId(Integer cpuId) {
        this.cpuId = cpuId;
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
}
