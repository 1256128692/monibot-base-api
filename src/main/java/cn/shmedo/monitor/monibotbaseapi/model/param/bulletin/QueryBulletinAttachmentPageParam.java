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
import org.hibernate.validator.constraints.Range;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:31
 */
@Data
public class QueryBulletinAttachmentPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "公告ID不能为空")
    @Positive(message = "公告ID不能小于1")
    private Integer bulletinID;
    @NotNull(message = "页大小不能为空")
    @Range(min = 1, max = 100, message = "分页大小必须在1~100之间")
    private Integer pageSize;
    @NotNull(message = "当前页不能为空")
    @Positive(message = "当前页必须大于0")
    private Integer currentPage;
    private String fileName;

    @Override
    public ResultWrapper<?> validate() {
        if (!ContextHolder.getBean(TbBulletinDataMapper.class).exists(new LambdaQueryWrapper<TbBulletinData>().eq(TbBulletinData::getId, bulletinID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公告不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
