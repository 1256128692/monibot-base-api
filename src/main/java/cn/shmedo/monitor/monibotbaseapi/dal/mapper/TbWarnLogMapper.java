package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtTerminalWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderWarnDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TbWarnLogMapper extends BaseMapper<TbWarnLog> {

    /**
     * 分页查询在线监测报警列表
     */
    IPage<WtWarnLogInfo> queryMonitorWarnPage(IPage<WtWarnLogInfo> page, @Param("param") QueryWtWarnLogPageParam param);

    /**
     * 分页查询视频/摄像头报警列表
     */
    IPage<WtWarnLogInfo> queryCameraWarnPage(IPage<WtWarnLogInfo> page, @Param("param") QueryWtWarnLogPageParam param);

    /**
     * 查询在线监测报警详情
     */
    WtWarnDetailInfo queryMonitorDetail(Integer warnID);

    /**
     * 查询视频/摄像头报警详情
     */
    WtWarnDetailInfo queryCameraDetail(Integer warnID);

    /**
     * 查询工单报警详情
     */
    WtWorkOrderWarnDetail queryWorkOrderWarnDetail(@Param("param") QueryWorkOrderWarnDetailParam param);

    /**
     * 查询终端报警列表
     */
    List<WtWarnLogInfo> queryTerminalWarnList(@Param("param") QueryWtTerminalWarnLogPageParam param);

    /**
     * 查询终端报警详情
     */
    List<WtTerminalWarnLog> queryTerminalWarnListByUniqueToken(@Param("param") QueryWtTerminalWarnLogPageParam param,
                                                               @Param("uniqueTokens") Collection<String> uniqueTokens);

    /**
     * 查询终端报警详情
     */
    WtTerminalWarnDetailInfo queryTerminalWarnDetail(Integer warnID);

    void updateByIdAndWorkOrderID(Integer warnID, int workOrderID);

    /**
     * 查询报警基础信息列表
     */
    List<WtWarnLogBase> queryBaseList(QueryWtWarnListParam pa);

    /**
     * 查询报警详情，通用类型
     */
    WtWarnDetailInfo queryDetailByID(Integer id);
}
