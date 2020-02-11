package cn.objectspace.logcenter.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 公告 DTO
 * @author nocor
 *
 */
public class NoticeDto {
	private String title;
	private String content;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date createTime;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
