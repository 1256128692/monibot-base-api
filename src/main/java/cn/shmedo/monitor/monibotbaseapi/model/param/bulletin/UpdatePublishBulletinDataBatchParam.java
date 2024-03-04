package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.enums.BulletinPublishStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-04 13:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdatePublishBulletinDataBatchParam extends BaseHandleBulletinDataBatchParam {
    @NotNull(message = "发布状态不能为空")
    @Range(max = 2, message = "发布状态 0.已发布公告撤销; 1.未发布公告发布")
    private Integer status;

    @Override
    public ResultWrapper<?> validate() {
        super.validate();
        final Date current = new Date();
        final List<TbBulletinData> tbBulletinDataList = getTbBulletinDataList();
        final List<Integer> bulletinIDList = getBulletinIDList();
        boolean isPublished = BulletinPublishStatus.PUBLISHED.getCode().equals(status);   //是否是发布动作
        if (bulletinIDList.size() == 1 && tbBulletinDataList.size() == 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要" + (isPublished ? "发布" : "撤销") + "的公告不存在");
        }
        if (tbBulletinDataList.stream().anyMatch(u -> u.getStatus().equals(status))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要" + (isPublished ? "发布" : "撤销") +
                    "的公告已被" + (isPublished ? "发布" : "撤销"));
        }
        tbBulletinDataList.forEach(u -> {
            if (isPublished) {
                u.setPublishTime(current);
            } else {
                // 撤销时取消置顶
                u.setTopMost(false);
                u.setTopMostTime(null);
                u.setPublishTime(null);
            }
            u.setStatus(status);
            u.setUpdateTime(current);
        });
        return null;
    }
}
