package cn.objectspace.componentcenter.service;

import cn.objectspace.componentcenter.pojo.dto.CloudServerCommandExecuteRecordDto;
import cn.objectspace.componentcenter.pojo.dto.ExecuteCommandReturnDto;
import cn.objectspace.componentcenter.pojo.dto.SimpleCommandDto;
import cn.objectspace.componentcenter.pojo.dto.WebSSHDataDto;
import com.jcraft.jsch.Session;

import java.util.List;

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
    public List<ExecuteCommandReturnDto> groupCommand(List<Session> sessions, String command, Integer userId);

    /**
     * @Description: 获取服务器执行命令记录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public List<CloudServerCommandExecuteRecordDto> getServerCommandExecuteRecord(Integer userId);

    /**
     * @Description: 获取常用命令集合
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public List<SimpleCommandDto> getSimpleCommands(Integer userId);

    /**
     * @Description: 新增常用命令
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public boolean addSimpleCommand(String commandName, String commandContent, Integer userId);


    /**
     * @Description: 删除常用命令
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/16
     */
    public boolean removeSimpleCommand(Integer commandId, Integer userId);
}
