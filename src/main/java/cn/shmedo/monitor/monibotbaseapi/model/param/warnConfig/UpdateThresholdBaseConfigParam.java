package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IPlatformCheck;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 10:19
 */
@Data
public class UpdateThresholdBaseConfigParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck {
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID必须为正值")
    private Integer projectID;
    @NotNull(message = "平台key不能为空")
    @Positive(message = "平台key必须为正值")
    private Integer platform;
    @NotNull(message = "监测项目ID不能为空")
    @Positive(message = "监测项目ID必须为正值")
    private Integer monitorItemID;
    @Range(min = 1, max = 2, message = "触发设置类型 1.有数据满足规则,直接触发对应等级报警 2.有连续n次数据满足规则,再触发对应等级报警")
    private Integer triggerType;
    private Integer triggerTimes;
    @NotEmpty
    private List<Object> aliasConfigList;

    @Override
    public ResultWrapper validate() {
        if (!validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在!");
        }
        if (!ContextHolder.getBean(TbMonitorItemMapper.class).exists(new LambdaQueryWrapper<TbMonitorItem>()
                .eq(TbMonitorItem::getID, monitorItemID).eq(TbMonitorItem::getProjectID, projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
        }
        if (Objects.nonNull(triggerType) && triggerType == 1) {
            triggerTimes = -1;
        }
        if ((Objects.isNull(triggerType) && Objects.nonNull(triggerTimes)) || (Objects.isNull(triggerTimes) && Objects.nonNull(triggerType))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警触发配置不合法");
        }


        //TODO
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
