package cn.objectspace.logcenter.dao;

import cn.objectspace.logcenter.pojo.entity.Log;
import org.apache.ibatis.annotations.Mapper;

/**
* @Description: 日志持久层
* @Author: NoCortY
* @Date: 2019/12/19
*/
@Mapper
public interface LogDao {
    /**
     * @Description: 新增日志信息
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    public Integer insertLog(Log log);

    /**
     * @Description: 获取日志条数（接口调用次数）
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2019/12/30
     */
    public Integer queryLogCount();
}
