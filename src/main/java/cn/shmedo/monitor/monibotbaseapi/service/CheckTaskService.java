package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checktsak.CheckTaskInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checktsak.CheckTaskSimple;
import cn.shmedo.monitor.monibotbaseapi.model.param.checktask.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.checktask.CheckTaskPageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CheckTaskService extends IService<TbCheckTask> {

    Integer save(AddCheckTaskRequest request);

    void update(UpdateCheckTaskRequest request);

    void delete(DeleteCheckTaskRequest request);

    CheckTaskPageResponse page(QueryCheckTaskPageRequest request);

    List<CheckTaskSimple> list(QueryCheckTaskListRequest request);

    CheckTaskInfo single(QueryCheckTaskRequest request);

    void startTask(StartTaskRequest request);

    void endTask(EndTaskRequest request);
}
