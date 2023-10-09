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
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * @create: 2023-10-07 17:20
 **/
@Data
public class UpdateAssetParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    @JsonAlias("ID")
    private Integer ID;
    @NotBlank
    private String name;
    @NotNull
    private String vendor;
    @NotNull
    @Min(0)
    private Integer warnValue;
    @NotBlank
    private String comparison;
    @Pattern(regexp = "^.+$", message = "扩展字段应为JSON格式")
    private String exValue;

    @JsonIgnore
    private TbAsset tbAsset;

    @Override
    public ResultWrapper validate() {
        TbAssetMapper tbAssetMapper = ContextHolder.getBean(TbAssetMapper.class);
        tbAsset = tbAssetMapper.selectById(ID);
        if (tbAsset == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产不存在");
        }
        if (!tbAsset.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公司ID不匹配");
        }
        if (tbAssetMapper.selectCount(new LambdaQueryWrapper<TbAsset>()
                .eq(TbAsset::getName, name)
                .eq(TbAsset::getCompanyID, companyID)
                .ne(TbAsset::getID, ID)) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公司下资产名称已存在");
        }
        if (DefaultConstant.assetComparisonList.stream().noneMatch(item -> item.equals(comparison))) {
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

    public TbAsset update(Integer subjectID) {
        tbAsset.setName(name);
        tbAsset.setVendor(vendor);
        tbAsset.setWarnValue(warnValue);
        tbAsset.setComparison(comparison);
        tbAsset.setExValue(exValue);
        tbAsset.setUpdateTime(new Date());
        tbAsset.setUpdateUserID(subjectID);
        return tbAsset;
    }


}
