package cn.objectspace.componentcenter.service;

import cn.objectspace.componentcenter.pojo.dto.LinuxFile;
import cn.objectspace.componentcenter.pojo.dto.WebSSHDataDto;
import com.jcraft.jsch.Session;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletOutputStream;
import java.util.List;

/**
 * @Description: 使用sftp上传下载文件
 * @Author: NoCortY
 * @Date: 2020/4/10
 */
public interface SFTPService {
    /**
     * @Description: 初始化ssh连接
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/3/7
     */
    public Session initConnection(String userId, WebSSHDataDto webSSHDataDto) throws Exception;

    /**
     * @Description: 获取文件列表
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/11
     */
    public List<LinuxFile> ls(Session session, String path);


    /**
     * @Description: 上传文件
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/10
     */
    public boolean uploadFile(Session session, String targetPath, CommonsMultipartFile uploadFile);

    /**
     * @Description: 下载文件
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/10
     */
    public boolean downloadFile(Session session, String filePath, String fileName, ServletOutputStream outputStream);

    /**
     * @Description: 删除文件
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/13
     */
    public boolean removeFile(Session session, String filePath, String fileName);

    /**
     * @Description: 删除目录
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/13
     */
    public boolean removeDir(Session session, String filePath);

    /**
     * @Description: 新建文件夹
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/14
     */
    public boolean mkdir(Session session, String filePath, String dirName);

    /**
     * @Description: 新建空白文件
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/14
     */
    //public boolean touch(Session session,String filePath,String fileName);

    /**
     * @Description: 修改文件权限
     * @Param:
     * @return:
     * @Author: NoCortY
     * @Date: 2020/4/14
     */
    public boolean chmod(Session session, String filePath, String permission);
}
