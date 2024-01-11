package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnBaseConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnLevelType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLevelStyle;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnTag;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 11:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateWarnBaseConfigParam extends CompanyPlatformParam {
    @Range(min = 1, max = 3, message = "报警标签枚举 1.报警 2.告警 3.预警")
    private Integer warnTag;
    @Range(min = 1, max = 2, message = "报警等级类型枚举 1: 4级 2: 3级")
    private Integer warnLevelType;
    @Range(min = 1, max = 3, message = "等级样式枚举 1: 红,橙,黄,蓝 2: 1,2,3,4级 3: Ⅰ,Ⅱ,Ⅲ,Ⅳ级")
    private Integer warnLevelStyle;
    @JsonIgnore
    private TbWarnBaseConfig tbWarnBaseConfig;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        Integer companyID = getCompanyID();
        Integer platform = getPlatform();
        if (Objects.nonNull(warnLevelType)) {
            if (ContextHolder.getBean(TbWarnThresholdConfigMapper.class).exists(new LambdaQueryWrapper<TbWarnThresholdConfig>()
                    .eq(TbWarnThresholdConfig::getCompanyID, companyID).eq(TbWarnThresholdConfig::getPlatform, platform))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该平台已有阈值配置,不允许修改报警等级类型");
            }
            if (ContextHolder.getBean(TbDataWarnLogMapper.class).exists(new LambdaQueryWrapper<TbDataWarnLog>()
                    .eq(TbDataWarnLog::getCompanyID, companyID).eq(TbDataWarnLog::getPlatform, platform))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该平台已产生过报警,不允许修改报警等级类型");
            }
            if (ContextHolder.getBean(TbWarnNotifyConfigMapper.class).exists(new LambdaQueryWrapper<TbWarnNotifyConfig>()
                    .eq(TbWarnNotifyConfig::getCompanyID, companyID).eq(TbWarnNotifyConfig::getPlatform, platform))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该平台已配置过数据报警通知,不允许修改报警等级类型");
            }
        }
        Integer userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        // Set time here to avoid using database default timezone.
        Date current = new Date();
        List<TbWarnBaseConfig> list = ContextHolder.getBean(TbWarnBaseConfigMapper.class).selectList(new LambdaQueryWrapper<TbWarnBaseConfig>()
                .eq(TbWarnBaseConfig::getCompanyID, companyID).eq(TbWarnBaseConfig::getPlatform, platform));
        tbWarnBaseConfig = list.stream().findAny().orElse(new TbWarnBaseConfig(null, companyID, platform,
                WarnTag.TYPE1.getCode(), DataWarnLevelType.FOUR_LEVEL.getCode(), WarnLevelStyle.COLOR.getCode(),
                userID, current, null, null));
        tbWarnBaseConfig.setUpdateUserID(userID);
        tbWarnBaseConfig.setUpdateTime(current);
        Optional.ofNullable(warnTag).ifPresent(tbWarnBaseConfig::setWarnTag);
        Optional.ofNullable(warnLevelType).ifPresent(tbWarnBaseConfig::setWarnLevelType);
        Optional.ofNullable(warnLevelStyle).ifPresent(tbWarnBaseConfig::setWarnLevelStyle);
        return null;
    }
}
