package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnTrigger;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtTriggerActionInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ITbWarnTriggerService extends IService<TbWarnTrigger> {
    List<WtTriggerActionInfo> queryWarnStatusByEngineIds(List<Integer> engineIds);

    void deleteWarnStatusByIDList(List<Integer> list);

    void deleteWarnStatusByList(List<TbWarnTrigger> list);
}
