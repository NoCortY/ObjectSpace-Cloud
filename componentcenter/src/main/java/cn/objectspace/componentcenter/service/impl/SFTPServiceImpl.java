package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.componentcenter.dao.ComponentDao;
import cn.objectspace.componentcenter.pojo.dto.LinuxFile;
import cn.objectspace.componentcenter.pojo.dto.ServerSSHInfoDto;
import cn.objectspace.componentcenter.pojo.dto.SessionLease;
import cn.objectspace.componentcenter.pojo.dto.WebSSHDataDto;
import cn.objectspace.componentcenter.service.SFTPService;
import com.jcraft.jsch.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: sftp
 * @Author: NoCortY
 * @Date: 2020/4/11
 */
@Service
public class SFTPServiceImpl implements SFTPService {
    @Autowired
    private ComponentDao componentDao;
    private Logger logger = LoggerFactory.getLogger(WebSSHServiceImpl.class);

    private static Map<String, SessionLease> sessionMap = new ConcurrentHashMap<>();
    @Override
    public Session initConnection(String userId, WebSSHDataDto webSSHDataDto) throws Exception {
        ServerSSHInfoDto serverSSHInfoDto = componentDao.queryServerSSHInfoByUserIdAndServerIp(Integer.valueOf(userId), webSSHDataDto.getHost());
        logger.info("stfp连接:IP:{},用户名:{}", webSSHDataDto.getHost(), serverSSHInfoDto.getSshUser());
        long l = System.currentTimeMillis();
        String sessionCacheKey = userId + ":" + webSSHDataDto.getHost();

        //懒淘汰策略
        //如果缓存中有，直接从缓存中返回
        if (sessionMap.containsKey(sessionCacheKey)) {
            SessionLease sessionLease = sessionMap.get(sessionCacheKey);
            //首先检查是否过期
            if (System.currentTimeMillis() <= sessionLease.getExpireTimeMillis()) {
                //如果没有过期，那么就返回
                return sessionLease.getSession();
            } else {
                //断开连接
                sessionMap.get(sessionCacheKey).getSession().disconnect();
                //如果过期了，就从缓存中删除，然后设置新的连接
                sessionMap.remove(sessionCacheKey);
            }
        }

        JSch jsch = new JSch();
        Session session = jsch.getSession(serverSSHInfoDto.getSshUser(), webSSHDataDto.getHost());
        if (session == null) {
            logger.error("无法连接该服务器");
            throw new Exception("无法连接该服务器");
        }
        //设置密码
        session.setPassword(serverSSHInfoDto.getSshPassword());

        //设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
//
//        //设置登陆超时时间
        session.connect(30000);
        //加入缓存
        SessionLease sessionLease = new SessionLease();
        //过期时间为30min
        sessionLease.setExpireTimeMillis(System.currentTimeMillis() + 30 * 60 * 1000);
        sessionLease.setSession(session);
        sessionMap.put(sessionCacheKey, sessionLease);
        //返回session
        return session;
    }

    @Override
    public List<LinuxFile> ls(Session session, String path) {
        if (session == null || StringUtils.isBlank(path)) {
            logger.info("session或者path不能为空");
            return null;
        }
        Channel channel = null;
        //打开stfp通道
        try {
            channel = session.openChannel("sftp");
            channel.connect(1000);
        } catch (JSchException e) {
            logger.error("sftp channel创建失败");
            logger.error("异常信息:{}", e.getMessage());
            return null;
        }


        ChannelSftp channelSftp = (ChannelSftp) channel;
        List<LinuxFile> linuxFileList = new LinkedList<>();
        try {
            channelSftp.cd(path);
            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(path);
            for (ChannelSftp.LsEntry file : files) {
                String[] s = file.getLongname().replaceAll("\\s{1,}", " ").split(" ");
                LinuxFile linuxFile = new LinuxFile();
                linuxFile.setType(s[0].substring(0, 1));
                linuxFile.setPower(s[0].substring(1));
                linuxFile.setLinkOrDirNum(s[1]);
                linuxFile.setUserName(s[2]);
                linuxFile.setGroupName(s[3]);
                linuxFile.setSize(Long.valueOf(s[4]));
                linuxFile.setLastModify(s[5] + " " + s[6] + " " + s[7]);
                linuxFile.setFileName(s[8]);
                linuxFileList.add(linuxFile);
            }
        } catch (SftpException e) {
            logger.error("移动到该目录异常");
            logger.error("异常信息:{}", e.getMessage());
            return null;
        } finally {
            channel.disconnect();
        }
        return linuxFileList;
    }

    @Override
    public boolean uploadFile(Session session, String targetPath, CommonsMultipartFile uploadFile) {
        if (session == null || StringUtils.isBlank(targetPath)) {
            logger.info("不能为null");
            return false;
        }
        Channel channel = null;
        //打开stfp通道
        try {
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            logger.error("sftp channel创建失败");
            logger.error("异常信息:{}", e.getMessage());
            return false;
        }


        ChannelSftp channelSftp = (ChannelSftp) channel;
        OutputStream outStream = null;
        InputStream inputStream = null;
        try {
            channelSftp.cd(targetPath);
            outStream = channelSftp.put(uploadFile.getOriginalFilename());
            inputStream = uploadFile.getInputStream();
            byte b[] = new byte[1024];
            int n;
            //流的对拷
            while ((n = inputStream.read(b)) != -1) {
                outStream.write(b, 0, n);
            }

            logger.info("上传文件到{}成功", session.getHost());

            return true;
        } catch (SftpException | IOException e) {
            logger.error("上传文件异常");
            logger.error("异常信息:{}", e.getMessage());
            return false;
        } finally {
            try {
                if (outStream != null) {
                    outStream.flush();
                    outStream.close();
                }
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                logger.error("流关闭异常,异常信息:{}", e.getMessage());
            }
            channel.disconnect();
        }
    }

    @Override
    public boolean downloadFile(Session session, String filePath, String fileName, ServletOutputStream outputStream) {
        if (session == null || StringUtils.isBlank(filePath) || StringUtils.isBlank(fileName)) {
            logger.info("不能为null");
            return false;
        }
        Channel channel = null;
        //打开stfp通道
        try {
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            logger.error("sftp channel创建失败");
            logger.error("异常信息:{}", e.getMessage());
            return false;
        }


        ChannelSftp channelSftp = (ChannelSftp) channel;
        InputStream inputStream = null;
        try {

            channelSftp.cd(filePath);
            /*Vector v = channelSftp.ls(filePath);
            for(int i=0;i<v.size();i++){
                System.out.println(v.get(i));
            }*/
            channelSftp.get(fileName, outputStream);

            logger.info("获取该文件下载流成功");
            return true;
        } catch (SftpException e) {
            logger.error("下载文件异常");
            logger.error("异常信息:{}", e.getMessage());
            return false;
        } finally {
            channel.disconnect();
        }
    }
}
