package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 15:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties
public class WetLineConfigInfo extends ThematicGroupPointListInfo {
    private String monitorGroupImagePath;
    private List<ThematicProjectConfigInfo> monitorGroupConfigList;
    private List<ThematicPointListInfoV2> monitorPointList;
    /**
     * just for {@code @JsonIgnore}
     */
    private List<ThematicPointListInfo> monitorPointDataList;

    /**
     * 不能作为{@link com.fasterxml.jackson.annotation.JsonProperty}写到{@link ThematicPointListInfoV2}类里<br>
     * 因为如果{@link ThematicPointListInfoV2#getEmptyPipeDistance()}为{@code null}时会出现json序列化错误。<br>
     * 尝试过{@link com.fasterxml.jackson.annotation.JsonInclude.Include#NON_NULL},{@link com.fasterxml.jackson.annotation.JsonIgnore},{@link com.fasterxml.jackson.databind.annotation.JsonSerialize}修改序列化器,但是均未生效依然报错。
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public WetLineConfigInfo afterProperties() {
        Optional.ofNullable(monitorPointList).filter(CollUtil::isNotEmpty).ifPresent(u -> u.stream().peek(w -> {
            Optional.ofNullable(w.getEmptyPipeDistance()).filter(s -> s < 0).ifPresent(s -> w.setEmptyPipeDistance(0D));
            Optional.ofNullable(w.getEmptyPipeDistance()).flatMap(s -> Optional.ofNullable(w.getLevelElevation())
                    .map(n -> s + n)).ifPresent(w::setNozzleElevation);
        }).toList());
        return this;
    }
}
