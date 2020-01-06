package cn.objectspace.authcenter.service;

import cn.objectspace.authcenter.pojo.dto.MenuDto;

import java.util.List;
import java.util.Map;

public interface MenuService {
    public Map<String, Map<String, MenuDto>> getMenuList(String page, String uuid);
    public Map<String,MenuDto> getStatic(String page);

    public List<MenuDto> getPageMenu(String page, String classify,String uuid);
    
}
