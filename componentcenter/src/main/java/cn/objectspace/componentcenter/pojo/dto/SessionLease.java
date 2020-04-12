package cn.objectspace.componentcenter.pojo.dto;

import com.jcraft.jsch.Session;

/**
 * @Description: SFTP Session租债器
 * @Author: NoCortY
 * @Date: 2020/4/12
 */
public class SessionLease {
    //session
    private Session session;
    //过期session的时间戳
    private long expireTimeMillis;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public long getExpireTimeMillis() {
        return expireTimeMillis;
    }

    public void setExpireTimeMillis(long expireTimeMillis) {
        this.expireTimeMillis = expireTimeMillis;
    }
}
