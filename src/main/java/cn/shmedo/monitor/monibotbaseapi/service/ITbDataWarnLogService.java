package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.CancelDataWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDataWarnPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDataWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDataWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnHistoryInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnPageInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbDataWarnLogService extends IService<TbDataWarnLog> {

    void saveDataWarnLog(SaveDataWarnParam param);

    PageUtil.PageWithMap<DataWarnPageInfo> queryDataWarnPage(QueryDataWarnPageParam param);

    void cancelDataWarn(Integer userID, CancelDataWarnParam param);

    DataWarnDetailInfo queryDataWarnDetail(QueryDataWarnDetailParam param);

    DataWarnHistoryInfo queryDataWarnHistory(QueryDataWarnDetailParam param);
}
