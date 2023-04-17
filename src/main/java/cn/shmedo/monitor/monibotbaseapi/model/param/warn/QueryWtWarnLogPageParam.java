package cn.shmedo.monitor.monibotbaseapi.model.param.warn;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class QueryWtWarnLogPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @Min(value = 1, message = "公司ID不能小于1")
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @Range(min = 1, max = 2, message = "查询类型不合法,查询类型:1.实时记录 2.历史记录")
    @NotNull(message = "查询类型不能为空")
    private Integer queryType;
    private String queryCode;
    private Integer monitorTypeID;
    private Integer monitorItemID;
    @Range(min = 1, max = 4, message = "报警等级必须在1~4之间")
    private Integer warnLevel;
    @Range(min = 1, max = 2, message = "排序规则不合法,排序规则 1.按照报警时间降序排序(默认) 2.按照报警时间升序排序")
    private Integer orderType;
    @Min(value = 1, message = "当前页不能小于1")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;
    @Range(min = 1, max = 100, message = "分页大小必须在1~100")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    @Override
    public ResultWrapper validate() {
        if (monitorTypeID != null) {
            TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
            if (tbMonitorTypeMapper.selectOne(new LambdaQueryWrapper<TbMonitorType>()
                    .eq(TbMonitorType::getID, monitorTypeID).select(TbMonitorType::getID)) == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
            }
        }
        if (monitorItemID != null) {
            TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
            if (tbMonitorItemMapper.selectOne(new LambdaQueryWrapper<TbMonitorItem>()
                    .eq(TbMonitorItem::getID, monitorItemID).select(TbMonitorItem::getID)) == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
