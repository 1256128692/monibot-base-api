package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbWarnLogService extends IService<TbWarnLog> {
    PageUtil.Page<WtWarnLogInfo> queryByPage(QueryWtWarnLogPageParam param);

    WtWarnDetailInfo queryDetail(QueryWtWarnDetailParam param);

    PageUtil.Page<WtTerminalWarnLog> queryTerminalWarnPage(QueryWtTerminalWarnLogPageParam param);

    WtTerminalWarnDetailInfo queryTerminalWarnDetail(QueryWtWarnDetailParam param);

    void addWarnLogBindWarnOrder(AddWarnLogBindWarnOrderParam param);

    WtWarnListResult queryBaseList(QueryWtWarnListParam pa);
}
