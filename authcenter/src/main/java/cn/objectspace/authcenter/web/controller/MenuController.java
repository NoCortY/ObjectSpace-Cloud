package cn.objectspace.authcenter.web.controller;

import cn.objectspace.authcenter.pojo.dto.MenuDto;
import cn.objectspace.authcenter.service.MenuService;
import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.common.util.HttpRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    MenuService menuService;

    /**
     * @Description: 初始化菜单
     * @Param: [request]
     * @return: cn.objectspace.common.pojo.entity.ResponseMap<java.util.Map<java.lang.String,cn.objectspace.authcenter.pojo.dto.MenuDto>>
     * @Author: NoCortY
     * @Date: 2020/1/3
     */
    @GetMapping("/initMenu")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    public ResponseMap<Map<String, Map<String, MenuDto>>> init(HttpServletRequest request){
        ResponseMap<Map<String, Map<String, MenuDto>>> responseMap = new ResponseMap<>();
        String uuid = HttpRequestUtil.getCookieValue(request,ConstantPool.Shiro.AC_UUID);
        String page = HttpRequestUtil.getStringParameter(request,"page");
        Map<String, Map<String, MenuDto>> menu = menuService.getMenuList(page,uuid);
        responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
        responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
        responseMap.setData(menu);
        return responseMap;
    }
    @GetMapping("/initStatic")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    public ResponseMap<Map<String,MenuDto>> initStatic(HttpServletRequest request){
    	ResponseMap<Map<String,MenuDto>> responseMap = new ResponseMap<Map<String,MenuDto>>();
    	String page = HttpRequestUtil.getStringParameter(request,"page");
    	Map<String,MenuDto> staticInfo = menuService.getStatic(page);
    	responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
        responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
        responseMap.setData(staticInfo);
        return responseMap;
    }

    @GetMapping("/pageMenu")
    @SaveLog(applicationId = ConstantPool.Shiro.APPLICATION_ID)
    public ResponseMap<List<MenuDto>> pageMenu(HttpServletRequest request){
        ResponseMap<List<MenuDto>> responseMap = new ResponseMap<>();
        String page = HttpRequestUtil.getStringParameter(request,"page");
        String classify = HttpRequestUtil.getStringParameter(request,"classify");
        String uuid = HttpRequestUtil.getCookieValue(request,ConstantPool.Shiro.AC_UUID);
        List<MenuDto> menuDtoList = menuService.getPageMenu(page,classify,uuid);
        if(menuDtoList!=null){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(menuDtoList);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }
    
    
}
