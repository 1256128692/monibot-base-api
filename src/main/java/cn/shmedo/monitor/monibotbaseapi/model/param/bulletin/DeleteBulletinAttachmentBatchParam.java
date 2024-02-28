package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbBulletinAttachmentMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinAttachment;
import cn.shmedo.monitor.monibotbaseapi.model.enums.BulletinAttachmentType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-28 09:49
 */
@Data
public class DeleteBulletinAttachmentBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @NotEmpty(message = "要删除的附件IDList不能为空")
    private List<Integer> attachmentIDList;

    @Override
    public ResultWrapper<?> validate() {
        if (attachmentIDList.size() == 1 && !ContextHolder.getBean(TbBulletinAttachmentMapper.class)
                .exists(new LambdaQueryWrapper<TbBulletinAttachment>().in(TbBulletinAttachment::getId, attachmentIDList)
                        .eq(TbBulletinAttachment::getType, BulletinAttachmentType.OSS_FILE.getCode()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要删除的附件不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
