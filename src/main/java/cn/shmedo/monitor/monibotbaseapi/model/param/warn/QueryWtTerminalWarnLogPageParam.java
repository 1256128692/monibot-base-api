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
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Chengfs on 2023/5/4
 */
@Data
public class QueryWtTerminalWarnLogPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @Min(value = 1)
    @NotNull
    private Integer companyID;

    @Range(min = 1, max = 2)
    @NotNull
    private Integer queryType;

    private String queryCode;

    private Integer monitorTypeID;

    private Integer monitorItemID;

    @Range(min = 1, max = 4)
    private Integer warnLevel;

    @Range(min = 1, max = 2)
    private Integer orderType;

    @Min(value = 1)
    @NotNull
    private Integer currentPage;

    @Range(min = 1, max = 100)
    @NotNull
    private Integer pageSize;

    @Range(min = 3, max = 3)
    private Integer warnType;

    @JsonIgnore
    private Collection<Integer> projectIDList;

    @Override
    public ResultWrapper<?> validate() {
        this.projectIDList = PermissionUtil.getHavePermissionProjectList(companyID);
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
        warnType = Optional.ofNullable(warnType).orElse(3);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }
}