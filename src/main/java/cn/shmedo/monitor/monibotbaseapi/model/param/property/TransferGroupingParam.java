package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModelGroup;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * @Author wuxl
 * @Date 2023/9/25 11:30
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.property
 * @ClassName: TransferGroupingParam
 * @Description: 设备表单转移分组
 * @Version 1.0
 */
@Data
@ToString
public class TransferGroupingParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "模板不能为空")
    private Integer modelID;

    @NotNull(message = "模板类型不能为空")
    private Integer modelType;

    @NotNull(message = "转移后分组ID不能为空")
    private Integer newGroupID;

    @JsonIgnore
    private TbPropertyModel tbPropertyModel;

    @Override
    public ResultWrapper<?> validate() {
        // 校验表单模板类型
        if (!PropertyModelType.getModelTypeValues().contains(modelType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型不合法");
        }
        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        tbPropertyModel = tbPropertyModelMapper.selectByPrimaryKey(modelID);
        if (Objects.isNull(tbPropertyModel)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未查询到对应模板");
        }

        // 只支持设备模板转移分组
        if(!PropertyModelType.DEVICE.getCode().equals(modelType) && !PropertyModelType.WORK_FLOW.getCode().equals(modelType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "仅支持设备、工作流模板转移分组");
        }

        // 校验新的模板分组是否存在
        TbPropertyModelGroupMapper tbPropertyModelGroupMapper = ContextHolder.getBean(TbPropertyModelGroupMapper.class);
        TbPropertyModelGroup tbPropertyModelGroup = tbPropertyModelGroupMapper.selectById(newGroupID);
        if (Objects.isNull(tbPropertyModelGroup)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "转移后分组不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
