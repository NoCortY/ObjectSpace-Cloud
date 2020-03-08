package cn.objectspace.componentcenter.pojo.dto.record;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 按照网卡名进行分类
 * @Author: NoCortY
 * @Date: 2020/3/7
 */
public class NetRecordGroupDto implements Serializable {
    private static final long serialVersionUID = -6451410919806248441L;
    private String netName;
    private List<NetRecordDto> netRecordDtoList;


    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public List<NetRecordDto> getNetRecordDtoList() {
        return netRecordDtoList;
    }

    public void setNetRecordDtoList(List<NetRecordDto> netRecordDtoList) {
        this.netRecordDtoList = netRecordDtoList;
    }
}
