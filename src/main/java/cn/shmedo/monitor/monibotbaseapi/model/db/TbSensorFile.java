package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbSensorFile implements Serializable {
    private Integer ID;

    private Integer sensorID;

    private Date time;

    private Date uploadTime;

    private String filePath;

    private String fileType;

    private Integer fileSize;

    private Integer storageType;

    private String s1;

    private String s2;

    private String s3;

    private String s4;

    private Integer i1;

    private Integer i2;

    private Integer i3;

    private Integer i4;

    private static final long serialVersionUID = 1L;
}