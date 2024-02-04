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

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 16:24
 */
@Data
public class QueryDataWarnDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @Positive(message = "公司ID为正值")
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @Positive(message = "报警记录ID为正值")
    @NotNull(message = "报警记录ID不能为空")
    private Integer warnLogID;
    @JsonIgnore
    private TbDataWarnLog tbDataWarnLog;

    @Override
    public ResultWrapper<?> validate() {
        List<TbDataWarnLog> tbDataWarnLogList = ContextHolder.getBean(TbDataWarnLogMapper.class)
                .selectList(new LambdaQueryWrapper<TbDataWarnLog>().eq(TbDataWarnLog::getId, warnLogID)
                        .eq(TbDataWarnLog::getCompanyID, companyID));
        if (CollUtil.isEmpty(tbDataWarnLogList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警记录不存在");
        }
        tbDataWarnLog = tbDataWarnLogList.stream().findAny().orElseThrow();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
