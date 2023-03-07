package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProjectImageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotBlank
    @Size(max = 100)
    private String fileName;
    @NotBlank
    private String imageContent;
    @NotBlank
    private String imageSuffix;

    @JsonIgnore
    private Integer companyID;


    @Override
    public ResultWrapper validate() {
        if (fileName.contains(".")){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "文件名称不能出现特殊字符,例如.");
        }

        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        LambdaQueryWrapper<TbProjectInfo> nameExistWrapper = new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getID, this.projectID);
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectOne(nameExistWrapper);
        if (!ObjectUtil.isNotNull(tbProjectInfo)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不存在");
        }
        this.companyID = tbProjectInfo.getCompanyID();
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
