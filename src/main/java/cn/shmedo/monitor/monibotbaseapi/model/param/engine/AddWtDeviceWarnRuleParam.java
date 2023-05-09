package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

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
    private Integer pointID;
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    private Boolean enable;
    @Size(max = 1000)
    private String desc;
    @Size(max = 1000)
    private String exValue;
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
        if (ruleType == 1) {
            if (monitorType != -1 && monitorItemID != -1 && pointID != null) {
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
                TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
                TbMonitorPoint tbMonitorPoint = tbMonitorPointMapper.selectById(pointID);
                if (tbMonitorPoint == null) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
                }
                if (!tbMonitorPoint.getProjectID().equals(projectID)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不属于该项目");
                }
                if (!tbMonitorPoint.getMonitorItemID().equals(monitorItemID)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点需要匹配监测项目");
                }

            } else {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "需要输入monitorType和monitorItemID和pointID");
            }
        } else {
            if (StringUtils.isNotBlank(productID) && StringUtils.isNotBlank(deviceCSV)) {
                if (!deviceCSV.equals("all")) {
                    try {
                        List<String> strings = Arrays.stream(deviceCSV.split(",")).toList();
                        if (ruleType == 3 && strings.stream().anyMatch(s -> !NumberUtil.isInteger(s))) {
                            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "deviceCSV不是合法的数字");
                        }

                    } catch (Exception e) {
                        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "deviceCSV格式不正确");
                    }
                }
            } else {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "需要输入productID和deviceCSV");
            }

        }
        if (StringUtils.isNotBlank(exValue) && !JSONUtil.isTypeJSON(exValue)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "exValue不是合法的JSON格式");
        }
        if (ruleType == 3 && !NumberUtil.isInteger(productID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "productID不是合法的数字");
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
