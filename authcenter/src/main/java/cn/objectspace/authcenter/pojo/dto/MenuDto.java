package cn.objectspace.authcenter.pojo.dto;

import java.io.Serializable;
import java.util.List;

/**
* @Description: 菜单dto
* @Author: NoCortY
* @Date: 2020/1/2
*/
public class MenuDto implements Serializable {
    private static final long serialVersionUID = -4201981755981668665L;
    private Integer id;
    private String title;
    private String icon;
    private String href;
    private String target;
    private String category;
    private List<MenuDto> child;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<MenuDto> getChild() {
        return child;
    }

    public void setChild(List<MenuDto> child) {
        this.child = child;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
