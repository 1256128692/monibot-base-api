package cn.shmedo.monitor.monibotbaseapi.util.engineField;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CompareInterval;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtTriggerActionInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-23 11:36
 */
@Slf4j
public class CompareIntervalDescUtil {
    private static final String LEFT_NOT_CONTAINS_END_POINT = "(";
    private static final String LEFT_CONTAINS_END_POINT = "[";
    private static final String RIGHT_NOT_CONTAINS_END_POINT = ")";
    private static final String RIGHT_CONTAINS_END_POINT = "]";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Map<Integer, String> getCompareRuleDescMap(Integer monitorTypeID, List<WtTriggerActionInfo> warnList) {
        Map<Integer, String> map = new HashMap<>();
        Map<Integer, Integer> warnIDlimitTypeMap = new HashMap<>();
        Map<Integer, String> warnIDUpperNameMap = new HashMap<>();
        HashMap<Integer, String> warnIDUnitMap = new HashMap<>();
        List<Tuple<Integer, Double>> tuples101 = new ArrayList<>();
        List<Tuple<Integer, Double>> tuples000 = new ArrayList<>();
        List<Tuple<Integer, Double>> tuples001 = new ArrayList<>();
        warnList.stream().filter(u -> Objects.nonNull(u.getCompareRule())).peek(u -> {
            Integer warnID = u.getWarnID();
            JSONObject compareRule = JSONUtil.parseObj(u.getCompareRule());
            String upperName = Optional.ofNullable(compareRule.getStr("upperName")).map(String::trim).orElse(null);
            String unit = compareRule.getStr("unit");
            Object limit = compareRule.get("upperLimit");
            warnIDlimitTypeMap.put(warnID, Optional.ofNullable(compareRule.getInt("limitType")).orElse(0)); //是否包含端点
            Double upperLimit = null;
            if (limit instanceof String) {
                upperLimit = Double.parseDouble((String) limit);
            } else if (limit instanceof Number) {
                upperLimit = ((Number) limit).doubleValue();
            }
            Optional.ofNullable(upperLimit).ifPresent(
                    l -> Optional.ofNullable(CompareInterval.getValue(monitorTypeID, u.getFieldToken(), upperName))
                            .ifPresent(w -> {
                                warnIDUnitMap.put(warnID, unit);
                                if (CompareInterval.notSpecialConcat(upperName)) {
                                    warnIDUpperNameMap.put(warnID, upperName);
                                }
                                switch (w.getShowType()) {
                                    case -1 -> tuples101.add(new Tuple<>(warnID, l));
                                    case 0 -> tuples000.add(new Tuple<>(warnID, l));
                                    case 1 -> tuples001.add(new Tuple<>(warnID, l));
                                    default ->
                                            log.error("CompareInterval参数存在未处理的showType,name:{},\tshowType:{}",
                                                    w.name(), w.getShowType());
                                }
                            }));
        }).collect(Collectors.toList());
        dealLowerLimitDesc(tuples101, map, warnIDlimitTypeMap, warnIDUpperNameMap, warnIDUnitMap, "(-∞,");
        dealLowerLimitDesc(tuples000, map, warnIDlimitTypeMap, warnIDUpperNameMap, warnIDUnitMap, "[0,");
        dealUpperLimitDesc(tuples001, map, warnIDlimitTypeMap, warnIDUpperNameMap, warnIDUnitMap);
        return map;
    }

