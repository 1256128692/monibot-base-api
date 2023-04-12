package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.List;

@Data
public class QueryMonitorPointsSensorDataListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer projectID;

    @NotNull
    private List<Integer> monitorPointIDs;

    @NotNull
    private Timestamp begin;

    @NotNull
    private Timestamp end;

    private String density;

    @JsonIgnore
    private List<TbMonitorPoint> tbMonitorPointList;

    @JsonIgnore
    private Integer monitorType;

    @JsonIgnore
    private Integer monitorItemID;

    @Override
    public ResultWrapper<?> validate() {
        // 加校验(1.监测点的项目ID必须与项目ID一致 2.密度不为空是,必须以h或者d结尾)
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        LambdaQueryWrapper<TbMonitorPoint> wrapper = new LambdaQueryWrapper<TbMonitorPoint>()
                .in(TbMonitorPoint::getID, monitorPointIDs);
        tbMonitorPointList = tbMonitorPointMapper.selectList(wrapper);

        if (CollectionUtil.isNullOrEmpty(tbMonitorPointList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未找到监测点数据");
        } else {
            long monitorTypeCount = tbMonitorPointList.stream().map(TbMonitorPoint::getMonitorType).distinct().count();
            if (monitorTypeCount > 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表数据的监测类型需一致");
            } else {
                monitorType = tbMonitorPointList.get(0).getMonitorType();
            }

            long monitorItemIDCount = tbMonitorPointList.stream().map(TbMonitorPoint::getMonitorItemID).distinct().count();
            if (monitorItemIDCount > 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表数据的监测项目需一致");
            } else {
                monitorItemID = tbMonitorPointList.get(0).getMonitorItemID();
            }

            long projectCount = tbMonitorPointList.stream().map(TbMonitorPoint::getProjectID).distinct().count();
            if (projectCount > 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表数据的所属工程需一致");
            }else {
                if (!projectID.equals(tbMonitorPointList.get(0).getProjectID())) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前请求工程ID与监测点所属工程ID不一致");
                }
            }
        }
        if (StringUtils.isNotBlank(density)) {
            if (!(density.endsWith("h") || density.endsWith("d") || density.endsWith("m"))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前查询密度条件错误");
            }
            // 传输条件为all时,置空查询密度,即可查询全部数据
            if (density.equals("all")){
                density = null;
            }
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
