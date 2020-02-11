package cn.objectspace.logcenter.controller;

import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.logcenter.pojo.dto.CallCountDto;
import cn.objectspace.logcenter.pojo.entity.Log;
import cn.objectspace.logcenter.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @Description: 日志控制器
* @Author: NoCortY
* @Date: 2019/12/19
*/
@RestController
@RequestMapping("/LC")
public class LogController {
    @Autowired
    private LogService logService;
    private Logger logger = LoggerFactory.getLogger(LogController.class);
    /**
     * @Description:  记录日志
     * @Param: [request]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @Async
    @PostMapping("/recordLog")
    public void recordLog(@RequestBody Log log){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("====================================日志入库===================================");
        try {
        	if(logService.addLog(log)) logger.info("{}:   操作日志入库成功!  ",sdf.format(new Date()));
            else logger.error("{}:   操作日志入库失败!  ",sdf.format(new Date()));
            
        }catch(Exception e) {
        	logger.error("{}:   日志入库异常!",sdf.format(new Date()));
        	logger.error("{}:   异常信息:{}",sdf.format(new Date()),e.getMessage());
        }
        logger.info("==========================================================================");
    }

    /**
     * @Description: 获取日志条数（接口调用次数）
     * @Param: []
     * @return: cn.objectspace.common.pojo.entity.ResponseMap<java.lang.Integer>
     * @Author: NoCortY
     * @Date: 2019/12/30
     */
    @GetMapping("/logCount")
    @SaveLog(applicationId = ConstantPool.LogCenter.APPLICATION_ID)
    public ResponseMap<Integer> logCount(){
        ResponseMap<Integer> responseMap = new ResponseMap<>();
        Integer logCount = logService.getLogCount();
        if(logCount!=null){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(logCount);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }
    
    @GetMapping("/callCount")
    @SaveLog(applicationId=ConstantPool.LogCenter.APPLICATION_ID)
    public ResponseMap<Map<String,List<CallCountDto>>> callCount(){
    	ResponseMap<Map<String,List<CallCountDto>>> responseMap = new ResponseMap<>();
        Map<String,List<CallCountDto>> dataMap = logService.getCallCount();
    	if(dataMap==null) {
    		responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
    		responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
    	}else {
    		responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
    		responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
    		responseMap.setData(dataMap);
    	}
    	return responseMap;
    }
}
