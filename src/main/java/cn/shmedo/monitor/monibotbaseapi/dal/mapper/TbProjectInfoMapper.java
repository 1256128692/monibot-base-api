package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbProjectInfoMapper extends BaseMapper<TbProjectInfo> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectInfo record);

    int insertSelective(TbProjectInfo record);

    TbProjectInfo selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectInfo record);

    int updateByPrimaryKey(TbProjectInfo record);
}