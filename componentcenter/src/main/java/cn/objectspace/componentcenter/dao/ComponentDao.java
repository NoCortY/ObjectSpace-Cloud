package cn.objectspace.componentcenter.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ComponentDao {
    public Integer insertComponent();
}
