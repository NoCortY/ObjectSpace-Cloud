package cn.objectspace.common.pojo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
* @Description: 接口响应对象
* @Author: NoCortY
* @Date: 2019/12/22
*/
@ApiModel(value="ResponseMap",description = "接口响应结果对象")
public class ResponseMap<T> {
    @ApiModelProperty(value="状态码")
    private String code;
    @ApiModelProperty(value="状态详细消息")
    private String message;
    @ApiModelProperty(value="数据")
    private T data;
    @ApiModelProperty(value="条数")
    private Integer count;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
