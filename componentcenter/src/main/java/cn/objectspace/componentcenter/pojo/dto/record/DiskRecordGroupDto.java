package cn.objectspace.componentcenter.pojo.dto.record;

import java.io.Serializable;
import java.util.List;

public class DiskRecordGroupDto implements Serializable {
    private static final long serialVersionUID = 787657739113766093L;
    private String diskName;
    private List<DiskRecordDto> diskRecordDtoList;

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public List<DiskRecordDto> getDiskRecordDtoList() {
        return diskRecordDtoList;
    }

    public void setDiskRecordDtoList(List<DiskRecordDto> diskRecordDtoList) {
        this.diskRecordDtoList = diskRecordDtoList;
    }


    @Override
    public String toString() {
        return "DiskRecordGroupDto{" +
                "diskName='" + diskName + '\'' +
                ", diskRecordDtoList=" + diskRecordDtoList +
                '}';
    }
}
