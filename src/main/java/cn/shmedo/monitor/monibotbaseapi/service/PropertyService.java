package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.AddModelParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.QueryModelListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.QueryPropertyValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.UpdatePropertyParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.Model4Web;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:38
 **/
public interface PropertyService {
    void updateProperty(UpdatePropertyParam pa);

    void updateProperty(Integer projectID, List<PropertyIdAndValue> propertyIdAndValueList,List<TbProperty> propertyList);

    void addModel(AddModelParam param, Integer userID);

    List<String> queryPropertyValue(QueryPropertyValueParam param);

    List<Model4Web> queryModelList(QueryModelListParam param);
}
