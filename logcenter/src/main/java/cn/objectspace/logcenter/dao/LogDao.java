package cn.objectspace.logcenter.dao;

import cn.objectspace.logcenter.pojo.dto.CallCountDto;
import cn.objectspace.logcenter.pojo.entity.Log;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    
    /**
     * 查询各接口调用次数
     * @return
     */
    public List<String> queryMaxInterfaceName();


    /**
     * @Description: 查询接口调用次数（周）
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/1/10
     */
    public List<CallCountDto> queryCallCountWeek(@Param("interfaceName")String interfaceName);
}
