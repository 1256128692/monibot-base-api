package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 视频设备预置点表
 */
@Data
@TableName("tb_video_preset_point")
public class TbVideoPresetPoint implements Serializable {
    @TableId(value = "ID",type = IdType.AUTO)
    private Integer ID;
    /**
     * 设备视频ID
     */
    @TableField("VideoDeviceID")
    private Integer videoDeviceID;
    /**
     * 通道号
     */
    @TableField("ChannelNo")
    private Integer channelNo;
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