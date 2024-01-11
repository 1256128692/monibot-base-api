package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.shmedo.iot.entity.api.ResultWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;

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

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        if (Objects.nonNull(warnLevelType)) {
            //TODO 如果有'阈值配置'或者'已经产生警报'或者'数据报警通知配置',该项不允许NotNull
        }
        return null;
    }
}
