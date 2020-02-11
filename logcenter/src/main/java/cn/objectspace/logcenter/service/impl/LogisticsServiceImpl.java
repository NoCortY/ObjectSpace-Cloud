package cn.objectspace.logcenter.service.impl;

import cn.objectspace.logcenter.dao.LogisticsDao;
import cn.objectspace.logcenter.pojo.dto.NoticeDto;
import cn.objectspace.logcenter.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogisticsServiceImpl implements LogisticsService{
	@Autowired
	LogisticsDao logisticsDao;
	@Override
	public List<NoticeDto> getNotice() {
		return logisticsDao.queryNotice();
	}
}
