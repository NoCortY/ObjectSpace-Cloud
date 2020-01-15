package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.service.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            logger.error("注册服务器出现异常");
            logger.error("异常信息:{}",e.getMessage());
        }
        if(effectiveNum>0) return false;
        else return true;
    }
}
