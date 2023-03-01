package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;

@Mapper
public interface RegionAreaMapper extends BaseMapper<RegionArea> {

    /**
     * 使用条件构造器进行流式查询
     *
     * @param handler 批次处理器  {@link ResultHandler}
     */
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = 1000)
    @ResultType(RegionArea.class)
    @Select("select * from `tb_region_area` ${ew.customSqlSegment}")
    void streamQuery(ResultHandler<RegionArea> handler, @Param("ew") Wrapper<RegionArea> wrapper);
}