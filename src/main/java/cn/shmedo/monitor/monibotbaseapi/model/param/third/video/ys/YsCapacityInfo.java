package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 15:26
 */
@Data
public class YsCapacityInfo {
    @JsonAlias("support_audio_onoff")
    private Integer supportAudioOnoff;
    @JsonAlias("support_volumn_set")
    private Integer supportVolumnSet;
    @JsonAlias("support_capture")
    private Integer supportCapture;
    @JsonAlias("support_talk")
    private Integer supportTalk;
    @JsonAlias("support_mcvolumn_set")
    private Integer supportMcvolumnSet;
    @JsonAlias("ptz_focus")
    private Integer ptzFocus;
    @JsonAlias("ptz_top_bottom")
    private Integer ptzTopBottom;
    @JsonAlias("ptz_left_right")
    private Integer ptzLeftRight;
    @JsonAlias("ptz_45")
    private Integer ptz45;
    @JsonAlias("ptz_zoom")
    private Integer ptzZoom;
    @JsonAlias("ptz_preset")
    private Integer ptzPreset;
    @JsonAlias("support_rate_limit")
    private Integer supportRateLimit;

    /**
     * param is null means ezviz device do not supprot this capacity,<br>
     * set to zero here for ensuring every param is not null.
     */
    private void afterProperties() {
        this.supportAudioOnoff = Objects.isNull(supportAudioOnoff) ? 0 : supportAudioOnoff;
        this.supportVolumnSet = Objects.isNull(supportVolumnSet) ? 0 : supportVolumnSet;
        this.supportCapture = Objects.isNull(supportCapture) ? 0 : supportCapture;
        this.supportTalk = Objects.isNull(supportTalk) ? 0 : supportTalk;
        this.supportMcvolumnSet = Objects.isNull(supportMcvolumnSet) ? 0 : supportMcvolumnSet;
        this.ptzFocus = Objects.isNull(ptzFocus) ? 0 : ptzFocus;
        this.ptzTopBottom = Objects.isNull(ptzTopBottom) ? 0 : ptzTopBottom;
        this.ptzLeftRight = Objects.isNull(ptzLeftRight) ? 0 : ptzLeftRight;
        this.ptz45 = Objects.isNull(ptz45) ? 0 : ptz45;
        this.ptzZoom = Objects.isNull(ptzZoom) ? 0 : ptzZoom;
        this.ptzPreset = Objects.isNull(ptzPreset) ? 0 : ptzPreset;
        this.supportRateLimit = Objects.isNull(supportRateLimit) ? 0 : supportRateLimit;
    }

    public Map<String, Integer> toMap() {
        afterProperties();
        return BeanUtil.beanToMap(this).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> (Integer) v.getValue()));
    }
}
