package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IPlatformCheck;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 16:42
 */
@Data
public class QueryThresholdBaseConfigParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck {
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID必须为正值")
    private Integer projectID;
    @NotNull(message = "平台key不能为空")
    @Positive(message = "平台key必须为正值")
    private Integer platform;
    @NotNull(message = "监测项目ID不能为空")
    @Positive(message = "监测项目ID必须为正值")
    private Integer monitorItemID;
    @JsonIgnore
    private Integer companyID;
    @JsonIgnore
    private List<TbMonitorItem> tbMonitorItemList;

    @Override
    public ResultWrapper validate() {
        if (!validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在!");
        }
        List<TbProjectInfo> tbProjectInfoList = ContextHolder.getBean(TbProjectInfoMapper.class).selectList(new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getID, projectID));
        if (CollUtil.isEmpty(tbProjectInfoList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程不存在");
        }
        tbMonitorItemList = ContextHolder.getBean(TbMonitorItemMapper.class).selectList(new LambdaQueryWrapper<TbMonitorItem>()
                .eq(TbMonitorItem::getID, monitorItemID)
                .eq(TbMonitorItem::getProjectID, projectID));
        if (CollUtil.isEmpty(tbMonitorItemList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
        }
        this.companyID = tbProjectInfoList.stream().findAny().map(TbProjectInfo::getCompanyID).orElseThrow();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
