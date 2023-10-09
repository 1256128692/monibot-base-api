package cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbOtherDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 14:41
 **/
@Data
public class UpdateOtherDeviceParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @JsonAlias
    TbOtherDevice tbOtherDevice;
    @NotNull
    private Integer companyID;
    @NotNull
    @JsonAlias("ID")
    private Integer ID;
    @NotNull
    private String name;
    @NotNull
    private String token;
    @NotNull
    private String model;
    @NotNull
    private String vendor;
    @Pattern(regexp = "^.+$", message = "扩展字段应为JSON格式")
    private String exValue;
    @Valid
    private List<@NotNull PropertyIdAndValue> propertyList;

    @Override
    public ResultWrapper validate() {
        TbOtherDeviceMapper tbOtherDeviceMapper = ContextHolder.getBean(TbOtherDeviceMapper.class);
        tbOtherDevice = tbOtherDeviceMapper.selectById(ID);
        if (tbOtherDevice == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "设备不存在");
        }
        if (!tbOtherDevice.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "设备不属于该公司");
        }
        if (ObjectUtil.isNotEmpty(exValue) && (!JSONUtil.isTypeJSON(exValue))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "扩展字段应为JSON格式");

        }
        if (tbOtherDeviceMapper.selectCount(
                new LambdaQueryWrapper<TbOtherDevice>()
                        .eq(TbOtherDevice::getVendor, token)
                        .eq(TbOtherDevice::getModel, name)
                        .eq(TbOtherDevice::getName, model)
                        .ne(TbOtherDevice::getID, ID)
        ) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "同一厂家同一型号下设备编号不可重复");
        }
        if (tbOtherDevice.getTemplateID() == null && ObjectUtil.isNotEmpty(propertyList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "设备没有模板，不能修改属性");
        }
        if (tbOtherDevice.getTemplateID() == null && ObjectUtil.isNotEmpty(propertyList)) {

        }
        if (ObjectUtil.isNotEmpty(propertyList)) {
            TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
            List<TbProperty> tbProperties = tbPropertyMapper.selectByModelIDs(List.of(tbOtherDevice.getTemplateID()));
            List<Integer> propertyIDList = tbProperties.stream().map(TbProperty::getID).toList();
            if (propertyList.stream().anyMatch(item -> !propertyIDList.contains(item.getID()))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有属性不属于该模板");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    public TbOtherDevice update(Integer subjectID) {
        tbOtherDevice.setName(name);
        tbOtherDevice.setToken(token);
        tbOtherDevice.setModel(model);
        tbOtherDevice.setVendor(vendor);
        tbOtherDevice.setExValue(exValue);
        return tbOtherDevice;
    }

    public List<TbProjectProperty> toProjectPropertyList() {
        return propertyList.stream().map(item -> {
            TbProjectProperty tbProjectProperty = new TbProjectProperty();
            tbProjectProperty.setProjectID(tbOtherDevice.getID());
            tbProjectProperty.setPropertyID(item.getID());
            tbProjectProperty.setValue(item.getValue());
            return tbProjectProperty;
        }).toList();
    }
}
