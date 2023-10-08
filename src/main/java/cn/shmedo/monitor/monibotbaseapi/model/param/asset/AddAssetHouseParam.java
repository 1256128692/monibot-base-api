package cn.shmedo.monitor.monibotbaseapi.model.param.asset;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetHouseMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetHouse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 17:45
 **/
@Data
public class AddAssetHouseParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    @NotEmpty
    private String address;

    private String comment;

    @Override
    public ResultWrapper validate() {
        TbAssetHouseMapper tbAssetHouseMapper = ContextHolder.getBean(TbAssetHouseMapper.class);
        if (tbAssetHouseMapper.selectCount(new LambdaQueryWrapper<TbAssetHouse>()
                .eq(TbAssetHouse::getCompanyID, companyID)
                .eq(TbAssetHouse::getName, name)
        ) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公司下资产名称已存在");
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

    public TbAssetHouse toEntity(Integer subjectID) {
        Date now = new Date();
        return TbAssetHouse.builder()
                .companyID(companyID)
                .name(name)
                .code(code)
                .address(address)
                .comment(comment)
                .createUserID(subjectID)
                .updateUserID(subjectID)
                .createTime(now)
                .updateTime(now)
                .build();
    }
}
