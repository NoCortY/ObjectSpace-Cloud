package cn.objectspace.componentcenter.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 服务器快照简信
 * @Author: NoCortY
 * @Date: 2020/4/21
 */
public class ServerSimpleSnapshot implements Serializable {
    private static final long serialVersionUID = 156071858394110402L;
    private Integer timeKeeping;
    private Double memory;
    private Double disk;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastHeartBeat;

    public Integer getTimeKeeping() {
        return timeKeeping;
    }

    public void setTimeKeeping(Integer timeKeeping) {
        this.timeKeeping = timeKeeping;
    }

    public Double getMemory() {
        return memory;
    }

    public void setMemory(Double memory) {
        this.memory = memory;
    }

    public Double getDisk() {
        return disk;
    }

    public void setDisk(Double disk) {
        this.disk = disk;
    }

    public Date getLastHeartBeat() {
        return lastHeartBeat;
    }

    public void setLastHeartBeat(Date lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }
}
