package cn.objectspace.componentcenter.service;

import cn.objectspace.componentcenter.pojo.dto.WebSSHDataDto;
import com.jcraft.jsch.Session;

import java.util.List;
import java.util.Map;

/**
 * @Description: 向服务器发送命令
 * @Author: NoCortY
 * @Date: 2020/4/14
 */
public interface SSHService {
    /**
     * @Description: 实例化连接
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/14
     */
    public Session initConnection(String userId, WebSSHDataDto webSSHDataDto) throws Exception;

    /**
     * @Description: 命令群发
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/14
     */
    public Map<String, String> groupCommand(List<Session> sessions, String command);

}
