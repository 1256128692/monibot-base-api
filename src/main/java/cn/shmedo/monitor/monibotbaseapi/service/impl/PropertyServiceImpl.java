package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.model.param.property.AddModelParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.UpdatePropertyParam;
import cn.shmedo.monitor.monibotbaseapi.service.PropertyService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ThirdHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.info.InfoService;
import org.springframework.stereotype.Service;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:38
 **/
@Service
public class PropertyServiceImpl implements PropertyService {
    @Override
    public void updateProperty(UpdatePropertyParam pa) {
        InfoService infoService = ThirdHttpService.getInstance(InfoService.class, ThirdHttpService.Info);
        // TODO  更新属性
    }

    @Override
    public void addModel(AddModelParam param, Integer userID) {
        InfoService infoService = ThirdHttpService.getInstance(InfoService.class, ThirdHttpService.Info);
        // TODO 新增模板
    }
}
