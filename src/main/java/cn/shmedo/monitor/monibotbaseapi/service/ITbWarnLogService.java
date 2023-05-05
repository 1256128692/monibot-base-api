package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtTerminalWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtTerminalWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtTerminalWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnLogInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbWarnLogService extends IService<TbWarnLog> {
    PageUtil.Page<WtWarnLogInfo> queryByPage(QueryWtWarnLogPageParam param);

    WtWarnDetailInfo queryDetail(QueryWtWarnDetailParam param);

    PageUtil.Page<WtTerminalWarnLog> queryByPage(QueryWtTerminalWarnLogPageParam param);

    WtTerminalWarnDetailInfo queryTerminalWarnDetail(QueryWtWarnDetailParam param);
}
