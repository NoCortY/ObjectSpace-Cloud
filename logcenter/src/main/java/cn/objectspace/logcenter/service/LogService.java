package cn.objectspace.logcenter.service;


import java.util.List;
import java.util.Map;

import cn.objectspace.logcenter.pojo.dto.CallCountDto;
import cn.objectspace.logcenter.pojo.entity.Log;

/**
* @Description: 日志中心职能
* @Author: NoCortY
* @Date: 2019/12/19
*/
public interface LogService {
    /**
     * @Description: 日志入库
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public Boolean addLog(Log log);

    /**
     * @Description: 获取日志条数
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/30
     */
    public Integer getLogCount();
    
    /**
     * 获取接口调用次数
     * @return
     */
    public Map<String,List<CallCountDto>> getCallCount();
}
