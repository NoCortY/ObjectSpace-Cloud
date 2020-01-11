package cn.objectspace.logcenter.pojo.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口调用次数DTO
 * @author nocor
 *
 */
public class CallCountDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7832995511790133008L;
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
	private Date callDate;
	private Integer callCount;

	public Date getCallDate() {
		return callDate;
	}

	public void setCallDate(Date callDate) {
		this.callDate = callDate;
	}

	public Integer getCallCount() {
		return callCount;
	}
	public void setCallCount(Integer callCount) {
		this.callCount = callCount;
	}
}
