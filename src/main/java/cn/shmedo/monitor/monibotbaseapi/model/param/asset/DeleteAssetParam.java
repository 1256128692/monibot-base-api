package cn.shmedo.monitor.monibotbaseapi.model.param.asset;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 17:27
 **/
@Data
public class DeleteAssetParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Valid
    private List<@NotNull Integer> assetIDList;

    @Override
    public ResultWrapper validate() {
        TbAssetMapper tbAssetMapper = ContextHolder.getBean(TbAssetMapper.class);
        List<TbAsset> tbAssets = tbAssetMapper.selectBatchIds(assetIDList);
        if (tbAssets.size() != assetIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资产不存在");
        }
        if (tbAssets.stream().anyMatch(tbAsset -> !tbAsset.getCompanyID().equals(companyID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有资产不属于该公司");
        }
        // 使用入出库记录
        TbAssetLogMapper tbAssetLogMapper = ContextHolder.getBean(TbAssetLogMapper.class);
        List<TbAssetLog> tbAssetLogs = tbAssetLogMapper.selectList(
                new LambdaQueryWrapper<TbAssetLog>().in(TbAssetLog::getAssetID, assetIDList)
        );
        tbAssetLogs.stream().collect(Collectors.groupingBy(TbAssetLog::getAssetID)).forEach((k, v) -> {
            if (v.stream().mapToInt(TbAssetLog::getValue).sum() != 0) {
                throw new CustomBaseException(ResultCode.INVALID_PARAMETER.toInt(), "有资产有入出库记录");
            }
        });
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
