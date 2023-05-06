package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Arrays;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-06 13:04
 **/
@Data
public class AddWtDeviceWarnRuleParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;
    @Positive
    @Max(3)
    private Byte ruleType;
    @NotNull
    private Integer monitorType;
    @NotNull
    private Integer monitorItemID;
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    private Boolean enable;
    @Size(max = 1000)
    private String desc;
    @Size(max = 1000)
    private String exValue;
    @NotBlank
    private String productID;
    private String deviceCSV;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        if (ruleType == null) {
            ruleType = 1;
        }
        if (!monitorType.equals(-1) && !monitorItemID.equals(-1)) {
            TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
            TbMonitorType tbMonitorType = tbMonitorTypeMapper.queryByType(monitorType);
            if (tbMonitorType == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
            }
            TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
            TbMonitorItem tbMonitorItem = tbMonitorItemMapper.selectByPrimaryKey(monitorItemID);
            if (tbMonitorItem == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项不存在");
            }
            if (!tbMonitorItem.getMonitorType().equals(tbMonitorType.getMonitorType())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型和监测项需要匹配");
            }

        } else if (monitorType.equals(-1) && monitorItemID.equals(-1)) {
        } else {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型和监测项需要匹配");
        }
        if (StringUtils.isNotBlank(exValue) && !JSONUtil.isTypeJSON(exValue)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "exValue不是合法的JSON格式");
        }
        if (ruleType != 2 && !NumberUtil.isInteger(productID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "productID不是合法的数字");
        }
        if (!deviceCSV.equals("all")) {
            try {
                if (Arrays.stream(deviceCSV.split(",")).anyMatch(s -> !NumberUtil.isInteger(s))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "deviceCSV不是合法的数字");
                }
            } catch (Exception e) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "deviceCSV不是合法的数字");
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
}
