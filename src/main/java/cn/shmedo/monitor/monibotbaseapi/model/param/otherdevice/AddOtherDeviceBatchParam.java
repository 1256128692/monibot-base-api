package cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbOtherDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-27 17:36
 **/
@Data
public class AddOtherDeviceBatchParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
    @NotNull
    private Integer companyID;
    private Integer templateID;
    @Valid
    private List<@NotNull AddOtherDeviceItem> list;
    @JsonIgnore
    private Map<TbOtherDevice, List<PropertyIdAndValue>> map;
    private String exValue;

    @Override
    public ResultWrapper validate() {
        if (templateID != null) {
            if (list.stream().anyMatch(item -> ObjectUtil.isEmpty(item.getPropertyList()))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有模板时，属性列表不能为空");
            }
            TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
            TbPropertyModel tbPropertyModel = tbPropertyModelMapper.selectById(templateID);
            if (tbPropertyModel == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板不存在");
            }
            if (!tbPropertyModel.getModelType().equals(PropertyModelType.DEVICE.getCode())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型错误");
            }
            TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
            List<TbProperty> tbProperties = tbPropertyMapper.selectByModelIDs(List.of(templateID));
            List<Integer> propertyIDList = tbProperties.stream().map(TbProperty::getID).toList();
            if (list.stream().anyMatch(e -> e.getPropertyList().size() != propertyIDList.size())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性数量错误");
            }
            if (list.stream().anyMatch(item -> item.getPropertyList().stream().anyMatch(propertyIdAndValue -> !propertyIDList.contains(propertyIdAndValue.getID())))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有属性不存在");
            }


        } else {
            if (list.stream().anyMatch(item -> ObjectUtil.isNotEmpty(item.getPropertyList()))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "没有模板时，属性列表应为空");
            }
        }
        if (list.stream().anyMatch(item -> ObjectUtil.isNotEmpty(item.getExValue()) && !JSONUtil.isTypeJSON(item.getExValue()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "扩展字段应为JSON格式");
        }
        if (list.stream().map(e -> e.getVendor() + e.getModel() + e.getToken()).distinct().count() != list.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "同一厂家同一型号下设备编号不可重复");
        }
        TbOtherDeviceMapper tbOtherDeviceMapper = ContextHolder.getBean(TbOtherDeviceMapper.class);
        // 校验设备厂商，型号，名称是否重复
        if (tbOtherDeviceMapper.countExist(list) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "同一厂家同一型号下设备编号不可重复");
        }
        List<Integer> projectIDList = list.stream().map(AddOtherDeviceItem::getProjectID).distinct().toList();
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectBatchIds(projectIDList).size() != projectIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "projectID不存在");
        }
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return list.stream().map(AddOtherDeviceItem::getProjectID).distinct().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }

    public List<TbOtherDevice> toList(Integer subjectID) {
        map = new HashMap<>(list.size());
        Date now = new Date();
        return list.stream().map(item -> {
            TbOtherDevice tbOtherDevice = new TbOtherDevice();
            tbOtherDevice.setCompanyID(companyID);
            tbOtherDevice.setProjectID(item.getProjectID());
            tbOtherDevice.setName(item.getName());
            tbOtherDevice.setToken(item.getToken());
            tbOtherDevice.setModel(item.getModel());
            tbOtherDevice.setVendor(item.getVendor());
            tbOtherDevice.setTemplateID(templateID);
            tbOtherDevice.setExValue(item.getExValue());
            tbOtherDevice.setUpdateUserID(subjectID);
            tbOtherDevice.setCreateUserID(subjectID);
            tbOtherDevice.setCreateTime(now);
            tbOtherDevice.setUpdateTime(now);
            map.put(tbOtherDevice, item.getPropertyList());
            return tbOtherDevice;
        }).toList();
    }

    public List<TbProjectProperty> toProjectPropertyList() {
        List<TbProjectProperty> tbProjectProperties = new ArrayList<>(map.size());
        map.forEach(
                (e, list) -> list.forEach(
                        item -> {
                            TbProjectProperty obj = new TbProjectProperty();
                            obj.setProjectID(e.getID());
                            obj.setValue(item.getValue());
                            obj.setPropertyID(item.getID());
                            tbProjectProperties.add(obj);
                        }
                )
        );

        return tbProjectProperties;
    }
}
