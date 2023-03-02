package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.AddModelParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.QueryModelListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.QueryPropertyValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.UpdatePropertyParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.Model4Web;
import cn.shmedo.monitor.monibotbaseapi.service.PropertyService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:38
 **/
@Service
public class PropertyServiceImpl implements PropertyService {
    private TbPropertyMapper tbPropertyMapper;
    private TbProjectPropertyMapper tbProjectPropertyMapper;
    private TbPropertyModelMapper tbPropertyModelMapper;
    @Autowired
    public PropertyServiceImpl(TbPropertyMapper tbPropertyMapper, TbProjectPropertyMapper tbProjectPropertyMapper, TbPropertyModelMapper tbPropertyModelMapper) {
        this.tbPropertyMapper = tbPropertyMapper;
        this.tbProjectPropertyMapper = tbProjectPropertyMapper;
        this.tbPropertyModelMapper = tbPropertyModelMapper;
    }

    @Override
    public void updateProperty(UpdatePropertyParam pa) {
        Integer projectID = pa.getProjectID();
        List<TbProperty> properties =  tbPropertyMapper.queryByPID(projectID);
        Map<String, TbProperty> propertyMap = properties.stream().collect(Collectors.toMap(TbProperty::getName, Function.identity()));
        List<TbProjectProperty> projectPropertyList = pa.getModelValueList().stream().map(
                item -> {
                    TbProjectProperty tbProjectProperty = new TbProjectProperty();
//                    tbProjectProperty.setPropertyID(pa.getProjectID());
                    tbProjectProperty.setPropertyID(propertyMap.get(item.getName()).getID());
                    tbProjectProperty.setValue(item.getValue());
                    return tbProjectProperty;
                }
        ).collect(Collectors.toList());

        tbProjectPropertyMapper.updateBatch(pa.getProjectID(), projectPropertyList);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addModel(AddModelParam param, Integer userID) {
        TbPropertyModel record = Param2DBEntityUtil.fromAddModelParam2TbPropertyModel(param, userID);
        tbPropertyModelMapper.insert(record);

        List<TbProperty> properties = Param2DBEntityUtil.fromAddModelParam2TbPropertyList(param, userID, record.getID());
        tbPropertyMapper.insertBatch(properties);
    }

    @Override
    public List<String> queryPropertyValue(QueryPropertyValueParam param) {
        return tbProjectPropertyMapper.getPropertyValue(param);
    }

    @Override
    public List<Model4Web> queryModelList(QueryModelListParam param) {
        if (param.getModelID()!=null){
            TbPropertyModel tbPropertyModel = tbPropertyModelMapper.selectByPrimaryKey(param.getModelID());
            if (tbPropertyModel == null){
                return List.of();
            }
           List<TbProperty> properties=  tbPropertyMapper.selectByModelIDs(List.of(param.getModelID()));
            return List.of(Model4Web.valueOf(tbPropertyModel, properties));
        }
        List<Model4Web> list = tbPropertyModelMapper.queryModel4WebBy(param.getProjectType(), param.getCreateType());
        if (ObjectUtil.isEmpty(list)){
            return List.of();
        }
        List<TbProperty> temp = tbPropertyMapper.selectByModelIDs(list.stream().map(Model4Web::getID).collect(Collectors.toList()));
        Map<Integer, List<TbProperty>> modelIDAndPropertyListMap = temp.stream().collect(Collectors.groupingBy(TbProperty::getModelID));
        list.forEach(item -> item.setPropertyList(modelIDAndPropertyListMap.get(item.getID()))
        );
        return list;
    }
}
