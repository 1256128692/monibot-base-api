package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-29 16:19
 */
@Data
@TableName("tb_video_preset_point")
public class TbVideoPresetPoint {
    @TableId(value = "ID")
    private Integer ID;
    /**
     * 设备视频ID
     */
    @TableField("DeviceVideoID")
    private Integer deviceVideoID;
    /**
     * 预置点名称
     */
    @TableField("PresetPointName")
    private String presetPointName;
    /**
     * 预置点位置
     */
    @TableField("PresetPointIndex")
    private Integer presetPointIndex;
}
