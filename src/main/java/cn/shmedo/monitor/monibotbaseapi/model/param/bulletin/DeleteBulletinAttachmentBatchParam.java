package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.hutool.core.collection.CollUtil;
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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        //.eq(TbBulletinAttachment::getType, BulletinAttachmentType.OSS_FILE.getCode())
        Map<Integer, TbBulletinAttachment> attachmentIDMap = ContextHolder.getBean(TbBulletinAttachmentMapper.class)
                .selectList(new LambdaQueryWrapper<TbBulletinAttachment>().in(TbBulletinAttachment::getId, attachmentIDList))
                .stream().collect(Collectors.toMap(TbBulletinAttachment::getId, Function.identity()));
        if (attachmentIDMap.values().stream().anyMatch(u -> !BulletinAttachmentType.OSS_FILE.getCode().equals(u.getType()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要删除的附件IDList里存在其他文件的ID");
        }
        if (attachmentIDList.size() == 1 && CollUtil.isEmpty(attachmentIDMap)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要删除的附件不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
