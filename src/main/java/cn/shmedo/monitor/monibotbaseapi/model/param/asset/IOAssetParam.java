package cn.shmedo.monitor.monibotbaseapi.model.param.asset;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetHouseMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetHouse;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetLog;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-08 10:40
 **/
@Data
public class IOAssetParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer houseID;
    @NotNull
    private Integer assetID;
    @NotNull
    private Integer value;
    private String comment;

    @Override
    public ResultWrapper validate() {
        TbAssetMapper tbAssetMapper = ContextHolder.getBean(TbAssetMapper.class);
        TbAsset tbAsset = tbAssetMapper.selectById(assetID);
        if (tbAsset == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产不存在");
        }
        if (!tbAsset.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产不属于该公司");
        }
        TbAssetHouseMapper tbAssetHouseMapper = ContextHolder.getBean(TbAssetHouseMapper.class);
        TbAssetHouse tbAssetHouse = tbAssetHouseMapper.selectById(houseID);
        if (tbAssetHouse == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产库不存在");
        }
        if (!tbAssetHouse.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产库不属于该公司");
        }
        if (value == 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产变化值不能为0");
        }
        if (value < 0) {
            RedisService redisService = ContextHolder.getBean(RedisService.class);
            Map map = redisService.get(RedisKeys.ASSET_HOUSE_KEY, houseID.toString(), Map.class);
            if (map == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产库无资产");
            }
            if (!map.containsKey(assetID.toString())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产库无该资产");
            }
            Integer integer = (Integer) map.get(assetID.toString());
            if (integer + value < 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产库资产不足");
            }

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

    public TbAssetLog toEntity(CurrentSubject currentSubject) {
        return TbAssetLog.builder()
                .houseID(houseID)
                .assetID(assetID)
                .value(value)
                .comment(comment)
                .time(new Date())
                .userID(currentSubject.getSubjectID())
                .userName(currentSubject.getSubjectName())
                .build();
    }
}
