package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 公式计算工具类
 *
 * @author Chengfs on 2023/3/24
 */
@Slf4j
public class FormulaUtil {

    private static final String FORMULA_PREFIX = "${";
    private static final String FORMULA_SUFFIX = "}";
    private static final String LEFT_BRACKETS = "[";
    private static final String RIGHT_BRACKETS = "]";
    private static final String AND = "&";
    private static final String EQUAL = "=";
    private static final String BEGIN_DATE = "beginDate";
    private static final String END_DATE = "endDate";
    private static final String OBJECT = "Object";
    private static final String ARRAY = "Array";

    public enum DataType {

        //iot-物模型数据源类型、mon-监测传感器数据类型、slef-自身数据、history-自身历史数据、cons-常量、param-参数、ex-扩展配置
        IOT, MON, SELF, HISTORY, CONS, PARAM, EX;

    }

    public static Map<DataType, Set<Source>> parse(String formula) {
        return Arrays.stream(StrUtil.subBetweenAll(formula, FORMULA_PREFIX, FORMULA_SUFFIX))
                .map(variable -> {
                    String head = StrUtil.subBefore(variable, StrUtil.COLON, false);
                    String tail = StrUtil.subAfter(variable, StrUtil.COLON, false);

                    String token = StrUtil.subBefore(tail, StrUtil.DOT, false);
                    String field = StrUtil.subAfter(tail, StrUtil.DOT, false);

                    DataType dataType = DataType.valueOf(head.toUpperCase());
                    Source source = new Source();
                    source.setOrigin(StrUtil.concat(true, FORMULA_PREFIX, variable, FORMULA_SUFFIX));
                    switch (dataType) {
                        case IOT: //iot:201_a.Temp、iot:201_a.objField[aField]、iot:201_a.arrayField[0]
                            source.setSourceToken(token);
                            String childToken = StrUtil.subBetween(field, LEFT_BRACKETS, RIGHT_BRACKETS);
                            if (childToken != null) {
                                source.setFieldToken(StrUtil.subBefore(field, LEFT_BRACKETS, false));
                                source.setFieldChildToken(childToken);
                            } else {
                                source.setFieldToken(field);
                            }
                            break;

                        case MON: //mon:22_a.X、mon:22_a.X:endDate=xxx&beginDate=yyy
                        case HISTORY: //history:self.X:endDate=2022-09-01 00:00:00&beginDate=2022-08-27 00:00:00
                            if (dataType == DataType.MON) {
                                source.setSourceToken(token);
                            }
                            source.setFieldToken(StrUtil.subBefore(field, StrUtil.COLON, false));
                            String paramStr = StrUtil.subAfter(field, StrUtil.COLON, false);
                            List<String> paramList = StrUtil.split(paramStr, AND);
                            if (paramList.size() > 1) {
                                Map<String, String> dataMap = paramList.stream().map(e -> {
                                    List<String> split = StrUtil.split(e, EQUAL);
                                    return MapUtil.entry(split.get(0), split.get(1));
                                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                source.setBeginDate(MapUtil.getDate(dataMap, BEGIN_DATE));
                                source.setEndDate(MapUtil.getDate(dataMap, END_DATE));
                            }
                            break;

                        case SELF: //self:self.X
                        case EX: //ex:ex.baseType
                            source.setFieldToken(field);
                            break;
                        case CONS: //cons:3.1416926
                            source.setFieldValue(tail);
                            break;
                        case PARAM: //param:area
                            source.setFieldToken(tail);
                            break;

                        default:
                            return null;
                    }
                    return MapUtil.entry(dataType, source);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));

    }

    public static Object calculate(String formula, Map<DataType, Set<Source>> map) {

        map.values().forEach(sources -> sources.forEach(source -> {
            if (source.getFieldValue() == null) {
                throw new RuntimeException(StrUtil.format("参数: {} 未设值", source.getOrigin()));
            }
        }));

        log.debug("原始公式：{}", formula);
        Map<String, Object> valueMap = map.values().stream()
                .flatMap(sources -> sources.stream()
                        .map(e -> MapUtil.entry(e.getOrigin(), e.getFieldValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            formula = formula.replace(entry.getKey(), entry.getValue().toString());
        }
        //调用mathParser进行计算
        log.debug("计算公式：{}", formula);
        Expression e = new Expression(formula);
//        Assert.isTrue(e.checkLexSyntax(), "公式语法错误");
        return e.calculate();
    }

    public static Object calculate(String formula, Consumer<Map<DataType, Set<Source>>> consumer) {
        Map<DataType, Set<Source>> dataMap = parse(formula);
        consumer.accept(dataMap);
        return calculate(formula, dataMap);
    }

    @Data
    @EqualsAndHashCode
    public static class Source {

        /**
         * 原始参数<br/>
         * 格式: {@code ${dataType:subject.field:constraint}}<br/>
         * 示例: {@code ${history:self.X:endDate=2022-09-01 00:00:00&beginDate=2022-08-27 00:00:00}}
         */
        private String origin;

        /**
         * 数据源代号
         */
        private String sourceToken;

        /**
         * 字段名称
         */
        private String fieldToken;

        /**
         * 子字段名称, 字段为Object时为字段名，为Array型时为索引值
         */
        private String fieldChildToken;

        /**
         * 字段值
         */
        private Object fieldValue;

        /**
         * 开始时间
         */
        private Date beginDate;

        /**
         * 结束时间
         */
        private Date endDate;
        
    }
}

    
    