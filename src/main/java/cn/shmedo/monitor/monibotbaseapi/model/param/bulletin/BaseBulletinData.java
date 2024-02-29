package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinAttachment;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinPlatformRelation;
import cn.shmedo.monitor.monibotbaseapi.model.enums.BulletinAttachmentType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IMultiPlatformCheck;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 11:38
 */
@Data
public abstract class BaseBulletinData implements ParameterValidator, ResourcePermissionProvider<Resource>, IMultiPlatformCheck {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    private List<String> filePathList;
    @JsonIgnore
    private TbBulletinData tbBulletinData;
    @JsonIgnore
    private List<TbBulletinPlatformRelation> platformRelationList;
    @JsonIgnore
    private List<TbBulletinAttachment> tbBulletinAttachmentList;
    @JsonIgnore
    private final Date current = new Date();

    @Override
    public ResultWrapper<?> validate() {
        final List<Integer> platform = getPlatform();
        if (CollUtil.isNotEmpty(platform)) {
            if (!allPlatformValid()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有平台不存在!");
            }
            this.platformRelationList = platform.stream().map(u -> new TbBulletinPlatformRelation(null, u, null)).toList();
        }
        Optional.ofNullable(filePathList).filter(CollUtil::isNotEmpty).map(u -> u.stream().filter(StrUtil::isNotEmpty).map(w -> {
            TbBulletinAttachment attachment = new TbBulletinAttachment();
            attachment.setType(BulletinAttachmentType.fromFilePath(w).getCode());
            attachment.setCreateTime(current);
            attachment.setFilePath(w);
            return attachment;
        }).toList()).ifPresent(this::setTbBulletinAttachmentList);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public Collection<Integer> getMultiPlatform() {
        return getPlatform();
    }

    public abstract List<Integer> getPlatform();
}
