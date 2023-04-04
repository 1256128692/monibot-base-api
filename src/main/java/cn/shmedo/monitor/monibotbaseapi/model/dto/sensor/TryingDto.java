package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 获取试运行参数 响应体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class TryingDto {

    private Integer calType;

    private List<Field> fieldList;

    private String script;

    private List<Param> paramList;

    public record Param(String name, String value, Integer unit, String origin, String token, String type) {
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Field extends TbMonitorTypeField {
        /**
         * 字段值
         */
        private String value;

        /**
         * 公式
         */
        private String formula;
    }
}

    
    