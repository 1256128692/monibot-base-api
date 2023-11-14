package cn.shmedo.monitor.monibotbaseapi.model.param.asset;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetHouseMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetHouse;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-20 15:41
 **/
@Data
public class QueryAssetCurrentValueParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer houseID;
    @NotNull
    private Integer assetID;

    @Override
    public ResultWrapper validate() {
        TbAssetHouseMapper tbAssetHouseMapper = ContextHolder.getBean(TbAssetHouseMapper.class);
        TbAssetHouse tbAssetHouse = tbAssetHouseMapper.selectById(houseID);
        if (tbAssetHouse == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "仓库不存在");
        }
        if (!tbAssetHouse.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "仓库不属于该公司");
        }

        TbAssetMapper tbAssetMapper = ContextHolder.getBean(TbAssetMapper.class);
        TbAsset tbAsset = tbAssetMapper.selectById(assetID);
        if (tbAsset == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产不存在");
        }
        if (!tbAsset.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产不属于该公司");
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
}
