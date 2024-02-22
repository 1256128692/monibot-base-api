package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDeviceWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataDeviceWarnType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 15:08
 */
@Data
public class WarnHandleParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "报警记录ID不能为空")
    @Positive(message = "报警记录ID必须为正值")
    private Integer warnLogID;
    @Range(min = 1, max = 2, message = "报警类型 1.数据报警 2.设备报警")
    @NotNull(message = "报警类型不能为空")
    private Integer warnType;
    @JsonIgnore
    private DataDeviceWarnType dataDeviceWarnType;
    @JsonIgnore
    private TbDataWarnLog tbDataWarnLog;
    @JsonIgnore
    private TbDeviceWarnLog tbDeviceWarnLog;

    @Override
    public ResultWrapper<?> validate() {
        dataDeviceWarnType = DataDeviceWarnType.fromCode(warnType);
        switch (dataDeviceWarnType) {
            case DATA -> {
                List<TbDataWarnLog> tbDataWarnLogList = ContextHolder.getBean(TbDataWarnLogMapper.class)
                        .selectList(new LambdaQueryWrapper<TbDataWarnLog>().eq(TbDataWarnLog::getId, warnLogID)
                                .eq(TbDataWarnLog::getDealStatus, 0));
                if (CollUtil.isEmpty(tbDataWarnLogList)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "数据报警记录不存在或已被处理");
                }
                tbDataWarnLog = tbDataWarnLogList.stream().findAny().orElseThrow();
            }
            case DEVICE -> {
                List<TbDeviceWarnLog> tbDeviceWarnLogList = ContextHolder.getBean(TbDeviceWarnLogMapper.class)
                        .selectList(new LambdaQueryWrapper<TbDeviceWarnLog>().eq(TbDeviceWarnLog::getId, warnLogID)
                                .eq(TbDeviceWarnLog::getDealStatus, 0));
                if (CollUtil.isEmpty(tbDeviceWarnLogList)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "设备报警记录不存在或已被处理");
                }
                tbDeviceWarnLog = tbDeviceWarnLogList.stream().findAny().orElseThrow();
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
