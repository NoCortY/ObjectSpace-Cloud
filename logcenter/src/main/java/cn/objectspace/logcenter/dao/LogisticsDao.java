package cn.objectspace.logcenter.dao;

import cn.objectspace.logcenter.pojo.dto.NoticeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface LogisticsDao {
	public List<NoticeDto> queryNotice();
}
