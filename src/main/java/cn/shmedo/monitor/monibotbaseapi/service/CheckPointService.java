package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint.CheckPointGroupSimple;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint.CheckPointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint.CheckPointSimple;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CheckPointService extends IService<TbCheckPoint> {

    Integer save(AddCheckPointRequest request);

    void update(UpdateCheckPointRequest request);

    void batchUpdate(BatchUpdateCheckPointRequest request);

    void delete(DeleteCheckPointRequest request);

    PageUtil.Page<CheckPointSimple> page(QueryCheckPointPageRequest request);

    List<CheckPointSimple> list(QueryCheckPointListRequest request);

    CheckPointInfo single(QueryCheckPointRequest request);

    Integer saveGroup(AddCheckPointGroupRequest request);

    void updateGroup(UpdateCheckPointGroupRequest request);

    void deleteGroup(DeleteCheckPointGroupRequest request);

    List<CheckPointGroupSimple> listGroup(QueryCheckPointGroupListRequest request);

}
