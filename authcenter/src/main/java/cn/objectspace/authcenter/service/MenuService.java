package cn.objectspace.authcenter.service;

import cn.objectspace.authcenter.pojo.dto.MenuDto;

import java.util.Map;

public interface MenuService {
    public Map<String, Map<String, MenuDto>> getMenuList(String page, String uuid);
    public Map<String,MenuDto> getStatic(String page);
}
