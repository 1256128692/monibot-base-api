package cn.shmedo.monitor.monibotbaseapi.model.param.asset;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AssetType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 16:48
 **/
@Data
public class AddAssetParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotBlank
    private String name;
    @NotNull
    private String vendor;
    @NotNull
    @Range(min = 1, max = 8)
    private Byte unit;
    @NotNull
    private Byte type;
    private Integer warnValue;
    private String comparison;
    @Pattern(regexp = "^.+$", message = "扩展字段应为JSON格式")
    private String exValue;

    @Override
    public ResultWrapper validate() {
        TbAssetMapper tbAssetMapper = ContextHolder.getBean(TbAssetMapper.class);
        if (tbAssetMapper.selectCount(new LambdaQueryWrapper<TbAsset>().eq(TbAsset::getName, name).eq(TbAsset::getCompanyID, companyID)) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公司下资产名称已存在");
        }
        if (!AssetType.isExist(type)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产类型不存在");
        }
        if (ObjectUtil.isNotEmpty(comparison) && DefaultConstant.assetComparisonList.stream().noneMatch(item -> item.equals(comparison))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "比较方式不存在");
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

    public TbAsset toEntity(Integer subjectID) {
        Date now = new Date();
        return TbAsset.builder()
                .companyID(companyID)
                .name(name)
                .vendor(vendor)
                .unit(unit)
                .type(type)
                .warnValue(warnValue)
                .comparison(comparison)
                .exValue(exValue)
                .createTime(now)
                .createUserID(subjectID)
                .updateTime(now)
                .updateUserID(subjectID)
                .build();
    }
}
