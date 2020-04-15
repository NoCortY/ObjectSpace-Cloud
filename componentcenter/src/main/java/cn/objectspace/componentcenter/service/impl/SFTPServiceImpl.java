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
import java.util.*;
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
    private Logger logger = LoggerFactory.getLogger(SFTPServiceImpl.class);

    private static Map<String, SessionLease> sessionMap = new ConcurrentHashMap<>();
    @Override
    public Session initConnection(String userId, WebSSHDataDto webSSHDataDto) throws Exception {
        ServerSSHInfoDto serverSSHInfoDto = componentDao.queryServerSSHInfoByUserIdAndServerIp(Integer.valueOf(userId), webSSHDataDto.getHost());
        if (serverSSHInfoDto == null) {
            logger.info("该用户没有该服务器的控制信息");
            return null;
        }
        logger.info("stfp连接:IP:{},用户名:{}", webSSHDataDto.getHost(), serverSSHInfoDto.getSshUser());
        long l = System.currentTimeMillis();
        String sessionCacheKey = userId + ":" + webSSHDataDto.getHost();

        //懒淘汰策略
        //如果缓存中有，直接从缓存中返回
        if (sessionMap.containsKey(sessionCacheKey)) {
            SessionLease sessionLease = sessionMap.get(sessionCacheKey);
            //首先检查是否过期
            if (System.currentTimeMillis() <= sessionLease.getExpireTimeMillis()) {
                //如果没有过期，那么就返回，同时给这个缓存中的过期时间续约
                sessionLease.setExpireTimeMillis(System.currentTimeMillis() + 10 * 60 * 1000);
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
        sessionLease.setExpireTimeMillis(System.currentTimeMillis() + 10 * 60 * 1000);
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

                if (".".equals(file.getFilename()) || "..".equals(file.getFilename())) continue;

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
    public synchronized boolean uploadFile(Session session, String targetPath, CommonsMultipartFile uploadFile) {
        if (session == null || StringUtils.isBlank(targetPath)) {
            logger.info("不能为null");
            return false;
        }
        //防止文件名字中带有特殊字符导致上传后无法正常读取
        String fileName = uploadFile.getOriginalFilename().replace(" ", "_");
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
            outStream = channelSftp.put(fileName);
            inputStream = uploadFile.getInputStream();
            byte b[] = new byte[1024];
            int n;
            //流的对拷
            while ((n = inputStream.read(b)) != -1) {
                outStream.write(b, 0, n);
            }

            logger.info("上传文件到{}成功,路径:{}", session.getHost(), targetPath);

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

            /*Vector v = channelSftp.ls(filePath);
            for(int i=0;i<v.size();i++){
                System.out.println(v.get(i));
            }*/
            channelSftp.get(filePath + fileName, outputStream);

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

    @Override
    public synchronized boolean removeFile(Session session, String filePath, String fileName) {
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
        try {
            //移动到文件目录
            channelSftp.cd(filePath);

            //删除文件
            channelSftp.rm(fileName);

            logger.info("删除{}成功", filePath + fileName);
            return true;
        } catch (SftpException e) {
            logger.error("删除文件异常");
            logger.error("异常信息:{}", e.getMessage());
            return false;
        } finally {
            channel.disconnect();
        }
    }

    /**
     * @Description: 删除目录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/13
     */
    @Override
    public synchronized boolean removeDir(Session session, String filePath) {
        if (session == null || StringUtils.isBlank(filePath)) {
            logger.info("不能为空");
            return false;
        }
        //要删除的空文件夹集合
        List<String> toBeDeleteEmptyDirContainer = new ArrayList<>();
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
        try {
            if (isDirectory(channelSftp, filePath)) {
                //那么使用递归删除
                if (rm(channelSftp, filePath, toBeDeleteEmptyDirContainer)) {
                    //如果递归删除成功,那么就循环遍历删除集合中的内容
                    for (int i = toBeDeleteEmptyDirContainer.size() - 1; i >= 0; --i) {
                        channelSftp.rmdir(toBeDeleteEmptyDirContainer.get(i));
                    }
                }

            } else {
                //移动到文件目录
                channelSftp.rm(filePath);
                logger.info("删除{}成功", filePath);
                return true;
            }
        } catch (SftpException e) {
            logger.error("删除文件夹失败");
            return false;
        } finally {
            channel.disconnect();
        }
        return true;
    }

    @Override
    public boolean mkdir(Session session, String filePath, String dirName) {
        if (session == null || StringUtils.isBlank(filePath) || StringUtils.isBlank(dirName)) {
            logger.info("mkdir某些必要条件缺失");
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
        try {
            channelSftp.mkdir(filePath + dirName);
        } catch (SftpException e) {
            logger.error("新建目录异常");
            logger.error("异常信息:{}", e.getMessage());
            return false;
        }

        logger.info("新建目录成功。IP:{} 路径:{}", session.getHost(), filePath + "/" + dirName);
        return true;
    }

    @Override
    public boolean chmod(Session session, String filePath, String permission) {
        if (session == null || StringUtils.isBlank(filePath)) {
            logger.info("chmod某些必要条件缺失");
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
        try {
            channelSftp.chmod(Integer.parseInt(permission, 8), filePath);
        } catch (SftpException e) {
            logger.error("修改权限异常");
            logger.error("异常信息:{}", e.getMessage());
            return false;
        }

        logger.info("新建目录成功。IP:{} 路径:{}", session.getHost(), filePath + "/" + filePath);
        return true;
    }

   /* @Override
    public boolean touch(Session session, String filePath, String fileName) {
        if(session==null|| StringUtils.isBlank(filePath)||StringUtils.isBlank(fileName)){
            logger.info("touch某些必要条件缺失");
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

        try {
            //暂无方法可以新建文件
            channelSftp.
        } catch (SftpException e) {
            logger.error("新建空白文件异常");
            logger.error("异常信息:{}",e.getMessage());
            return false;
        }

        logger.info("新建文件成功。IP:{} 路径:{}",session.getHost(),filePath+"/"+fileName);
        return true;
    }*/

    /**
     * @Description: 递归删除，用于删除目录，先删除文件夹中的东西，然后统一删除文件夹
     * @Param: [channelSftp, filePath,toBeDeleteEmptyDirContainer(要删除的空文件夹集合)]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/4/13
     */
    private boolean rm(ChannelSftp channelSftp, String filePath, List<String> toBeDeleteEmptyDirContainer) throws SftpException {
        if (channelSftp == null) {
            logger.error("channelSftp为空");
            return false;
        }
        if (!isDirectory(channelSftp, filePath)) {
            //如果不是文件夹,那么直接调用rm删除
            channelSftp.rm(filePath);
            return true;
        }
        Vector<ChannelSftp.LsEntry> ls = channelSftp.ls(filePath);
        if (isDirectory(channelSftp, filePath) && ls.size() == 2) {
            //如果是文件夹，且文件夹内没有任何文件（只有.和..）
            //那么直接使用rmdir删除即可
            channelSftp.rmdir(filePath);
            return true;
        }

        //如果是文件夹且里面有文件，则先加入空文件夹集合中，删除其中的文件
        toBeDeleteEmptyDirContainer.add(filePath);

        for (ChannelSftp.LsEntry entry : ls) {
            //如果里面存在文件，那么就需要以依次遍历
            //如果遇到.或者..那么不处理
            if ("..".equals(entry.getFilename()) || ".".equals(entry.getFilename()))
                continue;
            String fp = filePath + "/" + entry.getFilename();
            if (isDirectory(channelSftp, fp)) {
                //如果文件夹中的这个文件是文件夹，则递归进入
                rm(channelSftp, fp, toBeDeleteEmptyDirContainer);
            } else {
                //如果文件夹中的这个文件不是文件夹，而是文件，则直接删除
                channelSftp.rm(fp);
            }
        }
        return true;
    }

    /**
     * @Description: 判断是否是目录
     * @Param: [path]
     * @return: boolean
     * @Author: NoCortY
     * @Date: 2020/4/13
     */
    private boolean isDirectory(ChannelSftp channelSftp, String path) {
        try {
            channelSftp.cd(path);
            //如果可以cd进去，就是文件夹
            return true;
        } catch (SftpException e) {
            //否则就不是文件夹
            return false;
        }
    }
}
