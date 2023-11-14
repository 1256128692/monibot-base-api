package cn.shmedo.monitor.monibotbaseapi.model.response.warn;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @author Chengfs on 2023/10/20
 */
@Data
public class WtWarnListResult {

    private Map<Integer, Long> statistic;
    private List<? extends WtWarnLogBase> list;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Item extends WtWarnLogBase {
        //暴雨预警 warnType: 6
        //预警发布单位
        private String issueUnit;
        //预警区域行政区划码
        private Long warnAreaCode;
        //预警区域名称
        private String warnArea;
        //预警区域经纬度
        private String warnAreaLocation;

        public static Item of(WtWarnLogBase base) {
            Item item = new Item();
            BeanUtil.copyProperties(base, item);
            return item;
        }
    }
}