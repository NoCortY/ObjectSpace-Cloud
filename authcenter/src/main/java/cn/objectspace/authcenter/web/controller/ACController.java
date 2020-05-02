package cn.objectspace.authcenter.web.controller;

import cn.objectspace.authcenter.pojo.dto.AuthDto;
import cn.objectspace.authcenter.pojo.dto.URPDto;
import cn.objectspace.authcenter.pojo.entity.CloudUser;
import cn.objectspace.authcenter.pojo.vo.LoginUserVO;
import cn.objectspace.authcenter.pojo.vo.RegisterUserVO;
import cn.objectspace.authcenter.service.ACService;
import cn.objectspace.authcenter.service.UserService;
import cn.objectspace.authcenter.util.CaptchaUtil;
import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.common.util.HttpRequestUtil;
import cn.objectspace.common.util.HttpResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
* @Description: 认证中心控制器
* @Author: NoCortY
* @Date: 2019/12/19
*/
@Api(value="ObjectService-AC",tags = "认证中心")
@RestController
@RequestMapping("/AC")
public class ACController {
    @Autowired
    private ACService acService;
    @Autowired
    private UserService userService;
    Logger logger = LoggerFactory.getLogger(ACController.class);

    /**
     * @Description:  注册
     * @Param: [request]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @ApiOperation(value="注册",notes = "用户注册接口",httpMethod = "POST")
    @ApiImplicitParam(paramType = "body",name="registerUser",value = "注册用户信息",dataType = "RegisterUserVO")
    @PostMapping("/register")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    public ResponseMap<AuthDto> register(@Valid @RequestBody RegisterUserVO registerUser){
        ResponseMap<AuthDto> responseMap = new ResponseMap<>();

        //验证码是否正确？
        if(!userService.checkVerifyCode(registerUser.getUserEmail(),registerUser.getEmailVerifyCode())){
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            return responseMap;
        }
        CloudUser cloudUser = new CloudUser();
        BeanUtils.copyProperties(registerUser,cloudUser);
        AuthDto authDto = acService.userRegister(cloudUser);
        if(ConstantPool.Shiro.AC_SUCCESS_CODE.equals(authDto.getAuthCode())){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(authDto);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }

    /**
     * @Description:  登录
     * @Param: [request, response]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @ApiOperation(value="登录",notes = "用户登录接口，需要传递用户邮箱和密码",httpMethod = "POST")
    @ApiParam(value="登录用户信息",required = true)
    @ApiImplicitParam(paramType = "body",name="loginUser",value = "登录用户信息",dataType = "LoginUserVO")
    @PostMapping("/login")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    public ResponseMap<AuthDto> login(@Valid @RequestBody LoginUserVO loginUser, HttpServletResponse response, HttpSession session){
        //定义接口返回通用对象
        ResponseMap<AuthDto> responseMap= new ResponseMap<AuthDto>();
        if(!CaptchaUtil.veriry(session,loginUser.getCaptcha())){
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage("验证码错误");
            return responseMap;
        }
        AuthDto authDto = null;
        CloudUser currentUser = new CloudUser();
        //将VO转换为Domain
        BeanUtils.copyProperties(loginUser,currentUser);
        authDto = acService.userLogin(currentUser);
        if(ConstantPool.Shiro.AC_SUCCESS_CODE.equals(authDto.getAuthCode())) {
            //如果认证成功了，就设置Cookie
            HttpResponseUtil.addCookie(response, ConstantPool.Shiro.AC_UUID, authDto.getUuid());
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(authDto);
        }else{
            //否则返回错误
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }
    /**
     * @Description:  认证
     * @Param: [request, response]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @ApiOperation(value="认证",notes = "访问微服务需要获取token令牌，只有在登录后进行认证，才可以正确获取token",httpMethod = "POST")
    @PostMapping("/authentication")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    public ResponseMap<String> authentication(HttpServletRequest request, HttpServletResponse response){
        ResponseMap<String> responseMap = new ResponseMap<>();
        String acid = HttpRequestUtil.getCookieValue(request,ConstantPool.Shiro.AC_UUID);
        AuthDto authDto = acService.authenticationInfo(acid);
        responseMap.setCode(authDto.getAuthCode());
        responseMap.setMessage(authDto.getAuthMessage());
        responseMap.setData(authDto.getToken());
        //如果认证成功了那么就直接设置一个token存入cookie给用户
        //如果认证成功了，就设置Cookie(该方法用网关会造成已发出请求到网关执行过程中设置cookie，下游服务无法获取刚设置上的cookie)
        /*if(ConstantPool.Shiro.AC_SUCCESS_CODE.equals(authDto.getAuthCode()))
            HttpResponseUtil.addCookie(response,ConstantPool.Shiro.AC_TOKEN,authDto.getToken());*/
        return responseMap;
    }
    /**
     * @Description:  授权
     * @Param: [request, response]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: NoCortY
     * @Date: 2019/12/19
     */
    @ApiOperation(value="授权",notes = "用户访问某个微服务之前先为它授权，授权需要给出当前微服务的applicationId",httpMethod = "POST")
    @ApiImplicitParam(paramType = "path",name="applicationId",value = "微服务ID",dataType = "String")
    @PostMapping("/authorization/{applicationId}/{ACToken}")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    public ResponseMap<URPDto> authorization(@PathVariable Integer applicationId, @PathVariable String ACToken){
        ResponseMap<URPDto> responseMap = new ResponseMap<>();
        //2020/2/7 修改为通过REST参数获取cookie，解决zuul中设置cookie无法读取的问题
        //String token = HttpRequestUtil.getCookieValue(request,ConstantPool.Shiro.AC_TOKEN);
        URPDto urpDto = acService.authorizationInfo(ACToken,applicationId);
        if(urpDto==null){
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(urpDto);
        }
        //删除cookie
        //HttpResponseUtil.deleteCookie(response,ConstantPool.Shiro.AC_TOKEN);
        return responseMap;
    }

    @ApiOperation(value = "Token销毁", notes = "没有使用到的Token及时进行销毁", httpMethod = "POST")
    @ApiImplicitParam(paramType = "path", name = "applicationId", value = "微服务ID", dataType = "String")
    @PostMapping("/destroyToken/{ACToken}")
    @Async
    public void destroyToken(@PathVariable String ACToken) {
        acService.tokenDestroy(ACToken);
    }
}
