package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;

@Data
public class QueryWtEnginePageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @Range(min = 1, max = 3, message = "规则类型, 1:报警规则 2:视频规则 3:智能终端规则")
    private Integer ruleType;
    @Min(value = 1, message = "终端设备产品ID不能小于1")
    private Integer productID;
    private String videoTypeName;
    private String queryCode;
    @Min(value = 1, message = "工程项目ID不能小于1")
    private Integer projectID;
    private String engineName;
    private Boolean enable;
    @Min(value = 1, message = "监测项目ID不能小于1")
    private Integer monitorItemID;
    @Min(value = 1, message = "监测点位ID不能小于1")
    private Integer monitorPointID;
    @Min(value = 1, message = "当前页不能小于1")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;
    @Range(min = 1, max = 100, message = "分页大小必须在1~100")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;
    @Range(min = 1, max = 2, message = "排序规则 1.按照创建时间降序排序(默认) 2.按照创建时间升序排序")
    private Integer orderType;

    @Override
    public ResultWrapper validate() {
        orderType = Objects.isNull(orderType) ? 1 : orderType;
        ruleType = Objects.isNull(ruleType) ? WarnType.MONITOR.getCode() : ruleType;
        if (WarnType.MONITOR.getCode().equals(ruleType)) {
            if (projectID != null) {
                TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
                if (tbProjectInfoMapper.selectCount(new LambdaQueryWrapper<TbProjectInfo>()
                        .eq(TbProjectInfo::getID, projectID)) < 1) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
                }
            }
            if (monitorItemID != null) {
                TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
                if (tbMonitorItemMapper.selectCount(new LambdaQueryWrapper<TbMonitorItem>()
                        .eq(TbMonitorItem::getID, monitorItemID)) < 1) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
                }
            }
            if (monitorPointID != null) {
                TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
                if (tbMonitorPointMapper.selectCount(new LambdaQueryWrapper<TbMonitorPoint>()
                        .eq(TbMonitorPoint::getID, monitorPointID)) < 1) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
                }
            }
        }
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
