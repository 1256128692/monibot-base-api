package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.TagKeyAndValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbTagMapper extends BaseMapper<TbTag> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTag record);

    int insertSelective(TbTag record);

    TbTag selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTag record);

    int updateByPrimaryKey(TbTag record);

    int countByCIDAndIDs(Integer companyID, List<Integer> tagIDList);

    void insertBatch(List<TbTag> tagList);

    List<TbTag> queryListBy(Integer companyID, String fuzzyKey, String fuzzyValue);

    int countByCIDAndTags(Integer companyID, List<TagKeyAndValue> tagList);
}