package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.property.AddModelParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.UpdatePropertyParam;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:38
 **/
public interface PropertyService {
    void updateProperty(UpdatePropertyParam pa);

    void addModel(AddModelParam param, Integer userID);
}
