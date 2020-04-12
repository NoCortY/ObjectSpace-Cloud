package cn.objectspace.componentcenter.pojo.dto;

/**
 * @Description: linux文件对象
 * @Author: NoCortY
 * @Date: 2020/4/11
 */
public class LinuxFile {
    //文件类型
    private String type;
    //权限
    private String power;
    //文件连接数和子目录数
    private String linkOrDirNum;
    //文件所属用户
    private String userName;
    //文件所属组
    private String groupName;
    //文件大小
    private Long size;
    //文件最后修改时间
    private String lastModify;
    //文件名
    private String fileName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getLinkOrDirNum() {
        return linkOrDirNum;
    }

    public void setLinkOrDirNum(String linkOrDirNum) {
        this.linkOrDirNum = linkOrDirNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
