package cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice;

import cn.hutool.core.util.NumberUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-05 14:37
 **/
@Data
public class QueryWtDevicePageListParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
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
    private String monitorItemName;
    private Integer ruleID;
    private Boolean select;
    @Positive
    private Integer monitorPointID;
    @Positive
    @NotNull
    private Integer currentPage;
    @Positive
    @NotNull
    @Max(100)
    private Integer pageSize;

    @JsonIgnore
    private List<TbProjectInfo> projectInfos;

    @Override
    public ResultWrapper validate() {
        Collection<Integer> permissionProjectList = PermissionUtil.getHavePermissionProjectList(companyID, projectIDList);
        if (permissionProjectList.isEmpty()) {
            return ResultWrapper.success(PageUtil.PageWithMap.empty());
        }
        this.projectIDList = permissionProjectList.stream().toList();
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);

        projectInfos = tbProjectInfoMapper.selectList(
                new QueryWrapper<TbProjectInfo>().lambda()
                        .in(TbProjectInfo::getID, projectIDList)
                        .eq(TbProjectInfo::getCompanyID, companyID)
        );

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
