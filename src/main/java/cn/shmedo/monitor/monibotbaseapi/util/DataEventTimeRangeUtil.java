package cn.shmedo.monitor.monibotbaseapi.util;

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
     * @return item1-startTime,item2-endTime
     */
    public static List<Tuple<Date, Date>> parse(String timeRange) {
        try {
            return JSONUtil.parseArray(timeRange).toList(TimeRange.class).stream().map(item ->
                    new Tuple<>(item.startTime, item.endTime)).collect(Collectors.toList());
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
     * @param time      目标时间
     * @param tupleList item1-startTime,item2-endTime,要求至少其一不为{@code null}
     */
    public static List<Tuple<Date, Date>> findHintTimeInRange(Date time, List<Tuple<Date, Date>> tupleList) {
        return Objects.isNull(time) ? List.of() : tupleList.stream().map(tuple ->
                Optional.ofNullable(tuple.getItem1()).map(startTime -> startTime.before(time)).orElse(true) &&
                        Optional.ofNullable(tuple.getItem2()).map(endTime -> endTime.after(time)).orElse(true) ?
                        tuple : null).filter(Objects::nonNull).toList();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private final static class TimeRange {
        private Date startTime;
        private Date endTime;
    }
}
