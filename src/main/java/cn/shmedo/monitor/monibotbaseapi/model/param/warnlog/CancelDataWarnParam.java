package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 14:37
 */
@Data
public class CancelDataWarnParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "报警记录ID不能为空")
    @Positive(message = "报警记录ID必须为正值")
    private Integer warnLogID;
    @Range(max = 168, message = "沉默周期最大为168h")
    private Integer silenceCycle;
    @JsonIgnore
    private TbDataWarnLog tbDataWarnLog;

    @Override
    public ResultWrapper<?> validate() {
        List<TbDataWarnLog> tbDataWarnLogList = ContextHolder.getBean(TbDataWarnLogMapper.class)
                .selectList(new LambdaQueryWrapper<TbDataWarnLog>().eq(TbDataWarnLog::getId, warnLogID)
                        .isNull(TbDataWarnLog::getWarnEndTime));
        if (CollUtil.isEmpty(tbDataWarnLogList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警记录不存在,或者报警已结束");
        }
        tbDataWarnLog = tbDataWarnLogList.stream().findAny().orElseThrow();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
