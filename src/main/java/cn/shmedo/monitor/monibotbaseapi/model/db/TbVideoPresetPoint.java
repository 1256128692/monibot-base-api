package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频设备预置点表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbVideoPresetPoint implements Serializable {
    private Integer ID;

    /**
     * 设备视频ID
     */
    private Integer deviceVideoID;

    /**
     * 预置点名称
     */
    private String presetPointName;

    /**
     * 预置点位置
     */
    private Integer presetPointIndex;

    private static final long serialVersionUID = 1L;
}