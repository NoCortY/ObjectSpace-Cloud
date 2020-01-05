package cn.objectspace.authcenter.service.impl;

import cn.objectspace.authcenter.dao.MenuDao;
import cn.objectspace.authcenter.pojo.dto.MenuDto;
import cn.objectspace.authcenter.pojo.entity.CloudUser;
import cn.objectspace.authcenter.service.MenuService;
import cn.objectspace.common.util.SerializeUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    MenuDao menuDao;
    @Override
    public Map<String,Map<String,MenuDto>> getMenuList(String page, String uuid) {
        Map<String,MenuDto> parentMenu = new LinkedHashMap<String,MenuDto>();
        Map<String,Map<String,MenuDto>> resMap = new LinkedHashMap<>();
        Session currentSession = SecurityUtils.getSubject().getSession();
        CloudUser currentUser = (CloudUser) SerializeUtil.unSerialize((byte[]) currentSession.getAttribute(uuid));
        String userEmail = currentUser.getUserEmail();
        /*************************************获取菜单********************************************/
        List<Integer> roleIdList = menuDao.queryRole(userEmail);
        for(Integer roleId : roleIdList){
            //取出所有角色对应的菜单
            List<MenuDto> menuDtoList = menuDao.queryMenu(roleId,page);
            for(MenuDto menuDto:menuDtoList){
                menuDto.setChild(getSubMenuList(roleId,menuDto.getId()));
                parentMenu.put(menuDto.getCategory(),menuDto);
            }
        }
        resMap.put("menuInfo", parentMenu);
        /****************************************************************************************/
        return resMap;
    }
    /**
     * @Description: 获取无限子代的菜单
     * @Param: [roleId, parentId]
     * @return: java.util.List<cn.objectspace.authcenter.pojo.dto.MenuDto>
     * @Author: NoCortY
     * @Date: 2020/1/3
     */
    public List<MenuDto> getSubMenuList(Integer roleId,Integer parentId){
        List<MenuDto> subMenuList = menuDao.querySubMenu(roleId,parentId);
        if(subMenuList==null||subMenuList.size()<=0){
            return null;
        }else{
            for(MenuDto subMenu:subMenuList){
                subMenu.setChild(getSubMenuList(roleId,subMenu.getId()));
            }
            return subMenuList;
        }
    }
	@Override
	public Map<String, MenuDto> getStatic(String page) {
		Map<String,MenuDto> resMap = new LinkedHashMap<String,MenuDto>();
        /*************************************获取首页********************************************/
        MenuDto homeMenu = menuDao.queryStatic(page,"home");
        resMap.put("homeInfo", homeMenu);
        MenuDto logo = menuDao.queryStatic(page, "logo");
        resMap.put("logoInfo", logo);
        /*****************************************************************************************/
        return resMap;
	}
}
