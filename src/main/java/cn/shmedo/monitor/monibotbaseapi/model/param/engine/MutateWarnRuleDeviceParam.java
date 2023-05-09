package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnRuleMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-06 13:47
 **/
@Data
public class MutateWarnRuleDeviceParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer roleID;
    private String sign;
    private String deviceCSV;
    @JsonIgnore
    private TbWarnRule tbWarnRule;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbWarnRuleMapper tbWarnRuleMapper = ContextHolder.getBean(TbWarnRuleMapper.class);
        tbWarnRule = tbWarnRuleMapper.selectById(roleID);
        if (tbWarnRule == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "规则不存在");
        }
        if (!tbWarnRule.getProjectID().equals(projectID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "规则不属于项目");
        }
        if (tbWarnRule.getRuleType() == 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "不支持的规则类型");
        }
        if (!sign.equals("+") && !sign.equals("-")) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "符号不合法");
        }
        if (StringUtils.isNotBlank(tbWarnRule.getDeviceCSV()) && tbWarnRule.getDeviceCSV().equals("all") || StringUtils.isNotBlank(tbWarnRule.getVideoCSV()) && tbWarnRule.getVideoCSV().equals("all")) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "规则已经包含所有设备");
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
}
