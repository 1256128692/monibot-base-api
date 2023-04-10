package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateWtMonitorClassParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer projectID;

    @NotNull
    private Integer monitorClass;

    @NotEmpty
    private List<Integer> monitorItemIDList;

    @NotNull
    private Boolean enable;

    @NotBlank
    private String density;


    @Override
    public ResultWrapper<?> validate() {
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        LambdaQueryWrapper<TbMonitorItem> wrapper = new LambdaQueryWrapper<TbMonitorItem>()
                .eq(TbMonitorItem::getProjectID, projectID)
                .in(TbMonitorItem::getID, monitorItemIDList)
                .ne(TbMonitorItem::getMonitorClass, monitorClass);
        List<TbMonitorItem> tbMonitorItems = tbMonitorItemMapper.selectList(wrapper);

        if (!CollectionUtil.isNullOrEmpty(tbMonitorItems)) {
            long count = tbMonitorItems.stream()
                    .filter(item -> item.getMonitorClass() != null).map(TbMonitorItem::getMonitorClass).distinct().count();
            if (count > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "目标监测项目列表存在其他监测类别,请先解绑其它监测项目的类别");
            }
        }

        if (!(density.endsWith("h") || density.endsWith("d") || density.endsWith("m") || density.equals("all"))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "查询密度仅支持小时或者分钟或天");
        }

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
