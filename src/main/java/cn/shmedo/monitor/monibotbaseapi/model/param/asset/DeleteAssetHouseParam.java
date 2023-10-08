package cn.shmedo.monitor.monibotbaseapi.model.param.asset;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetHouseMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetHouse;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-08 10:29
 **/
@Data
public class DeleteAssetHouseParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Valid
    private List<@NotNull Integer> houseIDList;

    @Override
    public ResultWrapper validate() {
        TbAssetHouseMapper tbAssetHouseMapper = ContextHolder.getBean(TbAssetHouseMapper.class);
        List<TbAssetHouse> tbAssetHouses = tbAssetHouseMapper.selectBatchIds(houseIDList);
        if (tbAssetHouses.size() != houseIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产库不存在");
        }
        if (tbAssetHouses.stream().anyMatch(tbAssetHouse -> !tbAssetHouse.getCompanyID().equals(companyID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有不属于该公司的资产库");
        }
        RedisService redisService = ContextHolder.getBean(RedisService.class);
        Map<String, String> all = redisService.getAll(RedisKeys.ASSET_HOUSE_KEY);
        if (houseIDList.stream().anyMatch(e -> all.containsKey(e.toString()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有资产库下有资产");
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
