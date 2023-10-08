package cn.shmedo.monitor.monibotbaseapi.model.param.asset;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetHouseMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetHouse;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AssetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-08 10:47
 **/
@Data
public class QueryAssetIOLogPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    private Integer houseID;
    private String fuzzyItem;
    private Byte type;
    private Boolean inOrOut;
    @NotNull
    @Positive
    private Integer currentPage;
    @NotNull
    @Range(min = 1, max = 100)
    private Integer pageSize;

    @Override
    public ResultWrapper validate() {
        if (houseID != null) {
            TbAssetHouseMapper tbAssetHouseMapper = ContextHolder.getBean(TbAssetHouseMapper.class);
            TbAssetHouse tbAssetHouse = tbAssetHouseMapper.selectById(houseID);
            if (tbAssetHouse == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产库不存在");
            }
            if (!tbAssetHouse.getCompanyID().equals(companyID)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产库不属于该公司");
            }
        }
        if (type != null && !AssetType.isExist(type)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产类型不存在");
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
