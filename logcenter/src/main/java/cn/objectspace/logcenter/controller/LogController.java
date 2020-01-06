package cn.objectspace.logcenter.controller;

import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.logcenter.pojo.entity.Log;
import cn.objectspace.logcenter.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/recordLog")
    public ResponseMap<String> recordLog(@RequestBody Log log){
        ResponseMap<String> responseMap = new ResponseMap<>();
        if(logService.addLog(log)){
            responseMap.setCode(ConstantPool.LogCenter.RECORD_LOG_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.LogCenter.RECORD_LOG_SUCCESS_MESSAGE);
        }else{
            responseMap.setCode(ConstantPool.LogCenter.RECORD_LOG_FALURE_CODE);
            responseMap.setMessage(ConstantPool.LogCenter.RECORD_LOG_FALURE_MESSAGE);
        }
        responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        return responseMap;
    }

    /**
     * @Description: 获取日志条数（接口调用次数）
     * @Param: []
     * @return: cn.objectspace.common.pojo.entity.ResponseMap<java.lang.Integer>
     * @Author: NoCortY
     * @Date: 2019/12/30
     */
    @GetMapping("/logCount")
    public ResponseMap<Integer> logCount(){
		/*
		 * ResponseMap<Integer> responseMap = new ResponseMap<>(); Integer logCount =
		 * logService.getLogCount(); if(logCount!=null){
		 * responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
		 * responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
		 * responseMap.setData(logCount); }else{
		 * responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
		 * responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE); } return
		 * responseMap;
		 */
    	System.out.println("123");
    	return null;
    }
}