    private static void dealUpperLimitDesc(List<Tuple<Integer, Double>> tupleList, Map<Integer, String> map,
                                           Map<Integer, Integer> warnIDlimitTypeMap,
                                           Map<Integer, String> warnIDUpperNameMap, Map<Integer, String> warnIDUnitMap) {
        if (tupleList.size() == 1) {
            Tuple<Integer, Double> tuple = tupleList.get(0);
            Integer tupleItem1 = tuple.getItem1();
            map.put(tupleItem1, (warnIDlimitTypeMap.getOrDefault(tupleItem1, 0) == 1 ?
                    LEFT_CONTAINS_END_POINT : LEFT_NOT_CONTAINS_END_POINT) +
                    (Optional.ofNullable(warnIDUpperNameMap.get(tupleItem1)).map(u -> u + ": ").orElse("")) +
                    tuple.getItem2() + warnIDUnitMap.get(tupleItem1) + ",+∞)");
            return;
        }
        Optional.of(tupleList).filter(u -> !CollectionUtil.isNullOrEmpty(u)).ifPresent(u -> {
            u.sort((o1, o2) -> (int) (o1.getItem2() - o2.getItem2()));
            Tuple<Integer, Double> tailTuple = u.get(u.size() - 1);
            Integer tailTupleItem1 = tailTuple.getItem1();
            map.put(tailTupleItem1, (warnIDlimitTypeMap.getOrDefault(tailTupleItem1, 0) == 1 ?
                    LEFT_CONTAINS_END_POINT : LEFT_NOT_CONTAINS_END_POINT) +
                    (Optional.ofNullable(warnIDUpperNameMap.get(tailTupleItem1)).map(w -> w + ": ").orElse("")) +
                    tailTuple.getItem2() + warnIDUnitMap.get(tailTupleItem1) + ",+∞)");
            u.stream().reduce((o1, o2) -> {
                Integer o1Item1 = o1.getItem1();
                Integer o2Item1 = o2.getItem1();
                map.put(o1Item1, (warnIDlimitTypeMap.getOrDefault(o1Item1, 0) == 1 ?
                        LEFT_CONTAINS_END_POINT : LEFT_NOT_CONTAINS_END_POINT) +
                        (Optional.ofNullable(warnIDUpperNameMap.get(o1Item1)).map(w -> w + ": ").orElse("")) +
                        o1.getItem2() + warnIDUnitMap.get(o1Item1) + "," +
                        (Optional.ofNullable(warnIDUpperNameMap.get(o2Item1)).map(w -> w + ": ").orElse("")) +
                        o2.getItem2() + warnIDUnitMap.get(o2Item1) + (warnIDlimitTypeMap.getOrDefault(o2Item1, 0) == 1 ?
                        RIGHT_NOT_CONTAINS_END_POINT : RIGHT_CONTAINS_END_POINT));
                return o2;
            });
        });
    }

    private static void dealLowerLimitDesc(List<Tuple<Integer, Double>> tupleList, Map<Integer, String> resMap,
                                           Map<Integer, Integer> warnIDlimitTypeMap,
                                           Map<Integer, String> warnIDUpperNameMap, Map<Integer, String> warnIDUnitMap,
                                           String headDesc) {
        if (tupleList.size() == 1) {
            Tuple<Integer, Double> tuple = tupleList.get(0);
            Integer tupleItem1 = tuple.getItem1();
            resMap.put(tupleItem1, headDesc +
                    (Optional.ofNullable(warnIDUpperNameMap.get(tupleItem1)).map(w -> w + ": ").orElse("")) +
                    tuple.getItem2() + warnIDUnitMap.get(tupleItem1) +
                    (warnIDlimitTypeMap.getOrDefault(tupleItem1, 0) == 1 ?
                    RIGHT_CONTAINS_END_POINT : RIGHT_NOT_CONTAINS_END_POINT));
            return;
        }
        Optional.of(tupleList).filter(u -> !CollectionUtil.isNullOrEmpty(u)).ifPresent(u -> {
            u.sort((o1, o2) -> (int) (o1.getItem2() - o2.getItem2()));
            Tuple<Integer, Double> headTuple = u.get(0);
            Integer headTupleItem1 = headTuple.getItem1();
            resMap.put(headTupleItem1, headDesc +
                    (Optional.ofNullable(warnIDUpperNameMap.get(headTupleItem1)).map(w -> w + ": ").orElse("")) +
                    headTuple.getItem2() + warnIDUnitMap.get(headTupleItem1) +
                    (warnIDlimitTypeMap.getOrDefault(headTupleItem1, 0) == 1 ?
                            RIGHT_CONTAINS_END_POINT : RIGHT_NOT_CONTAINS_END_POINT));
            u.stream().reduce((o1, o2) -> {
                Integer o1Item1 = o1.getItem1();
                Integer o2Item1 = o2.getItem1();
                resMap.put(o2Item1,
                        (warnIDlimitTypeMap.getOrDefault(o1Item1, 0) == 1 ?
                                LEFT_NOT_CONTAINS_END_POINT : LEFT_CONTAINS_END_POINT) +
                                (Optional.ofNullable(warnIDUpperNameMap.get(o1Item1)).map(w -> w + ": ").orElse("")) +
                                o1.getItem2() + warnIDUnitMap.get(o1Item1) + "," +
                                (Optional.ofNullable(warnIDUpperNameMap.get(o2Item1)).map(w -> w + ": ").orElse("")) +
                                o2.getItem2() + warnIDUnitMap.get(o2Item1) +
                                (warnIDlimitTypeMap.getOrDefault(o2Item1, 0) == 1 ?
                                        RIGHT_CONTAINS_END_POINT : RIGHT_NOT_CONTAINS_END_POINT));
                return o2;
            });
        });
    }
}
