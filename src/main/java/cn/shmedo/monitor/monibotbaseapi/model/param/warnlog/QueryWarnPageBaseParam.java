package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IPlatformCheck;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-19 16:08
 */
@Data
public class QueryWarnPageBaseParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck {
    @Positive(message = "公司ID为正值")
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @NotNull(message = "页大小不能为空")
    @Range(min = 1, max = 100, message = "分页大小必须在1~100之间")
    private Integer pageSize;
    @NotNull(message = "当前页不能为空")
    @Positive(message = "当前页必须大于0")
    private Integer currentPage;
    @NotNull(message = "平台key不能为空")
    @Positive(message = "平台key必须为正值")
    private Integer platform;
    private Boolean isRealTime;
    private String queryCode;
    private Integer projectID;
    private Date startTime;
    private Date endTime;
    @Range(min = 0, max = 2, message = "处理状态 0:未处理 1:已处理 2:取消")
    private Integer dealStatus;
    @JsonIgnore
    private TbWarnBaseConfig tbWarnBaseConfig;

    @Override
    public ResultWrapper<?> validate() {
        if (!validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在!");
        }
        isRealTime = Objects.isNull(isRealTime) || isRealTime;
        if (Objects.nonNull(projectID) && !ContextHolder.getBean(TbProjectInfoMapper.class).exists(new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getID, projectID).eq(TbProjectInfo::getCompanyID, companyID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程不存在或者用户缺少权限");
        }
        tbWarnBaseConfig = ContextHolder.getBean(ITbWarnBaseConfigService.class).queryByCompanyIDAndPlatform(getCompanyID(), getPlatform());
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
