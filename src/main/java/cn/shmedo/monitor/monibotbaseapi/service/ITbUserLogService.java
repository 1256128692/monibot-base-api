package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.userLog.QueryUserOperationLogParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.userLog.QueryUserLogResult;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbUserLogService extends IService<TbUserLog> {

    PageUtil.Page<QueryUserLogResult> queryUserOperationLog(QueryUserOperationLogParameter pa);
}
