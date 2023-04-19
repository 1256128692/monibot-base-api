package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnLogInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderWarnDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbWarnLogMapper extends BaseMapper<TbWarnLog> {
    Long queryCurrentRecordCount(@Param("param") QueryWtWarnLogPageParam param,
                                 @Param("projectIDList") List<Integer> projectIDList);

    List<WtWarnLogInfo> queryCurrentRecords(@Param("param") QueryWtWarnLogPageParam param,
                                            @Param("projectIDList") List<Integer> projectIDList);

    IPage<WtWarnLogInfo> queryHistoryRecords(IPage<WtWarnLogInfo> page, @Param("param") QueryWtWarnLogPageParam param,
                                             @Param("projectIDList") List<Integer> projectIDList);

    WtWarnDetailInfo queryWarnDetail(Integer warnID);

    WtWorkOrderWarnDetail queryWorkOrderWarnDetail(QueryWorkOrderWarnDetailParam param);
}
