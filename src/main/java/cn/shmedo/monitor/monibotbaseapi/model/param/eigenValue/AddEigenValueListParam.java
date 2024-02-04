package cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataUnitMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbEigenValueMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataUnit;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddEigenValueListParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;

    @NotEmpty
    private List<AddEigenValueParam> dataList;

    @Override
    public ResultWrapper validate() {
        TbEigenValueMapper tbEigenValueMapper = ContextHolder.getBean(TbEigenValueMapper.class);
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        TbDataUnitMapper tbDataUnitMapper = ContextHolder.getBean(TbDataUnitMapper.class);
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);

        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setProjectID(projectID);
            if (!ScopeType.isValidScopeType(dataList.get(i).getScope())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:scope字段值的为作用范围非法类型");
            }
            if (dataList.get(i).getScope() == ScopeType.SPECIAL_ANALYSIS.getCode()) {
                Integer count = tbEigenValueMapper.selectCountByProjectIDAndItemIDAndFiledIDAndName(projectID,
                        dataList.get(i).getMonitorItemID(), dataList.get(i).getMonitorTypeFieldID(),
                        dataList.get(i).getName(), dataList.get(i).getMonitorPointIDList());
                if (count > 0) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "同一工程下的监测项目的监测点特征值名称已存在");
                }
            } else {
                Integer count = tbEigenValueMapper.selectCountByProjectIDAndItemIDAndFiledIDAndName(projectID,
                        dataList.get(i).getMonitorItemID(), dataList.get(i).getMonitorTypeFieldID(),
                        dataList.get(i).getName(), null);
                if (count > 0) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "同一工程下的监测项目的该特征值名称已存在");
                }
            }
            List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectPointListByIDList(dataList.get(i).getMonitorPointIDList());
            if (CollectionUtil.isNullOrEmpty(tbMonitorPoints) || tbMonitorPoints.size() != dataList.get(i).getMonitorPointIDList().size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:监测点有不存在数据");
            }
            TbDataUnit tbDataUnit = tbDataUnitMapper.selectByPrimaryKey(dataList.get(i).getUnitID());
            if (!ObjectUtil.isNotNull(tbDataUnit)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:单位ID不存在");
            }
            TbMonitorItem tbMonitorItem = tbMonitorItemMapper.selectByPrimaryKey(dataList.get(i).getMonitorItemID());
            if (!ObjectUtil.isNotNull(tbMonitorItem)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:监测项目不存在");
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
