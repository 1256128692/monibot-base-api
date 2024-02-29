package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint.CheckPointSimple;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint.QueryCheckPointListRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbCheckPointMapper extends BasicMapper<TbCheckPoint> {

    Integer lastSerialNumber(String prefix);

   Page<CheckPointSimple> page(@Param("param") QueryCheckPointListRequest request,
                               @Param("page") IPage<CheckPointSimple> page);

    List<CheckPointSimple> list(@Param("param") QueryCheckPointListRequest request);
}