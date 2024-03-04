package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbBulletinAttachmentMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbBulletinPlatformRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinAttachment;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinPlatformRelation;
import cn.shmedo.monitor.monibotbaseapi.model.enums.BulletinPublishStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-04 13:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteBulletinDataBatchParam extends BaseHandleBulletinDataBatchParam {
    @JsonIgnore
    private List<Integer> attachmentIDList;
    @JsonIgnore
    private List<Integer> platformRelateIDList;

    @Override
    public ResultWrapper<?> validate() {
        super.validate();
        final List<TbBulletinData> tbBulletinDataList = getTbBulletinDataList();
        final List<Integer> bulletinIDList = getBulletinIDList();
        if (bulletinIDList.size() == 1 && tbBulletinDataList.size() == 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要删除的公告不存在");
        }
        if (tbBulletinDataList.stream().anyMatch(u -> BulletinPublishStatus.PUBLISHED.getCode().equals(u.getStatus()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "发布的公告必须撤销后才能删除");
        }
        this.attachmentIDList = ContextHolder.getBean(TbBulletinAttachmentMapper.class).selectList(
                        new LambdaQueryWrapper<TbBulletinAttachment>().in(TbBulletinAttachment::getBulletinID, bulletinIDList))
                .stream().map(TbBulletinAttachment::getId).toList();
        this.platformRelateIDList = ContextHolder.getBean(TbBulletinPlatformRelationMapper.class).selectList(
                        new LambdaQueryWrapper<TbBulletinPlatformRelation>().in(TbBulletinPlatformRelation::getBulletinID, bulletinIDList))
                .stream().map(TbBulletinPlatformRelation::getId).toList();
        return null;
    }
}
