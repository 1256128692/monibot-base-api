package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 16:11
 **/
@Data
public class AddMonitorPointBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;


    @NotEmpty
    @Valid
    @Size(max = 100)
    private List<@NotNull AddPointItem> addPointItemList;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        List<Integer> typeList = addPointItemList.stream().map(AddPointItem::getMonitorType).distinct().toList();
        List<TbMonitorType> tbMonitorTypes = tbMonitorTypeMapper.selectList(
                new QueryWrapper<TbMonitorType>().lambda().in(TbMonitorType::getMonitorType, typeList)

        );
        if (CollectionUtils.isEmpty(tbMonitorTypes) || tbMonitorTypes.size() != typeList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测类型不存在");
        }
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        List<Integer> itemIDList = addPointItemList.stream().map(AddPointItem::getMonitorItemID).distinct().toList();
        List<TbMonitorItem> tbMonitorItems = tbMonitorItemMapper.selectBatchIds(
                itemIDList
        );
        if (tbMonitorItems.size() != itemIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测项目不存在");
        }
        if (tbMonitorItems.stream().anyMatch(item -> !item.getProjectID().equals(projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不属于该工程项目");

        }
        Map<Integer, TbMonitorItem> map = tbMonitorItems.stream().collect(Collectors.toMap(TbMonitorItem::getID, Function.identity()));
        if (addPointItemList.stream().anyMatch(item -> !map.get(item.getMonitorItemID()).getMonitorType().equals(item.getMonitorType()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目与监测类型不匹配");

        }
        if (addPointItemList.stream().map(AddPointItem::getName).distinct().count() != addPointItemList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "新增的监测点名称有重复");
        }
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        if (tbMonitorPointMapper.selectCount(
                new QueryWrapper<TbMonitorPoint>().eq("projectID", projectID).in("Name", addPointItemList.stream().map(AddPointItem::getName).toList())
        ) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目下监测点名称已存在");
        }
        // TODO 4个位置的校验

        if (addPointItemList.stream().anyMatch(item -> StringUtils.isNotBlank(item.getExValues()) && JSONUtil.isTypeJSON(item.getExValues()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有点的额外属性不合法");
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
