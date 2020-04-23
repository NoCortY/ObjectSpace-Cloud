package cn.objectspace.componentcenter.controller;

import cn.objectspace.common.annotation.SaveLog;
import cn.objectspace.common.constant.ConstantPool;
import cn.objectspace.common.pojo.entity.ResponseMap;
import cn.objectspace.common.util.HttpRequestUtil;
import cn.objectspace.componentcenter.pojo.dto.*;
import cn.objectspace.componentcenter.pojo.dto.daemon.ServerInfoDto;
import cn.objectspace.componentcenter.pojo.dto.record.CpuRecordGroupDto;
import cn.objectspace.componentcenter.pojo.dto.record.DiskRecordGroupDto;
import cn.objectspace.componentcenter.pojo.dto.record.MemRecordDto;
import cn.objectspace.componentcenter.pojo.dto.record.NetRecordGroupDto;
import cn.objectspace.componentcenter.pojo.entity.CloudServer;
import cn.objectspace.componentcenter.pojo.entity.FutureTask;
import cn.objectspace.componentcenter.service.SFTPService;
import cn.objectspace.componentcenter.service.SSHService;
import cn.objectspace.componentcenter.service.ServerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/CC/server")
public class ServerController {
    @Autowired
    ServerService serverService;
    @Autowired
    SFTPService sftpService;
    @Autowired
    SSHService sshService;
    Logger logger = LoggerFactory.getLogger(ServerController.class);
    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/register")
    public ResponseMap<String> register(HttpServletRequest request) {
        ResponseMap<String> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        String installCommand = ConstantPool.ComponentCenter.COMMAND_FOR_DAEMON_INSTALL + String.valueOf(userId);
        if (userId != null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.ComponentCenter.REGISTER_SERVER_SUCCESS);
            responseMap.setData(installCommand);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.ComponentCenter.REGISTER_SERVER_FALURE);
            responseMap.setData("无法获取您专属的指令,请重新登录");
        }
        return responseMap;
    }

    /**
     * @Description: 获取本人的服务器列表
     * @Param: [request]
     * @return: cn.objectspace.common.pojo.entity.ResponseMap<java.util.List < cn.objectspace.componentcenter.pojo.dto.CloudServerDto>>
     * @Author: NoCortY
     * @Date: 2020/4/15
     */
    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/listMyselfServer")
    public ResponseMap<List<CloudServerDto>> listMyselfServer(HttpServletRequest request) throws JsonProcessingException {
        ResponseMap<List<CloudServerDto>> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        //分页
        Integer page = HttpRequestUtil.getIntegerParameter(request,"page");
        Integer limit = HttpRequestUtil.getIntegerParameter(request,"limit");
        List<CloudServerDto> cloudServerList = serverService.getMySelfServer(userId,page,limit);
        if(cloudServerList!=null){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(cloudServerList);
            responseMap.setCount(serverService.getCountOfMySelfServer(userId));
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }
    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/ping")
    public ResponseMap<String> ping(@RequestBody ServerInfoDto serverInfoDto){
            ResponseMap<String> responseMap = new ResponseMap<>();
        //心跳接收器
        if(serverService.ping(serverInfoDto)){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            //响应ping
            responseMap.setData(ConstantPool.ComponentCenter.PONG);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }
        return responseMap;
    }
    /**
     * @Description: 获取服务器详细信息
     * @Param: [serverIp, request]
     * @return: cn.objectspace.common.pojo.entity.ResponseMap<cn.objectspace.componentcenter.pojo.dto.ServerDetailDto>
     * @Author: NoCortY
     * @Date: 2020/2/14
     */
    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/serverDetail/{serverIp}")
    public ResponseMap<ServerDetailDto> serverDetatil(@PathVariable String serverIp,HttpServletRequest request){
        ResponseMap<ServerDetailDto> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        ServerDetailDto serverDetailDto = serverService.getServerDetail(serverIp,userId);
        if(serverDetailDto==null){
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(serverDetailDto);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/listServerResume")
    public ResponseMap<List<ServerResumeDto>> listServerResume(HttpServletRequest request) {
        ResponseMap<List<ServerResumeDto>> responseMap = new ResponseMap<>();
        List<ServerResumeDto> serverResumeDtoList = serverService.getServerResumes((Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY));
        if (serverResumeDtoList == null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(serverResumeDtoList);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/serverCount")
    public ResponseMap<Integer> serverCount() {
        ResponseMap<Integer> responseMap = new ResponseMap<>();
        Integer count = serverService.getServerCount();
        if (count == null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(count);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/runtimeCpuRecord/{serverIp}/{intervalMinute}")
    public ResponseMap<List<CpuRecordGroupDto>> runtimeCpuRecord(@PathVariable String serverIp, @PathVariable Long intervalMinute, HttpServletRequest request) {
        ResponseMap<List<CpuRecordGroupDto>> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        List<CpuRecordGroupDto> cpuRecordGroupDtoList = serverService.getRuntimeCpuRecord(userId, serverIp, intervalMinute);
        if (cpuRecordGroupDtoList == null || cpuRecordGroupDtoList.isEmpty()) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(cpuRecordGroupDtoList);
        }
        return responseMap;
    }
    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/runtimeDiskRecord/{serverIp}/{intervalMinute}")
    public ResponseMap<List<DiskRecordGroupDto>> runtimeDiskRecord(@PathVariable String serverIp, @PathVariable Long intervalMinute, HttpServletRequest request) {
        ResponseMap<List<DiskRecordGroupDto>> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        List<DiskRecordGroupDto> diskRecordGroupDtoList = serverService.getRuntimeDiskRecord(userId, serverIp, intervalMinute);
        if (diskRecordGroupDtoList == null || diskRecordGroupDtoList.isEmpty()) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(diskRecordGroupDtoList);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/runtimeMemRecord/{serverIp}/{intervalMinute}")
    public ResponseMap<List<MemRecordDto>> runtimeMemRecord(@PathVariable String serverIp, @PathVariable Long intervalMinute, HttpServletRequest request) {
        ResponseMap<List<MemRecordDto>> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        List<MemRecordDto> memRecordDtoList = serverService.getRuntimeMemRecord(userId, serverIp, intervalMinute);
        if (memRecordDtoList == null || memRecordDtoList.isEmpty()) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(memRecordDtoList);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/runtimeNetRecord/{serverIp}/{intervalMinute}")
    public ResponseMap<List<NetRecordGroupDto>> runtimeNetRecord(@PathVariable String serverIp, @PathVariable Long intervalMinute, HttpServletRequest request) {
        ResponseMap<List<NetRecordGroupDto>> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        List<NetRecordGroupDto> netRecordGroupDtoList = serverService.getRuntimeNetRecord(userId, serverIp, intervalMinute);
        if (netRecordGroupDtoList == null || netRecordGroupDtoList.isEmpty()) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(netRecordGroupDtoList);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/serverSSHList")
    public ResponseMap<List<ServerSSHDto>> serverSSHList(@RequestParam Integer page, @RequestParam Integer limit, HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        ResponseMap<List<ServerSSHDto>> responseMap = serverService.getServerSSHList(userId, page, limit);
        if (responseMap == null) {
            responseMap = new ResponseMap<>();
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/sshInfoUpdate")
    public ResponseMap<Integer> sshInfoUpdate(@RequestBody CloudServer cloudServer, HttpServletRequest request) {
        ResponseMap<Integer> responseMap = new ResponseMap<>();
        cloudServer.setServerUser((Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY));
        Integer effectiveNum = serverService.renewServerSSHInfo(cloudServer);
        if (effectiveNum == null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(effectiveNum);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/uploadFile/{serverIp}")
    public ResponseMap<String> uploadFile(HttpServletRequest request, @PathVariable String serverIp) {
        ResponseMap<String> responseMap = new ResponseMap<>();
        CommonsMultipartFile uploadFile = null;
        //创建文件接收器
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (commonsMultipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            uploadFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("uploadFile");
        }
        WebSSHDataDto webSSHData = new WebSSHDataDto();
        webSSHData.setHost(serverIp);
        Session session = null;
        try {
            session = sftpService.initConnection(String.valueOf(request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY)), webSSHData);
        } catch (Exception e) {
            logger.error("创建sftp连接失败");
            logger.error("异常信息:{}", e.getMessage());
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
            return responseMap;
        }
        if (sftpService.uploadFile(session, HttpRequestUtil.getStringParameter(request, "uploadPath"), uploadFile)) {
            //如果上传成功了
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }

        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/downloadFile/{serverIp}/{downloadFileName}")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String serverIp, @PathVariable String downloadFileName) {
        WebSSHDataDto webSSHData = new WebSSHDataDto();
        webSSHData.setHost(serverIp);
        //创建连接
        Session session = null;
        try {
            session = sftpService.initConnection(String.valueOf(request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY)), webSSHData);
        } catch (Exception e) {
            logger.error("创建sftp连接失败");
            logger.error("异常信息:{}", e.getMessage());
        }
        try {
            if (!sftpService.downloadFile(session, HttpRequestUtil.getStringParameter(request, "downloadPath"), downloadFileName, response.getOutputStream())) {
                logger.info("获取文件失败");
            }
        } catch (IOException e) {
            logger.error("下载文件异常");
            logger.error("异常信息:{}", e.getMessage());
            return;
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        HttpHeaders headers = new HttpHeaders();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/x-download; charset=UTF-8");
        headers.add("Content-Disposition", "attchement;filename=" + downloadFileName);
        try {
            bos = new BufferedOutputStream(response.getOutputStream());
            bos.write(1);
            bos.flush();
        } catch (IOException e) {
            logger.error("流转换失败");
            logger.error("异常信息:" + e.getMessage());
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                logger.error("关闭流失败");
                logger.error("异常信息:" + e.getMessage());
            }
        }
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/ls")
    public ResponseMap<List<LinuxFile>> ls(HttpServletRequest request) {
        String path = HttpRequestUtil.getStringParameter(request, "path");
        String serverIp = HttpRequestUtil.getStringParameter(request, "serverIp");
        WebSSHDataDto webSSHData = new WebSSHDataDto();
        webSSHData.setHost(serverIp);
        ResponseMap<List<LinuxFile>> responseMap = new ResponseMap<>();
        List<LinuxFile> linuxFileList = null;
        Session session = null;
        try {
            session = sftpService.initConnection(String.valueOf(request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY)), webSSHData);
        } catch (Exception e) {
            logger.error("创建sftp连接失败");
            logger.error("异常信息:{}", e.getMessage());
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(null);
        }
        linuxFileList = sftpService.ls(session, path);
        if (linuxFileList == null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(null);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(linuxFileList);
            responseMap.setCount(linuxFileList.size());
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/removeFile/{serverIp}")
    public ResponseMap<String> removeFile(@PathVariable String serverIp, HttpServletRequest request) {
        String filePath = HttpRequestUtil.getStringParameter(request, "filePath");
        String fileName = HttpRequestUtil.getStringParameter(request, "fileName");
        ResponseMap<String> responseMap = new ResponseMap<>();
        WebSSHDataDto webSSHData = new WebSSHDataDto();
        webSSHData.setHost(serverIp);
        Session session = null;
        try {
            session = sftpService.initConnection(String.valueOf(request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY)), webSSHData);
        } catch (Exception e) {
            logger.error("创建sftp连接失败");
            logger.error("异常信息:{}", e.getMessage());
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(null);
        }
        if (sftpService.removeFile(session, filePath, fileName)) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/removeDir/{serverIp}")
    public ResponseMap<String> removeDir(@PathVariable String serverIp, HttpServletRequest request) {
        //文件夹路径由文件基础路径和文件夹名称组成
        String filePath = HttpRequestUtil.getStringParameter(request, "filePath") + HttpRequestUtil.getStringParameter(request, "fileName");
        ResponseMap<String> responseMap = new ResponseMap<>();
        WebSSHDataDto webSSHData = new WebSSHDataDto();
        webSSHData.setHost(serverIp);
        Session session = null;
        try {
            session = sftpService.initConnection(String.valueOf(request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY)), webSSHData);
        } catch (Exception e) {
            logger.error("创建sftp连接失败");
            logger.error("异常信息:{}", e.getMessage());
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(null);
        }
        if (sftpService.removeDir(session, filePath)) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/mkdir/{serverIp}")
    public ResponseMap<String> mkdir(@PathVariable String serverIp, HttpServletRequest request) {
        String filePath = HttpRequestUtil.getStringParameter(request, "filePath");
        String dirName = HttpRequestUtil.getStringParameter(request, "dirName");

        ResponseMap<String> responseMap = new ResponseMap<>();

        WebSSHDataDto webSSHData = new WebSSHDataDto();
        webSSHData.setHost(serverIp);
        Session session = null;
        try {
            session = sftpService.initConnection(String.valueOf(request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY)), webSSHData);
        } catch (Exception e) {
            logger.error("创建sftp连接失败");
            logger.error("异常信息:{}", e.getMessage());
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }

        if (sftpService.mkdir(session, filePath, dirName)) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/chmod/{serverIp}")
    public ResponseMap<String> chmod(@PathVariable String serverIp, HttpServletRequest request) {
        String filePath = HttpRequestUtil.getStringParameter(request, "filePath");
        String fileName = HttpRequestUtil.getStringParameter(request, "fileName");
        String permission = HttpRequestUtil.getStringParameter(request, "permission");

        ResponseMap<String> responseMap = new ResponseMap<>();

        WebSSHDataDto webSSHData = new WebSSHDataDto();
        webSSHData.setHost(serverIp);
        Session session = null;
        try {
            session = sftpService.initConnection(String.valueOf(request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY)), webSSHData);
        } catch (Exception e) {
            logger.error("创建sftp连接失败");
            logger.error("异常信息:{}", e.getMessage());
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }

        if (sftpService.chmod(session, filePath + fileName, permission)) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/commandGroup")
    public ResponseMap<List<ExecuteCommandReturnDto>> commandGroup(HttpServletRequest request) {
        ResponseMap<List<ExecuteCommandReturnDto>> responseMap = new ResponseMap<>();
        String command = HttpRequestUtil.getStringParameter(request, "command");
        ObjectMapper objectMapper = new ObjectMapper();
        List servers = null;
        try {
            //转换list需要用到typeReference
            servers = objectMapper.readValue(request.getParameter("servers"), new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            logger.error("JSON转换异常");
            logger.error("异常信息:{}", e.getMessage());
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            return responseMap;
        }
        List<Session> sessions = new LinkedList<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        for (Object serverIp : servers) {
            WebSSHDataDto webSSHDataDto = new WebSSHDataDto();
            webSSHDataDto.setHost((String) serverIp);
            try {
                //将获取的session存入list
                sessions.add(sshService.initConnection(String.valueOf(userId), webSSHDataDto));
            } catch (Exception e) {
                logger.error("获取{}的session异常", serverIp);
                logger.error("异常信息:{}", e.getMessage());
            }
        }
        List<ExecuteCommandReturnDto> executeCommandReturnDtos = sshService.groupCommand(sessions, command, userId);
        responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
        responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
        responseMap.setData(executeCommandReturnDtos);
        responseMap.setCount(executeCommandReturnDtos.size());
        return responseMap;

    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/serverCommandExecuteRecord")
    public ResponseMap<List<CloudServerCommandExecuteRecordDto>> serverCommandExecuteRecord(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        ResponseMap<List<CloudServerCommandExecuteRecordDto>> responseMap = new ResponseMap<>();

        List<CloudServerCommandExecuteRecordDto> cloudServerCommandExecuteRecordDtoList = sshService.getServerCommandExecuteRecord(userId);

        if (cloudServerCommandExecuteRecordDtoList != null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(cloudServerCommandExecuteRecordDtoList);
            responseMap.setCount(cloudServerCommandExecuteRecordDtoList.size());
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/simpleCommand")
    public ResponseMap<List<SimpleCommandDto>> simpleCommand(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        ResponseMap<List<SimpleCommandDto>> responseMap = new ResponseMap<>();
        List<SimpleCommandDto> simpleCommandDtos = sshService.getSimpleCommands(userId);

        if (simpleCommandDtos != null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(simpleCommandDtos);
            responseMap.setCount(simpleCommandDtos.size());
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/addSimpleCommand")
    public ResponseMap<String> addSimpleCommand(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);

        ResponseMap<String> responseMap = new ResponseMap<>();

        String commandName = HttpRequestUtil.getStringParameter(request, "commandName");
        String commandContent = HttpRequestUtil.getStringParameter(request, "commandContent");

        boolean flag = sshService.addSimpleCommand(commandName, commandContent, userId);

        if (flag) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/removeSimpleCommand")
    public ResponseMap<String> removeSimpleCommand(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);

        Integer commandId = HttpRequestUtil.getIntegerParameter(request, "commandId");
        ResponseMap<String> responseMap = new ResponseMap<>();


        boolean flag = sshService.removeSimpleCommand(commandId, userId);

        if (flag) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/serverSimpleStatus")
    public ResponseMap<ServerSimpleStatusDto> serverSimpleStatus(HttpServletRequest request) {
        ResponseMap<ServerSimpleStatusDto> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        ServerSimpleStatusDto serverSimpleStatusDto = serverService.getServerSimpleStatus(userId);

        if (serverSimpleStatusDto == null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(serverSimpleStatusDto);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/serverSimpleSnapshot/{serverIp}")
    public ResponseMap<ServerSimpleSnapshot> serverSimpleSnapshot(HttpServletRequest request, @PathVariable String serverIp) {
        ResponseMap<ServerSimpleSnapshot> responseMap = new ResponseMap<>();

        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);

        ServerSimpleSnapshot serverSimpleSnapshot = serverService.getServerSimpleSnapshot(userId, serverIp);

        if (serverSimpleSnapshot == null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(serverSimpleSnapshot);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/serverRuntimeRecord")
    public ResponseMap<List<ServerRuntimeRecordDto>> serverRuntimeRecord(HttpServletRequest request) {
        ResponseMap<List<ServerRuntimeRecordDto>> responseMap = new ResponseMap<>();
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);

        List<ServerRuntimeRecordDto> serverRuntimeRecordDtoList = serverService.getServerRuntimeRecord(userId);

        if (serverRuntimeRecordDtoList == null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(serverRuntimeRecordDtoList);
        }
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/futureTask")
    public ResponseMap<String> futureTask(HttpServletRequest request, @RequestBody FutureTask futureTask) {
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        ResponseMap<String> responseMap = new ResponseMap<>();
        if (serverService.addFutureTask(futureTask, userId)) {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        return responseMap;
    }

    @SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @GetMapping("/futureTask")
    public ResponseMap<List<FutureTaskDto>> futureTask(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY);
        ResponseMap<List<FutureTaskDto>> responseMap = new ResponseMap<>();
        Integer page = HttpRequestUtil.getIntegerParameter(request, "page");
        Integer limit = HttpRequestUtil.getIntegerParameter(request, "limit");
        List<FutureTaskDto> futureTaskDtos = serverService.getFutureTaskList(userId, page, limit);
        if (futureTaskDtos == null) {
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        } else {
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
            responseMap.setData(futureTaskDtos);
        }
        return responseMap;
    }
    /*@SaveLog(applicationId = ConstantPool.ComponentCenter.APPLICATION_ID)
    @PostMapping("/touch/{serverIp}")
    public ResponseMap<String> touch(@PathVariable String serverIp,HttpServletRequest request){
        String filePath = HttpRequestUtil.getStringParameter(request,"filePath");
        String fileName = HttpRequestUtil.getStringParameter(request,"fileName");

        ResponseMap<String> responseMap = new ResponseMap<>();

        WebSSHDataDto webSSHData = new WebSSHDataDto();
        webSSHData.setHost(serverIp);
        Session session = null;
        try {
            session = sftpService.initConnection(String.valueOf(request.getSession().getAttribute(ConstantPool.ComponentCenter.SESSION_USER_ID_KEY)), webSSHData);
        } catch (Exception e) {
            logger.error("创建sftp连接失败");
            logger.error("异常信息:{}", e.getMessage());
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
            responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        }

        if(sftpService.touch(session,filePath,fileName)){
            responseMap.setCode(ConstantPool.Common.REQUEST_SUCCESS_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_SUCCESS_MESSAGE);
        }else{
            responseMap.setCode(ConstantPool.Common.REQUEST_FAILURE_CODE);
            responseMap.setMessage(ConstantPool.Common.REQUEST_FAILURE_MESSAGE);
        }
        responseMap.setData(ConstantPool.Common.RES_NOT_DATA);
        return responseMap;
    }*/
}
