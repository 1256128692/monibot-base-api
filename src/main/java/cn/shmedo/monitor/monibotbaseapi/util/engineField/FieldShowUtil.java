package cn.shmedo.monitor.monibotbaseapi.util.engineField;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IFieldName;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IFieldToken;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IMonitorType;

import java.util.*;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-21 10:39
 */
public final class FieldShowUtil {
    private static final Map<String, String> SPECIAL_TYPE =
            Map.of("dailyRainfall", "日降雨量", "waterQuality", "水质等级");

    public static boolean belongSpecialType(String source) {
        String trim = Optional.ofNullable(source).map(String::trim).orElse("");
        return SPECIAL_TYPE.containsKey(trim);
    }

    public static boolean notBelongSpecialType(String source) {
        return !belongSpecialType(source);
    }

    /**
     * field特殊处理
     *
     * @param source
     * @param <T>
     * @see cn.shmedo.monitor.monibotbaseapi.service.impl.WtMonitorServiceImpl
     */
    public static <T> T dealFieldShow(T source) {
        //无token,添加,若需要添加名字也添加
        //有token但需要name,添加
        if (source instanceof IFieldToken && source instanceof IMonitorType) {
            String fieldToken = ((IFieldToken) source).getFieldToken();
            Optional.ofNullable(((IMonitorType) source).getMonitorType()).ifPresent(u -> {
                Tuple<String, String> tuple = getSpecialTokenAndName(u);
                if (Objects.isNull(fieldToken)) {
                    ((IFieldToken) source).setFieldToken(tuple.getItem1());
                    if (source instanceof IFieldName) {
                        ((IFieldName) source).setFieldName(tuple.getItem2());
                    }
                } else if (belongSpecialType(fieldToken) && source instanceof IFieldName &&
                        Objects.isNull(((IFieldName) source).getFieldName())) {
                    ((IFieldName) source).setFieldName(tuple.getItem2());
                }
            });
        }
        return source;
    }

    private static Tuple<String, String> getSpecialTokenAndName(Integer monitorType) {
        assert Objects.nonNull(monitorType);
        if (monitorType == MonitorType.WATER_QUALITY.getKey()) {
            return new Tuple<>("waterQuality", SPECIAL_TYPE.get("waterQuality"));
        }
        if (monitorType == MonitorType.RAINFALL.getKey()) {
            return new Tuple<>("dailyRainfall", SPECIAL_TYPE.get("dailyRainfall"));
        }
        return new Tuple<>();
    }
}
