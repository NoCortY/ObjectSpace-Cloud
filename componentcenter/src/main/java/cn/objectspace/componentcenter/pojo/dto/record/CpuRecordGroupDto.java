package cn.objectspace.componentcenter.pojo.dto.record;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 按照cpuid区分
 * @Author: NoCortY
 * @Date: 2020/3/4
 */
public class CpuRecordGroupDto implements Serializable {
    private static final long serialVersionUID = 6198336550195995441L;
    private Integer cpuId;
    private List<CpuRecordDto> cpuRecordDtoList;


    public Integer getCpuId() {
        return cpuId;
    }

    public void setCpuId(Integer cpuId) {
        this.cpuId = cpuId;
    }

    public List<CpuRecordDto> getCpuRecordDtoList() {
        return cpuRecordDtoList;
    }

    public void setCpuRecordDtoList(List<CpuRecordDto> cpuRecordDtoList) {
        this.cpuRecordDtoList = cpuRecordDtoList;
    }

    @Override
    public String toString() {
        return "CpuRecordGroupDto{" +
                "cpuId=" + cpuId +
                ", cpuRecordDtoList=" + cpuRecordDtoList +
                '}';
    }
}
