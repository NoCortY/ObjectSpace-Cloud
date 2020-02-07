package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.service.ServerService;
import org.apache.catalina.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ServerServiceImpl implements ServerService {
    @Autowired
    ComponentDao componentDao;
    Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);
    @Override
    public boolean registerServer(CloudServer cloudServer) {
        if(cloudServer==null||cloudServer.getServerIp()==null||cloudServer.getServerIp().length()==0) return false;
        int effectiveNum = 0;
        try{
            effectiveNum = componentDao.insertCloudServer(cloudServer);
        }catch (Exception e){
            System.out.println(effectiveNum);
            logger.error("注册服务器出现异常");
            logger.error("异常信息:{}",e.getMessage());
            e.printStackTrace();
            return false;
        }
        if(effectiveNum<=0) return false;
        else return true;
    }

    @Override
    public List<CloudServer> getMySelfServer(Integer userId) {
        if(userId==null){
            logger.info("用户ID为空是非法操作");
            return null;
        }
        return componentDao.queryServerByUserId(userId);
    }
}
