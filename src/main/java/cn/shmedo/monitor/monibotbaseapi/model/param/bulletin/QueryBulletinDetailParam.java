package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbBulletinDataMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-01 15:09
 */
@Data
public class QueryBulletinDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "公告ID不能为空")
    @Positive(message = "公告ID必须为正值")
    private Integer bulletinID;

    @Override
    public ResultWrapper<?> validate() {
        return ContextHolder.getBean(TbBulletinDataMapper.class).exists(new LambdaQueryWrapper<TbBulletinData>()
                .eq(TbBulletinData::getCompanyID, companyID).eq(TbBulletinData::getId, bulletinID)) ?
                null : ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公告不存在");
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
