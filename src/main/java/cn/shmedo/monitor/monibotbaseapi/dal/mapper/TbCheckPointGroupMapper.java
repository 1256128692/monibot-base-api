package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint.CheckPointGroupSimple;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint.QueryCheckPointGroupListRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbCheckPointGroupMapper extends BasicMapper<TbCheckPointGroup> {

    List<CheckPointGroupSimple> list(QueryCheckPointGroupListRequest request);
}