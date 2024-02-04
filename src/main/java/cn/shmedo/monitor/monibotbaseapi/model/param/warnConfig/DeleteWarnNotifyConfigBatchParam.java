package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 13:44
 */
@Data
public class DeleteWarnNotifyConfigBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotEmpty(message = "报警通知配置ID List不能为空")
    private List<Integer> notifyConfigIDList;

    @Override
    public ResultWrapper<?> validate() {
        TbWarnNotifyConfigMapper tbWarnNotifyConfigMapper = ContextHolder.getBean(TbWarnNotifyConfigMapper.class);
        if (notifyConfigIDList.size() == 1) {
            if (!tbWarnNotifyConfigMapper.exists(new LambdaQueryWrapper<TbWarnNotifyConfig>()
                    .eq(TbWarnNotifyConfig::getId, notifyConfigIDList.get(0)).eq(TbWarnNotifyConfig::getCompanyID, companyID))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要删除的报警通知配置不存在");
            }
        }
        if (tbWarnNotifyConfigMapper.selectBatchIds(notifyConfigIDList).stream().anyMatch(u -> !u.getCompanyID().equals(companyID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在报警通知配置不属于当前公司");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
