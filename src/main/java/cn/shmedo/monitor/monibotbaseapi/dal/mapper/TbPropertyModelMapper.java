package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModelGroup;
import cn.shmedo.monitor.monibotbaseapi.model.response.Model4Web;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbPropertyModelMapper extends BaseMapper<TbPropertyModel> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbPropertyModel record);

    TbPropertyModel selectByPrimaryKey(Integer ID);

    List<Model4Web> queryModel4WebBy(Byte projectType, Byte createType);

    int countByName(String modelName);
}