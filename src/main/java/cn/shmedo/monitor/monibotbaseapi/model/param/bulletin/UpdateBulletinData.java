package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbBulletinDataMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 10:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateBulletinData extends BaseBulletinData {
    @NotNull(message = "公告ID不能为空")
    @Positive(message = "公告ID不能小于1")
    private Integer bulletinID;
    private List<Integer> platform;
    @Range(max = 1, message = "发布状态 0.未发布(仅保存); 1.已发布(保存并发布)")
    private Integer status;
    @Range(max = 3, message = "公告类型 0.其他 1.行业政策 2.重要新闻 3.工作公告")
    private Integer type;
    private Boolean topMost;
    private String name;
    private String content;

    @Override
    public ResultWrapper<?> validate() {
        List<TbBulletinData> tbBulletinDataList = ContextHolder.getBean(TbBulletinDataMapper.class).selectList(
                new LambdaQueryWrapper<TbBulletinData>().eq(TbBulletinData::getCompanyID, getCompanyID())
                        .eq(TbBulletinData::getId, bulletinID));
        if (CollUtil.isEmpty(tbBulletinDataList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "公告不存在");
        }
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        TbBulletinData tbBulletinData = tbBulletinDataList.stream().findAny().orElseThrow();
        if (tbBulletinData.getStatus() == 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "已发布的公告需要先撤销后才能编辑");
        }
        if (Optional.ofNullable(this.topMost).orElse(false) && (Objects.isNull(this.status) || this.status == 0)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未发布的公告无法置顶");
        }
        Optional.ofNullable(status).ifPresent(tbBulletinData::setStatus);
        Optional.ofNullable(type).ifPresent(tbBulletinData::setType);
        Optional.ofNullable(topMost).ifPresent(tbBulletinData::setTopMost);
        Optional.ofNullable(name).filter(StrUtil::isNotEmpty).ifPresent(tbBulletinData::setName);
        Optional.ofNullable(content).filter(StrUtil::isNotEmpty).ifPresent(tbBulletinData::setContent);
        tbBulletinData.setUpdateTime(getCurrent());
        setTbBulletinData(tbBulletinData);
        Optional.ofNullable(getPlatformRelationList()).filter(CollUtil::isNotEmpty).ifPresent(u ->
                u.forEach(w -> w.setBulletinID(bulletinID)));
        return null;
    }
}
