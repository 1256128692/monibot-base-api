package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.enums.BulletinPublishStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-28 17:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddBulletinDataParam extends BaseBulletinData {
    @NotEmpty(message = "请至少选择一个平台")
    private List<Integer> platform;
    @Range(max = 1, message = "发布状态 0.未发布(仅保存); 1.已发布(保存并发布)")
    private Integer status;
    private Boolean topMost;
    @NotNull(message = "请选择公告类型")
    @Range(max = 3, message = "公告类型 0.其他 1.行业政策 2.重要新闻 3.工作公告")
    private Integer type;
    @NotEmpty(message = "请输入公告标题")
    @Size(max = 100, message = "公告标题限制100个字符")
    private String name;
    @NotEmpty(message = "请输入公告内容")
    private String content;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        final Date current = getCurrent();
        this.status = Objects.isNull(this.status) ? 0 : this.status;
        this.topMost = Objects.nonNull(this.topMost) && this.topMost;
        if (this.topMost && BulletinPublishStatus.UNPUBLISHED.getCode().equals(this.status)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未发布的公告无法置顶");
        }
        setTbBulletinData(new TbBulletinData(null, getCompanyID(), type, name, content, null, status, topMost, current, null, current, null));
        return null;
    }
}
