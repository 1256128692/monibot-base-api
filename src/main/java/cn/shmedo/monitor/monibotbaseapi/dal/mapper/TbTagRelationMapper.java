package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTagRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbTagRelationMapper  extends BaseMapper<TbTagRelation> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTagRelation record);

    int insertSelective(TbTagRelation record);

    TbTagRelation selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTagRelation record);

    int updateByPrimaryKey(TbTagRelation record);

    void insertBatch(List<Integer> tagIDList, Integer projectID);

    void deleteByProjectID(Integer projectID);

    int deleteProjectTagList(List ids);
    int countTag(List ids);
}