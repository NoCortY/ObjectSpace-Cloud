package cn.objectspace.componentcenter.pojo.dto;

import java.io.Serializable;

/**
 * @Description: 服务器基本状态DTO(正常运行数量 、 高负载 、 停机)
 * @Author: NoCortY
 * @Date: 2020/4/21
 */
public class ServerSimpleStatusDto implements Serializable {
    private static final long serialVersionUID = 6801099590017502153L;
    private Integer normalCount;
    private Integer highOverload;
    private Integer down;

    public Integer getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount) {
        this.normalCount = normalCount;
    }

    public Integer getHighOverload() {
        return highOverload;
    }

    public void setHighOverload(Integer highOverload) {
        this.highOverload = highOverload;
    }

    public Integer getDown() {
        return down;
    }

    public void setDown(Integer down) {
        this.down = down;
    }
}
