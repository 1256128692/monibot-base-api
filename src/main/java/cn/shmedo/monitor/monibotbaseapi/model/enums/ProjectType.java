package cn.shmedo.monitor.monibotbaseapi.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 对 tb_project_type 的静态枚举，以方便在代码中使用
 */
@Getter
@AllArgsConstructor
public enum ProjectType {

    @JsonProperty("1")
    RESERVOIR(1, "水库", "水文水利"),

    @JsonProperty("2")
    WATERCOURSE(2, "河道", "水文水利"),

    @JsonProperty("3")
    DIKE(3, "堤防", "水文水利"),

    @JsonProperty("4")
    WATERSHED(4, "流域", "水文水利"),

    @JsonProperty("5")
    TAILING_PONDS(5, "尾矿库", "矿山"),

    @JsonProperty("6")
    FOUNDATION_PIT(6, "基坑", "基建"),

    @JsonProperty("7")
    IRRIGATED_AREA(7, "灌区", "水文水利"),

    @JsonProperty("8")
    HEADWORK(8, "渠首", "水文水利"),

    @JsonProperty("9")
    DRIVEN_WELL(9, "机井", "水文水利"),

    @JsonProperty("10")
    CANAL_SYSTEM(10, "渠系", "水文水利"),

    @JsonProperty("11")
    SLUICE(11, "水闸", "水文水利"),

    @JsonProperty("12")
    PUMP_STATION(12, "泵站", "水文水利"),

    @JsonProperty("13")
    FIELD(13, "田间", "水文水利"),

    @JsonProperty("14")
    GEOLOGIC_HAZARD(14, "地质灾害", "林业"),

    @JsonProperty("15")
    FIRE(15, "火灾", "林业"),

    @JsonProperty("16")
    EXTREME_WEATHER(16, "极端天气", "林业"),

    @JsonProperty("17")
    BIOLOGICAL_NOISE(17, "生物干扰", "林业"),

    @JsonProperty("18")
    ILLEGAL_ACTIVITY(18, "非法活动", "林业"),

    @JsonProperty("19")
    ENVIRONMENTAL_POLLUTION(19, "环境污染", "林业"),
    ;

    @JsonValue
    @EnumValue
    private final Integer code;
    private final String name;
    private final String mainType;

    public static ProjectType formInt(Integer code) {
        return Arrays.stream(ProjectType.values()).filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ProjectType code " + code + " is not exist"));
    }

    public static List<ProjectType> getByMainType(String mainType) {
        return Arrays.stream(ProjectType.values()).filter(e -> e.getMainType().equals(mainType)).toList();
    }
}
