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
}
