package cn.objectspace.logcenter.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import cn.objectspace.logcenter.pojo.dto.NoticeDto;


@Mapper
public interface LogisticsDao {
	public List<NoticeDto> queryNotice();
}
