package cn.objectspace.logcenter.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.objectspace.logcenter.dao.LogisticsDao;
import cn.objectspace.logcenter.pojo.dto.NoticeDto;
import cn.objectspace.logcenter.service.LogisticsService;

@Service
public class LogisticsServiceImpl implements LogisticsService{
	@Autowired
	LogisticsDao logisticsDao;
	@Override
	public List<NoticeDto> getNotice() {
		return logisticsDao.queryNotice();
	}
}
