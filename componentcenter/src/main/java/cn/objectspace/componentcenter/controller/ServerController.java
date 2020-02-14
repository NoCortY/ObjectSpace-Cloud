package cn.objectspace.componentcenter.controller;

import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.common.util.HttpRequestUtil;
import cn.objectspace.componentcenter.pojo.dto.CloudServerDto;
import cn.objectspace.componentcenter.pojo.dto.daemon.ServerInfoDto;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.service.ServerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/CC/server")
public class ServerController {
    @Autowired
    ServerService serverService;
    Logger logger = LoggerFactory.getLogger(ServerController.class);
    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/register")
    public ResponseMap<String> register(@RequestBody CloudServer cloudServer){
        ResponseMap<String> responseMap = new ResponseMap<>();
        //正则匹配
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(cloudServer.getServerIp());
        if(!m.matches()){
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.ComponentCenter.ERROR_SERVER_IP);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
            return responseMap;
        }
        if(serverService.registerServer(cloudServer)){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.ComponentCenter.REGISTER_SERVER_SUCCESS);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.ComponentCenter.REGISTER_SERVER_FALURE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/listMyselfServer")
    public ResponseMap<List<CloudServerDto>> listMyselfServer(HttpServletRequest request) throws JsonProcessingException {
        ResponseMap<List<CloudServerDto>> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute("CCUserId");
        //分页
        Integer page = HttpRequestUtil.getIntegerParameter(request,"page");
        Integer limit = HttpRequestUtil.getIntegerParameter(request,"limit");
        List<CloudServerDto> cloudServerList = serverService.getMySelfServer(userId,page,limit);
        if(cloudServerList!=null){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(cloudServerList);
            responseMap.setCount(serverService.getCountOfMySelfServer(userId));
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }
    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/ping")
    public ResponseMap<String> ping(@RequestBody ServerInfoDto serverInfoDto){
            ResponseMap<String> responseMap = new ResponseMap<>();
        //心跳接收器
        if(serverService.ping(serverInfoDto)){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            //响应ping
            responseMap.setData(ConstantPool.ComponentCenter.PONG);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }
        return responseMap;
    }
}
