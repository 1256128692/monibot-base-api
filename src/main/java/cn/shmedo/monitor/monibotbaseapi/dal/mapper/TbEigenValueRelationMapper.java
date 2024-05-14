package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValueRelation;

import java.util.List;

public interface TbEigenValueRelationMapper extends BasicMapper<TbEigenValueRelation> {

    void deleteByEigenValueIDList(List<Integer> eigenValueIDList);

    List<TbEigenValueRelation> selectByIDs(List<Integer> eigenValueIDList);

}