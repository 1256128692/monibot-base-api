package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtTerminalWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtTerminalWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtTerminalWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnLogInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderWarnDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TbWarnLogMapper extends BaseMapper<TbWarnLog> {
    Long queryCurrentRecordCount(@Param("param") QueryWtWarnLogPageParam param);

    List<WtWarnLogInfo> queryCurrentRecords(@Param("param") QueryWtWarnLogPageParam param);

    IPage<WtWarnLogInfo> queryHistoryRecords(IPage<WtWarnLogInfo> page, @Param("param") QueryWtWarnLogPageParam param);

    WtWarnDetailInfo queryWarnDetail(Integer warnID);

    WtWorkOrderWarnDetail queryWorkOrderWarnDetail(@Param("param") QueryWorkOrderWarnDetailParam param);

    List<WtWarnLogInfo> queryTerminalRecords(@Param("param") QueryWtTerminalWarnLogPageParam param, @Param("flag") boolean flag);

    List<WtTerminalWarnLog> queryTerminalRecordsByUniqueToken(@Param("param") QueryWtTerminalWarnLogPageParam param,
                                                              @Param("uniqueTokens") Collection<String> uniqueTokens);

    WtTerminalWarnDetailInfo queryTerminalWarnDetail(Integer warnID);
}
