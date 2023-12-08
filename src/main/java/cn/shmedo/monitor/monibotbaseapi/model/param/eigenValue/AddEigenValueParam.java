package cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AddEigenValueParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    @NotNull(message = "作用域scope不能为空")
    private Integer scope;
    @NotNull(message = "监测项目ID不能为空")
    private Integer monitorItemID;
    @NotEmpty
    private List<@NotNull Integer> monitorPointIDList;
    @NotNull(message = "监测类型子类型monitorTypeFieldID不能为空")
    private Integer monitorTypeFieldID;
    @NotBlank(message = "名称不能为空")
    private String name;
    @NotNull(message = "值不能为空")
    private Double value;
    @NotNull(message = "单位ID不能为空")
    private Integer unitID;
    private String exValue;
    private Integer eigenValueID;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private Integer createUserID;
    @JsonIgnore
    private Integer updateUserID;

    public static TbEigenValue toNewVo(AddEigenValueParam pa, Integer subjectID) {
        DateTime date = DateUtil.date();
        TbEigenValue vo = new TbEigenValue();
        if (pa.getEigenValueID() != null) {
            vo.setId(pa.getEigenValueID());
        }
        vo.setValue(pa.getValue());
        vo.setExValue(pa.getExValue());
        vo.setName(pa.getName());
        vo.setScope(pa.getScope());
        vo.setCreateTime(date);
        vo.setUpdateTime(date);
        vo.setMonitorItemID(pa.getMonitorItemID());
        vo.setMonitorTypeFieldID(pa.getMonitorTypeFieldID());
        vo.setUnitID(pa.getUnitID());
        vo.setProjectID(pa.getProjectID());
        vo.setCreateUserID(subjectID);
        vo.setUpdateUserID(subjectID);
        return vo;
    }

    public static AddEigenValueParam toNewVo1(AddEigenValueParam pa, Integer subjectID) {
        DateTime date = DateUtil.date();
        AddEigenValueParam vo = new AddEigenValueParam();
        if (pa.getEigenValueID() != null) {
            vo.setEigenValueID(pa.getEigenValueID());
        }
        vo.setValue(pa.getValue());
        vo.setExValue(pa.getExValue());
        vo.setName(pa.getName());
        vo.setScope(pa.getScope());
        vo.setCreateTime(date);
        vo.setUpdateTime(date);
        vo.setMonitorItemID(pa.getMonitorItemID());
        vo.setMonitorTypeFieldID(pa.getMonitorTypeFieldID());
        vo.setUnitID(pa.getUnitID());
        vo.setProjectID(pa.getProjectID());
        vo.setCreateUserID(subjectID);
        vo.setUpdateUserID(subjectID);
        vo.setMonitorPointIDList(pa.getMonitorPointIDList());
        return vo;
    }

    @Override
    public ResultWrapper validate() {

        if (!ScopeType.isValidScopeType(scope)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:scope字段值的为作用范围非法类型");
        }

        // 重复名称校验
        // 专题分析的话不校验重名
        TbEigenValueMapper tbEigenValueMapper = ContextHolder.getBean(TbEigenValueMapper.class);
        if (scope == ScopeType.SPECIAL_ANALYSIS.getCode()) {
            Integer count = tbEigenValueMapper.selectCountByProjectIDAndItemIDAndFiledIDAndName(projectID, monitorItemID,
                    monitorTypeFieldID, name, monitorPointIDList);
            if (count > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "同一工程下的监测项目的监测点特征值名称已存在");
            }
        } else {
            Integer count = tbEigenValueMapper.selectCountByProjectIDAndItemIDAndFiledIDAndName(projectID, monitorItemID,
                    monitorTypeFieldID, name, null);
            if (count > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "同一工程下的监测项目的该特征值名称已存在");
            }
        }

        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectPointListByIDList(monitorPointIDList);
        if (CollectionUtil.isNullOrEmpty(tbMonitorPoints) || tbMonitorPoints.size() != monitorPointIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:监测点有不存在数据");
        }

        TbDataUnitMapper tbDataUnitMapper = ContextHolder.getBean(TbDataUnitMapper.class);
        TbDataUnit tbDataUnit = tbDataUnitMapper.selectByPrimaryKey(unitID);
        if (!ObjectUtil.isNotNull(tbDataUnit)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:单位ID不存在");
        }

        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        TbMonitorItem tbMonitorItem = tbMonitorItemMapper.selectByPrimaryKey(monitorItemID);
        if (!ObjectUtil.isNotNull(tbMonitorItem)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:监测项目不存在");
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
