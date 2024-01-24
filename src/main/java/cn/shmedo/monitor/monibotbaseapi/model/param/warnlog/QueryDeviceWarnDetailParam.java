package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDeviceWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-23 14:29
 */
@Data
public class QueryDeviceWarnDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @Positive(message = "公司ID为正值")
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @Positive(message = "报警记录ID为正值")
    @NotNull(message = "报警记录ID不能为空")
    private Integer warnLogID;
    @JsonIgnore
    private TbDeviceWarnLog tbDeviceWarnLog;

    @Override
    public ResultWrapper<?> validate() {
        List<TbDeviceWarnLog> tbDeviceWarnLogList = ContextHolder.getBean(TbDeviceWarnLogMapper.class)
                .selectList(new LambdaQueryWrapper<TbDeviceWarnLog>().eq(TbDeviceWarnLog::getId, warnLogID)
                        .eq(TbDeviceWarnLog::getCompanyID, companyID));
        if (CollUtil.isEmpty(tbDeviceWarnLogList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警记录不存在");
        }
        tbDeviceWarnLog = tbDeviceWarnLogList.stream().findAny().orElseThrow();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
