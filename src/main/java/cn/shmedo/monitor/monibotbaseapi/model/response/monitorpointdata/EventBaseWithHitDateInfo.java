package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-05-11 16:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EventBaseWithHitDateInfo extends EventBaseInfo {
    // 命中时间段array,一个大事记可以有多个相交的的时间段,
    private String hintTimeRange;

    public static EventBaseWithHitDateInfo build(EventBaseInfo info, String hintTimeRange) {
        EventBaseWithHitDateInfo result = new EventBaseWithHitDateInfo();
        BeanUtil.copyProperties(info, result);
        result.setHintTimeRange(hintTimeRange);
        return result;
    }
}
