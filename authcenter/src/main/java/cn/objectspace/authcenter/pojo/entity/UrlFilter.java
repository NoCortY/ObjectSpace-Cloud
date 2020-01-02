package cn.objectspace.authcenter.pojo.entity;

import java.io.Serializable;

public class UrlFilter implements Serializable {
    private static final long serialVersionUID = -8408926346426638791L;
    private String url;
    private String filter;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
