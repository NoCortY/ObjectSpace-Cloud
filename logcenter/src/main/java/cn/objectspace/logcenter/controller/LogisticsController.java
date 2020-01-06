package cn.objectspace.logcenter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.logcenter.pojo.dto.NoticeDto;
import cn.objectspace.logcenter.service.LogisticsService;

@RestController
@RequestMapping("/Logistics")
public class LogisticsController {

	@Autowired
	LogisticsService logisticsService;
	@GetMapping("/notice")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    public ResponseMap<List<NoticeDto>> notice(){
    	ResponseMap<List<NoticeDto>> responseMap = new ResponseMap<List<NoticeDto>>();
    	List<NoticeDto> noticeDtoList = logisticsService.getNotice();
    	if(noticeDtoList==null) {
    		responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
    		responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
    	}else {
    		responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
    		responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
    		responseMap.setData(noticeDtoList);
    	}
    	return responseMap;
    }
}
