package cn.objectspace.authcenter.dao;

import cn.objectspace.authcenter.pojo.dto.MenuDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuDao {
    public List<MenuDto> queryMenu(@Param("roleId") Integer roleId, @Param("page") String page);
    public MenuDto queryHome(@Param("page")String page);
    public List<MenuDto> querySubMenu(@Param("roleId")Integer roleId,@Param("parentId")Integer parentId);
    public List<Integer> queryRole(@Param("userEmail") String userEmail);
}
