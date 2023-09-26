package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.Model4Web;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:38
 **/
public interface PropertyService extends IService<TbProperty> {
    void updateProperty(UpdatePropertyParam pa);

    void updateProperty(Integer projectID, List<PropertyIdAndValue> propertyIdAndValueList,List<TbProperty> propertyList);

    Integer addModel(AddModelParam param, Integer userID);

    Boolean updateModel(UpdateModelParam param);

    Boolean transferGrouping(TransferGroupingParam param);

    Integer copyModel(CopyModelParam param);

    List<String> queryPropertyValue(QueryPropertyValueParam param);

    List<Model4Web> queryModelList(QueryModelListParam param);

    Integer deleteModel(DeleteModelParam param);
}
