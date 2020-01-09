package cn.objectspace.logcenter.pojo.dto;


/**
 * 接口调用次数DTO
 * @author nocor
 *
 */
public class CallCountDto {
	private String operateInterface;
	private Integer callCount;
	public String getOperateInterface() {
		return operateInterface;
	}
	public void setOperateInterface(String operateInterface) {
		this.operateInterface = operateInterface;
	}
	public Integer getCallCount() {
		return callCount;
	}
	public void setCallCount(Integer callCount) {
		this.callCount = callCount;
	}
	
	
}
