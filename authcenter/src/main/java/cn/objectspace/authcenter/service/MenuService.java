package cn.objectspace.authcenter.service;

import cn.objectspace.authcenter.pojo.dto.MenuDto;

import java.util.Map;

public interface MenuService {
    public Map<String,MenuDto> getMenuList(String page, String uuid);
}
