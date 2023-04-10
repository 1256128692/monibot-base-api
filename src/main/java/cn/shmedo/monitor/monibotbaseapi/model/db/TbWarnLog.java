package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.util.Date;

@Data
@TableName("tb_warn_log")
public class TbWarnLog {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer ID;
    @TableField("ProjectID")
    private Integer projectID;
    @TableField("MonitorType")
    private Integer monitorType;
    @TableField("MonitorItemID")
    private Integer monitorItemID;
    @TableField("MonitorPointID")
    private Integer monitorPointID;
    @TableField("SensorID")
    private Integer sensorID;
    @TableField("Name")
    private String alarmName;
    @TableField("WarnTime")
    private Date warnTime;
    @TableField("WarnLevel")
    private Integer warnLevel;
    @TableField("WarnContent")
    private String warnContent;
    /**
     * 处置状态，1.待接单 2.处置中 3.已处置 4.审核中 5.已结束 6.已关闭
     */
    @TableField("Status")
    private Integer status;
    @TableField("WorkOrder")
    private String workOrder;
    @Serial
    private static final long serialVersionUID = 1L;
}
