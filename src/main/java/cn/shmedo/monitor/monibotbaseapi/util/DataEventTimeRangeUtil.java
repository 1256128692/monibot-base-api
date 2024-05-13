package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.CommonBeans;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-05-11 13:20
 */
@Slf4j
public class DataEventTimeRangeUtil {
    private static ObjectMapper objectMapper = null;

    private static ObjectMapper getObjectMapper() {
        return objectMapper = Objects.nonNull(objectMapper) ? objectMapper :
                SpringUtil.getBean(CommonBeans.CAMEL_OBJECT_MAPPER, ObjectMapper.class);
    }

    /**
     * 根据数据时间解析时间范围
     * <pre>
     * 如果频率{@code frequency}等于1时表示每年这个时间段都是大事记的时间段,此时的时间范围就需要忽略掉年份;
     * 对于单个数据而言,展示给前端页面的也应该是这个数据那年的大事记的时间段,这里在解析时就已经转化了
     * </pre>
     *
     * @param dataTime  数据时间,不允许为{@code null}
     * @param frequency 频率
     * @return item1-startTime,item2-endTime
     */
    public static List<Tuple<Date, Date>> parse(String timeRange, Date dataTime, Integer frequency) {
        try {
            int year = DateUtil.date(dataTime).getField(DateField.YEAR);
            return JSONUtil.parseArray(timeRange).toList(TimeRange.class).stream().map(item ->
                    new Tuple<>(adjustTimeByDataTimeFrequency(item.startTime, year, frequency),
                            adjustTimeByDataTimeFrequency(item.endTime, year, frequency))).collect(Collectors.toList());
        } catch (JSONException e) {
            log.error("parse timeRange failed,timeRange: {}", timeRange);
            return null;
        }
    }

    public static @Nullable String parse(List<Tuple<Date, Date>> timeRange) {
        return Optional.ofNullable(timeRange).map(list -> list.stream().map(item ->
                new TimeRange(item.getItem1(), item.getItem2())).collect(Collectors.toList())).map(list -> {
            try {
                return getObjectMapper().writeValueAsString(list);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
    }

    /**
     * 筛选出{@code tupleList}中命中目标时间范围的子集,目标时间{@code time}为{@code null}时返回空集
     *
     * @param time     目标时间
     * @param dateList 根据目标时间处理后的时间范围。
     * @see #parse(String, Date, Integer)
     */
    public static List<Tuple<Date, Date>> findHintTimeInRange(Date time, List<Tuple<Date, Date>> dateList) {
        return Optional.ofNullable(time).flatMap(t -> Optional.ofNullable(dateList).map(list ->
                list.stream().map(tuple -> Optional.ofNullable(tuple.getItem1()).map(startTime ->
                        startTime.before(time)).orElse(true) && Optional.ofNullable(tuple.getItem2()).map(endTime ->
                        endTime.after(time)).orElse(true) ? tuple : null).filter(Objects::nonNull).toList())).orElse(List.of());
    }

    private static Date adjustTimeByDataTimeFrequency(Date time, int year, Integer frequency) {
        return Optional.ofNullable(time).map(t -> Optional.ofNullable(frequency).map(f -> f == 1).orElse(false) ?
                adjustTimeToDataTimeYear(t, year) : t).orElse(null);
    }

    private static Date adjustTimeToDataTimeYear(Date time, int year) {
        return DateUtil.date(time).setField(DateField.YEAR, year);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private final static class TimeRange {
        private Date startTime;
        private Date endTime;
    }
}
