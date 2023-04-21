package cn.shmedo.monitor.monibotbaseapi.util.formula;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 原始公式
 *
 * @author Chengfs on 2023/4/18
 */
@Data
public class Origin implements Serializable {

    /**
     * 公式类型
     */
    private Type type;

    /**
     * 第一个标识
     */
    private String firstToken;

    /**
     * 第二个标识
     */
    private String secondToken;

    /**
     * 参数
     */
    private Map<String, Object> params;

    public static Origin parse(String origin) {
        Origin result = new Origin();

        //按冒号切分 依次为 类型 标识 参数
//        history:self.X:endDate=2022-09-01 00:00:00&beginDate=2022-08-27 00:00:00
        String[] parts = origin.split(StrUtil.COLON);
        Assert.isTrue(parts.length > 1, "公式{}格式错误", origin);

        result.type = Type.valueOf(parts[0].toUpperCase());
        Assert.notNull(result.type, "公式{}类型{}错误", origin, parts[0]);

        if (!Type.CONS.equals(result.type)) {
            result.firstToken = StrUtil.subBefore(parts[1], StrUtil.DOT, false);
            result.secondToken = StrUtil.subAfter(parts[1], StrUtil.DOT, false);
        } else {
            result.firstToken = parts[1];
        }

        if (parts.length > 2) {
            String last =  ArrayUtil.join(ArrayUtil.sub(parts, 2, parts.length), StrUtil.COLON);
            result.params = StrUtil.split(last, Constant.AND).stream()
                    .map(e -> e.split(Constant.EQUAL))
                    .map(e -> MapUtil.entry(e[0], e[1]))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            result.params = Collections.emptyMap();
        }
        return result;
    }

    public enum Type implements Serializable {

        //iot-物模型数据源类型、mon-监测传感器数据类型、slef-自身数据、history-自身历史数据、cons-常量、param-参数、ex-扩展配置
        IOT, MON, SELF, HISTORY, CONS, PARAM, EX;
    }

    public static final Long serialVersionUID = 1L;
}