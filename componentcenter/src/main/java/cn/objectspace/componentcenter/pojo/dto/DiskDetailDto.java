package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

public class DiskDetailDto implements Serializable {

    private static final long serialVersionUID = -2429707631060980997L;

    //盘符
    private String diskName;
    //文件系统类型
    private String diskType;
    //大小
    private String total;

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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
