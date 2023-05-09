package cn.shmedo.monitor.monibotbaseapi.model.param.warn;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-14 16:47
 */
@Data
public class QueryWtWarnDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "报警记录ID不能为空")
    @Min(value = 1, message = "报警记录ID不能小于1")
    private Integer warnID;

    @JsonIgnore
    private Integer warnType;

    @Override
    public ResultWrapper<?> validate() {
        TbWarnLogMapper tbWarnLogMapper = ContextHolder.getBean(TbWarnLogMapper.class);
        TbWarnLog warnLog = tbWarnLogMapper.selectOne(new LambdaQueryWrapper<TbWarnLog>()
                .eq(TbWarnLog::getID, warnID)
                .select(TbWarnLog::getWarnType));
        Assert.notNull(warnLog, "报警记录不存在");
        warnType = warnLog.getWarnType();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
