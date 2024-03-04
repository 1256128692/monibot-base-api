package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbBulletinDataMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-04 13:29
 */
@Data
public abstract class BaseHandleBulletinDataBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotEmpty(message = "公告ID List不能为空")
    private List<Integer> bulletinIDList;
    @JsonIgnore
    private List<TbBulletinData> tbBulletinDataList;

    @Override
    public ResultWrapper<?> validate() {
        tbBulletinDataList = ContextHolder.getBean(TbBulletinDataMapper.class).selectList(new LambdaQueryWrapper<TbBulletinData>()
                .in(TbBulletinData::getId, bulletinIDList).eq(TbBulletinData::getCompanyID, companyID));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
