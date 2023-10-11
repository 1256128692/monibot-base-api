package cn.shmedo.monitor.monibotbaseapi.model.param.asset;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetHouseMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetHouse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 18:06
 **/
@Data
public class UpdateAssetHouseParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    @JsonAlias("ID")
    private Integer ID;
    @NotBlank
    private String name;
    private String code;
    @NotBlank
    private String address;
    private String comment;
    private String contactPerson;
    private String contactNumber;
    @Pattern(regexp = "^.+$", message = "扩展字段应为JSON格式")
    private String exValue;

    @JsonAlias
    @ToString.Exclude
    private TbAssetHouse tbAssetHouse;

    @Override
    public ResultWrapper validate() {
        TbAssetHouseMapper tbAssetHouseMapper = ContextHolder.getBean(TbAssetHouseMapper.class);
        tbAssetHouse = tbAssetHouseMapper.selectById(ID);
        if (tbAssetHouse == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产不存在");
        }
        if (!tbAssetHouse.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公司ID不匹配");
        }
        if (tbAssetHouseMapper.selectCount(new LambdaQueryWrapper<TbAssetHouse>()
                .eq(TbAssetHouse::getCompanyID, companyID)
                .eq(TbAssetHouse::getName, name)
                .ne(TbAssetHouse::getID, ID)
        ) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公司下资产名称已存在");
        }
        if (ObjectUtil.isNotEmpty(exValue) && !JSONUtil.isTypeJSON(exValue)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "扩展字段应为JSON格式");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    public TbAssetHouse update(Integer subjectID) {
        tbAssetHouse.setName(name);
        tbAssetHouse.setCode(code);
        tbAssetHouse.setAddress(address);
        tbAssetHouse.setComment(comment);
        tbAssetHouse.setContactPerson(contactPerson);
        tbAssetHouse.setContactNumber(contactNumber);
        tbAssetHouse.setExValue(exValue);
        tbAssetHouse.setUpdateUserID(subjectID);
        tbAssetHouse.setUpdateTime(new Date());
        return tbAssetHouse;
    }
}
