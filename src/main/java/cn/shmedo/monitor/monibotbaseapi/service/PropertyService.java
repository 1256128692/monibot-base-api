package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.property.AddModelParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.QueryPropertyValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.UpdatePropertyParam;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:38
 **/
public interface PropertyService {
    void updateProperty(UpdatePropertyParam pa);

    void addModel(AddModelParam param, Integer userID);

    List<String> queryPropertyValue(QueryPropertyValueParam param);
}
