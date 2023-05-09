package cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice;

import cn.hutool.core.util.NumberUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-09 10:00
 **/
@Data
public class ExportWtDeviceParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
    @NotNull
    private Integer companyID;
    @Valid
    @Size(max = 100)
    private List<@NotNull Integer> projectIDList;
    private String queryCode;
    private Integer productID;
    private Boolean online;
    private Integer status;
    private String areaCode;
    private Integer monitorItemID;
    private Integer ruleID;
    private Boolean select;

    @JsonIgnore
    private List<TbProjectInfo> projectInfos;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (CollectionUtils.isNotEmpty(projectIDList)) {
            if (projectIDList.stream().distinct().count() != projectIDList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "projectIDList中有重复的项目ID");
            }
            projectInfos = tbProjectInfoMapper.selectList(
                    new QueryWrapper<TbProjectInfo>().lambda()
                            .in(TbProjectInfo::getID, projectIDList)
                            .eq(TbProjectInfo::getCompanyID, companyID)
            );
            if (CollectionUtils.isEmpty(projectInfos) || projectInfos.size() != projectIDList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有项目不存在或者不属于该公司");
            }
        } else {
            projectInfos = tbProjectInfoMapper.selectList(
                    new QueryWrapper<TbProjectInfo>().lambda().eq(TbProjectInfo::getCompanyID, companyID)
            );
            if (CollectionUtils.isEmpty(projectInfos)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该公司下没有项目");

            }
        }
        if (status != null && status != 0 && status != 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "status只能为0或1");
        }
        if (StringUtils.isNotBlank(areaCode) && !NumberUtil.isNumber(areaCode)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "区域编码必须为数字");
        }
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return projectInfos.stream().map(projectInfo -> new Resource(projectInfo.getID().toString(), ResourceType.BASE_PROJECT)).collect(Collectors.toList());

    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}
