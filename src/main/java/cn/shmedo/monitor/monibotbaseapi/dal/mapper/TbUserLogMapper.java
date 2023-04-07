package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.hutool.core.date.DateTime;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserLog;

import java.util.Date;

public interface TbUserLogMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbUserLog record);

    int insertSelective(TbUserLog record);

    TbUserLog selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbUserLog record);

    int updateByPrimaryKey(TbUserLog record);

    void cleanUserLog();

}