package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 视频设备抓拍表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbVideoCapture implements Serializable {
    private Integer ID;

    /**
    * 设备序列号/监控点唯一标识
    */
    private String deviceSerial;

    /**
    * 传感器ID
    */
    private Integer sensorID;

    /**
    * 抓拍间隔
    */
    private Integer captureInterval;

    /**
    * 最后抓拍时间
    */
    private Date lastCaptureTime;

    /**
    * 下次抓拍时间
    */
    private Date nextCaptureTime;

    private static final long serialVersionUID = 1L;
}