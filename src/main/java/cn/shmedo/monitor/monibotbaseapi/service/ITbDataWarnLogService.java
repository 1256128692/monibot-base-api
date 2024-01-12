package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.DataWarnDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ITbDataWarnLogService extends IService<TbDataWarnLog> {

    void saveDataWarnLog(List<DataWarnDto> request);
}
