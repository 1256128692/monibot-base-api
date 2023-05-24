package cn.shmedo.monitor.monibotbaseapi.model.param.warn;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Range(min = 1, max = 2, message = "报警类型不合法,报警类型:1.在线监测报警记录; 2.视频报警记录")
    private Integer warnType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    @Range(min = 1, max = 6)
    private Integer status;
    @Min(value = 1)
    private Integer projectID;

    @JsonIgnore
    private Collection<Integer> projectIDList;

    @Override
    public ResultWrapper<?> validate() {
        this.projectIDList = PermissionUtil.getHavePermissionProjectList(companyID, projectID == null ? null: List.of(projectID));
        if (CollUtil.isEmpty(projectIDList)) {
            return ResultWrapper.success(PageUtil.Page.empty());
        }

        if (monitorTypeID != null) {
            TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
            if (tbMonitorTypeMapper.selectCount(new LambdaQueryWrapper<TbMonitorType>()
                    .eq(TbMonitorType::getID, monitorTypeID)) < 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
            }
        }
        if (monitorItemID != null) {
            TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
            if (tbMonitorItemMapper.selectCount(new LambdaQueryWrapper<TbMonitorItem>()
                    .eq(TbMonitorItem::getID, monitorItemID)) < 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
            }
        }

        warnType = Optional.ofNullable(warnType).orElse(1);
//        DateTime now = DateTime.now();
//        beginTime = Optional.ofNullable(beginTime).orElse(DateUtil.offsetDay(now, -7));
//        endTime = Optional.ofNullable(endTime).orElse(now);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
