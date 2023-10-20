package cn.shmedo.monitor.monibotbaseapi.model.param.warn;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Chengfs on 2023/5/4
 */
@Data
public class QueryWtWarnListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer companyID;

    @Positive
    private Integer projectID;

    @Positive
    private Integer monitorTypeID;

    @Positive
    private Integer monitorItemID;

    @Range(min = 1, max = 4)
    private Integer warnLevel;

    @Range(min = 1, max = 2)
    private Integer orderType;

    private WarnType warnType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @JsonIgnore
    private Collection<Integer> projectIDList;

    @Override
    public ResultWrapper<?> validate() {
        this.projectIDList = projectID != null ? List.of(projectID) : null;
        this.projectIDList = PermissionUtil.getHavePermissionProjectList(companyID, this.projectIDList);
        if (CollUtil.isEmpty(projectIDList)) {
            return ResultWrapper.success(PageUtil.Page.empty());
        }

        Optional.ofNullable(monitorTypeID).ifPresent(monitorType -> {
            TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
            Assert.isTrue(tbMonitorTypeMapper.selectCount(new LambdaQueryWrapper<TbMonitorType>()
                    .eq(TbMonitorType::getID, monitorTypeID)) > 0, "监测类型不存在");
        });

        Optional.ofNullable(monitorItemID).ifPresent(monitorItem -> {
            TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
            Assert.isTrue(tbMonitorItemMapper.selectCount(new LambdaQueryWrapper<TbMonitorItem>()
                    .eq(TbMonitorItem::getID, monitorItemID)) > 0, "监测项目不存在");
        });

        this.orderType = Optional.ofNullable(orderType).orElse(1);

        if (beginTime != null && endTime != null) {
            Assert.isTrue(beginTime.before(endTime), "开始时间不能大于结束时间");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }
}