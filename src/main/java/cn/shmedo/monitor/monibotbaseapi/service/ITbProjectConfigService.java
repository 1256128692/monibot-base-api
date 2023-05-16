package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.BatchSetProjectConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.ListProjectConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.SetProjectConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig.ListProjectConfigResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-15 17:37
 */
public interface ITbProjectConfigService extends IService<TbProjectConfig> {
    void setProjectConfig(SetProjectConfigParam param);

    void batchSetProjectConfig(BatchSetProjectConfigParam param);

    List<ListProjectConfigResponse> listProjectConfig(ListProjectConfigParam param);
}
