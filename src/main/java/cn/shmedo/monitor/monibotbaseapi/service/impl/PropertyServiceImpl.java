package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.AddModelParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.UpdatePropertyParam;
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
                    tbProjectProperty.setPropertyID(pa.getProjectID());
                    tbProjectProperty.setPropertyID(propertyMap.get(item.getName()).getID());
                    tbProjectProperty.setValue(item.getValue());
                    return tbProjectProperty;
                }
        ).collect(Collectors.toList());

        tbProjectPropertyMapper.updateBatch(projectPropertyList);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addModel(AddModelParam param, Integer userID) {
        TbPropertyModel record = Param2DBEntityUtil.fromAddModelParam2TbPropertyModel(param, userID);
        tbPropertyModelMapper.insert(record);

        List<TbProperty> properties = Param2DBEntityUtil.fromAddModelParam2TbPropertyList(param, userID, record.getID());
        tbPropertyMapper.insertBatch(properties);
    }
}
