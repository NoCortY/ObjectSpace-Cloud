package cn.objectspace.authcenter.web.controller;

import cn.objectspace.authcenter.service.UserService;
import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@Api(value="ObjectService-AC",tags = "用户中心")
@RestController
@RequestMapping("/UC")
public class UserController {
    @Autowired
    private UserService userService;
    
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @ApiOperation(value="邮箱验证",notes = "发送邮件验证码",httpMethod = "GET")
    @ApiImplicitParam(paramType = "path",name="userEmail",value = "邮箱",dataType = "String")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    @Async
    @GetMapping("/mailVerify/{userEmail}")
    @ResponseBody
    public void mailVerify(@PathVariable String userEmail){
        if(userService.sendEmail(userEmail)) logger.info("验证码邮件发送成功");
        else logger.info("验证码邮件发送失败");
    }

    @ApiOperation(value="获取用户数量",notes = "获取当前已注册用户数量",httpMethod = "GET")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    @GetMapping("/userCount")
    @ResponseBody
    public ResponseMap<Integer> userCount(){
        ResponseMap<Integer> responseMap = new ResponseMap<>();
        Integer userCount = userService.getUserCount();
        if(userCount!=null){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(userCount);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }
}

