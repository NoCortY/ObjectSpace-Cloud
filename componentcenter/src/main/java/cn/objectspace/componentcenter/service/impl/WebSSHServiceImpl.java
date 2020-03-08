package cn.objectspace.componentcenter.service.impl;

import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.componentcenter.pojo.dto.WebSSHDataDto;
import cn.objectspace.componentcenter.pojo.entity.CloudSSH;
import cn.objectspace.componentcenter.service.WebSSHService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: WebSSH Logic impl
 * @Author: NoCortY
 * @Date: 2020/3/7
 */
@Service
public class WebSSHServiceImpl implements WebSSHService {
    //存放ssh连接信息的map
    private static Map<String, Object> sshMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(WebSSHServiceImpl.class);
    //线程池
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * @Description: 初始化连接
     * @Param: [session]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    @Override
    public void initConnection(WebSocketSession session) {
        JSch jSch = new JSch();
        CloudSSH cloudSSH = new CloudSSH();
        cloudSSH.setjSch(jSch);
        cloudSSH.setWebSocketSession(session);
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY));
        //将这个ssh连接信息放入map中
        sshMap.put(userId, cloudSSH);
    }

    /**
     * @Description: 处理客户端发送的数据
     * @Param: [buffer, session]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    @Override
    public void recvHandle(String buffer, WebSocketSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        WebSSHDataDto webSSHData = null;
        try {
            webSSHData = objectMapper.readValue(buffer, WebSSHDataDto.class);
        } catch (IOException e) {
            logger.error("Json转换异常");
            logger.error("异常信息:{}", e.getMessage());
            return;
        }
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY));
        if (ConstantPool.ComponentCenter.WEBSSH_OPERATE_CONNECT.equals(webSSHData.getOperate())) {
            //找到ssh连接对象
            CloudSSH cloudSSH = (CloudSSH) sshMap.get(userId);
            //启动线程异步处理
            WebSSHDataDto finalWebSSHData = webSSHData;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        connectToSSH(cloudSSH, finalWebSSHData, session);
                    } catch (JSchException | IOException e) {
                        logger.error("webssh连接异常");
                        logger.error("异常信息:{}", e.getMessage());
                        close(session);
                    }
                }
            });
        } else if (ConstantPool.ComponentCenter.WEBSSH_OPERATE_COMMAND.equals(webSSHData.getOperate())) {
            String command = webSSHData.getCommand();
            CloudSSH cloudSSH = (CloudSSH) sshMap.get(userId);
            if (cloudSSH != null) {
                try {
                    transToSSH(cloudSSH.getChannel(), command);
                } catch (IOException e) {
                    logger.error("webssh连接异常");
                    logger.error("异常信息:{}", e.getMessage());
                    close(session);
                }
            }
        } else {
            logger.error("不支持的操作");
            close(session);
        }
    }

    @Override
    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    @Override
    public void close(WebSocketSession session) {
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY));
        CloudSSH cloudSSH = (CloudSSH) sshMap.get(userId);
        if (cloudSSH != null) {
            //断开连接
            if (cloudSSH.getChannel() != null) cloudSSH.getChannel().disconnect();
            //map中移除
            sshMap.remove(userId);
        }
    }

    /**
     * @Description: 使用jsch连接终端
     * @Param: [cloudSSH, webSSHData, webSocketSession]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    private void connectToSSH(CloudSSH cloudSSH, WebSSHDataDto webSSHData, WebSocketSession webSocketSession) throws JSchException, IOException {
        Session session = null;
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //获取jsch的会话
        session = cloudSSH.getjSch().getSession(webSSHData.getUsername(), webSSHData.getHost(), webSSHData.getPort());
        session.setConfig(config);
        //设置密码
        session.setPassword(webSSHData.getPassword());
        //连接  超时时间30s
        session.connect(30000);

        //开启shell通道
        Channel channel = session.openChannel("shell");

        //通道连接 超时时间3s
        channel.connect(3000);

        //设置channel
        cloudSSH.setChannel(channel);

        //转发消息
        transToSSH(channel, "\r");

        //读取终端返回的信息流
        InputStream inputStream = channel.getInputStream();
        try {
            //循环读取
            byte[] buffer = new byte[1024];
            int i = 0;
            while ((i = inputStream.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }

        } finally {
            //关闭会话
            session.disconnect();
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    /**
     * @Description: 将消息转发到终端
     * @Param: [channel, data]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    private void transToSSH(Channel channel, String command) throws IOException {
        if (channel != null) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }


}
