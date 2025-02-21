package cn.shmedo.monitor.monibotbaseapi.util.formula;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 解析后的公式
 *
 * @author Chengfs on 2023/4/18
 */
@lombok.Data
@EqualsAndHashCode
public class FormulaData implements Serializable {

    /**
     * 数据类型 {@link Origin.Type}
     */
    private Origin.Type type;

    /**
     * 需要的fieldValue 类型 {@link Provide}
     */
    private Provide provide;

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
    private BigDecimal fieldValue;

    /**
     * 开始时间
     */
    private long beginDate;

    /**
     * 结束时间
     */
    private long endDate;

    /**
     * 设置字段值，数据类型直接设置，时间戳类型单位为毫秒
     *
     * @param fieldValue 字段值
     */
    public void setFieldValue(Double fieldValue) {
        if (fieldValue != null) {
            switch (provide) {
                case DATA:
                case UNIXMILLI:
                    this.fieldValue = BigDecimal.valueOf(fieldValue);
                    break;
                case UNIXSECOND:
                    this.fieldValue = BigDecimal.valueOf(fieldValue / 1000);
                    break;
            }
        }
    }

    /**
     * 设置 {@link Provide#UNIXMILLI} 或 {@link Provide#UNIXSECOND} 时字段值
     *
     * @param fieldValue 字段值(必须为时间)
     */
    public void setFieldValue(Date fieldValue) {
        if (fieldValue != null && !Provide.DATA.equals(provide)) {
            this.fieldValue = BigDecimal.valueOf((Provide.UNIXMILLI.equals(provide) ?
                    fieldValue.getTime() : fieldValue.getTime() / 1000));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Provide {

        /**
         * 毫秒时间戳
         */
        UNIXMILLI("数据时间戳 (毫秒)"),

        /**
         * 秒时间戳
         */
        UNIXSECOND("数据时间戳 (秒)"),

        /**
         * 时间
         */
        DATA("数据"),
        ;

        private final String value;
    }

    public static final Long serialVersionUID = 1L;
}