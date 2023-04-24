package cn.shmedo.monitor.monibotbaseapi.util.formula;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 公式计算工具类
 *
 * @author Chengfs on 2023/4/18
 */
@Slf4j
public class FormulaUtil {

    /**
     * 解析表达式
     *
     * @param item 具体表达式，如 {@code self:self.time:format=unixMilli}
     * @return 解析结果 {@link FormulaData}
     */
    private static FormulaData parseItem(@Nonnull String item) {
        Origin origin = Origin.parse(item);
        FormulaData result = new FormulaData();
        result.setOrigin(StrUtil.concat(true,
                Constant.FORMULA_PREFIX, item, Constant.FORMULA_SUFFIX));
        result.setType(origin.getType());
        result.setProvide(FormulaData.Provide.DATA);

        switch (origin.getType()) {
            case IOT:
                result.setSourceToken(origin.getFirstToken());
                String childToken = StrUtil.subBetween(origin.getSecondToken(),
                        Constant.LEFT_BRACKETS, Constant.RIGHT_BRACKETS);
                if (childToken != null) {
                    result.setFieldToken(StrUtil.subBefore(origin.getSecondToken(),
                            Constant.LEFT_BRACKETS, false));
                    result.setFieldChildToken(childToken);
                } else {
                    result.setFieldToken(origin.getSecondToken());
                }
                Optional.ofNullable(MapUtil.getStr(origin.getParams(), Constant.FORMAT))
                        .ifPresent(s -> result.setProvide(FormulaData.Provide.valueOf(s.toUpperCase())));
                break;
            case MON:
                result.setSourceToken(origin.getFirstToken());
            case HISTORY:
                result.setFieldToken(origin.getSecondToken());
                Optional.ofNullable(MapUtil.getStr(origin.getParams(), Constant.FORMAT))
                        .ifPresent(s -> result.setProvide(FormulaData.Provide.valueOf(s.toUpperCase())));
                Optional.ofNullable(MapUtil.getStr(origin.getParams(), Constant.END_DATE))
                        .filter(StrUtil::isNotBlank)
                        .ifPresent(s -> result.setEndDate(Constant.SELF_TIME.equals(s) ? -1 : DateUtil.parseDate(s).getTime()));
                Optional.ofNullable(MapUtil.getStr(origin.getParams(), Constant.BEGIN_DATE))
                        .filter(StrUtil::isNotBlank)
                        .ifPresent(s -> result.setBeginDate(DateUtil.parseDate(s).getTime()));
                break;
            case SELF:
                Optional.ofNullable(MapUtil.getStr(origin.getParams(), Constant.FORMAT))
                        .ifPresent(s -> result.setProvide(FormulaData.Provide.valueOf(s.toUpperCase())));
            case EX:
                result.setFieldToken(origin.getSecondToken());
                break;
            case PARAM:
                result.setFieldToken(origin.getFirstToken());
                break;
            case CONS:
                result.setFieldValue(Convert.toDouble(origin.getFirstToken()));
                break;
        }
        return result;
    }

    /**
     * 解析公式
     *
     * @param formula 原始公式 形如: {@code ${self:self.time:format=unixMilli}+${self:self.time:format=unixMilli}}
     * @return 解析结果 {@link Collection<FormulaData>}
     */
    public static Collection<FormulaData> parse(String formula) {
        return Arrays.stream(StrUtil.subBetweenAll(formula, Constant.FORMULA_PREFIX, Constant.FORMULA_SUFFIX))
                .map(FormulaUtil::parseItem).collect(Collectors.toSet());
    }

    /**
     * 计算公式
     *
     * @param formula    公式
     * @param collection 公式中的参数
     * @return 计算结果
     */
    public static Double calculate(String formula, Collection<FormulaData> collection) {
        final String originFormula = formula;
        //检查参数是否设值
        collection.stream().filter(e -> e.getFieldValue() == null).findFirst().ifPresent(e -> {
            throw new IllegalArgumentException(StrUtil.format("参数 {} 未设值", e.getOrigin()));
        });

        for (FormulaData data : collection) {
            formula = formula.replace(data.getOrigin(), data.getFieldValue().toString());
        }
        //调用mathParser进行计算
        Expression e = new Expression(formula);
        double result = e.calculate();
        Assert.isFalse(Double.isNaN(result), "公式错误: {} => {} = {}", originFormula, formula, result);
        return result;
    }

    /**
     * 解析并计算公式
     *
     * @param formula  公式
     * @param consumer 参数值提供器
     * @return 计算结果
     */
    public static Double calculate(String formula, Consumer<Map<Origin.Type, List<FormulaData>>> consumer) {
        Collection<FormulaData> data = parse(formula);
        Map<Origin.Type, List<FormulaData>> typeMap = data.stream().collect(Collectors.groupingBy(FormulaData::getType));
        consumer.accept(typeMap);
        return calculate(formula, data);
    }

}