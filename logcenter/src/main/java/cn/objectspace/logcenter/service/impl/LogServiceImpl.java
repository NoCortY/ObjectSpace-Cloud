package cn.objectspace.logcenter.service.impl;

import cn.objectspace.logcenter.dao.LogDao;
import cn.objectspace.logcenter.pojo.dto.CallCountDto;
import cn.objectspace.logcenter.pojo.entity.Log;
import cn.objectspace.logcenter.service.LogService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @Description: 日志中心职能
* @Author: NoCortY
* @Date: 2019/12/19
*/
@Service
public class LogServiceImpl implements LogService {
    @Autowired
    LogDao logDao;
    /**
     * @Description: 日志入库
     * @Param: [log]
     * @return: java.lang.Boolean
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @Override
    public Boolean addLog(Log log) {
        if(log==null) return false;
        Integer effectiveNums = logDao.insertLog(log);
        if(effectiveNums>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Integer getLogCount() {
        return logDao.queryLogCount();
    }

	@Override
	public Map<String,List<CallCountDto>> getCallCount() {
        List<String> maxInterfaceNameList = logDao.queryMaxInterfaceName();
        Map<String,List<CallCountDto>> resMap = new LinkedHashMap<>();
        if(maxInterfaceNameList==null||maxInterfaceNameList.size()==0) return null;
        for(String maxInterfaceName:maxInterfaceNameList){
            //将数据以("接口名","七天调用次数的形式返回")
            resMap.put(maxInterfaceName,logDao.queryCallCountWeek(maxInterfaceName));
        }
        return resMap;
	}
}
