package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.BatchSetProjectConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.ListProjectConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.SetProjectConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig.ListProjectConfigResponse;
import cn.shmedo.monitor.monibotbaseapi.service.ITbProjectConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-15 17:37
 */
@Service
public class TbProjectConfigServiceImpl extends ServiceImpl<TbProjectConfigMapper, TbProjectConfig> implements ITbProjectConfigService {
    @Override
    public void setProjectConfig(SetProjectConfigParam param) {
        batchSetProjectConfig(BatchSetProjectConfigParam.singleToBatch(param));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSetProjectConfig(BatchSetProjectConfigParam param) {
        this.saveOrUpdateBatch(param.build());
    }

    @Override
    public List<ListProjectConfigResponse> listProjectConfig(ListProjectConfigParam param) {
        return this.list(param.build()).stream().map(ListProjectConfigResponse::build).toList();
    }
}
