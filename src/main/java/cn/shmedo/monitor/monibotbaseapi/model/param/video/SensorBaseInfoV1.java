package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.util.ByteUtil;
import cn.hutool.json.JSONObject;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
public class SensorBaseInfoV1 {

    private Integer sensorID;

    private String sensorName;

    private Boolean sensorEnable;

    private Integer channelCode;

    private Boolean imageCapture;

    private Integer captureInterval;

    private Integer videoDeviceSourceID;

    @JsonIgnore
    private Integer videoDeviceID;

    @JsonIgnore
    private Integer templateID;

    @JsonIgnore
    private Integer dataSourceComposeType;

    @JsonIgnore
    private Integer monitorType;

    @JsonIgnore
    private Byte kind;

    @JsonIgnore
    private Integer displayOrder;

    @JsonIgnore
    private String exValues;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Integer createUserID;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private Integer updateUserID;

    @JsonIgnore
    private String deviceSerial;

    @JsonIgnore
    private Integer projectID;


    public static SensorBaseInfoV1 createNewSensor(SensorBaseInfoV1 inputSensor,
                                                   Integer subjectID,
                                                   VideoDeviceInfoV3 videoDeviceID,
                                                   Integer displayOrder) {
        SensorBaseInfoV1 newSensor = new SensorBaseInfoV1();

        // 复制输入对象的属性到新对象
        newSensor.setProjectID(videoDeviceID.getProjectID()==null ? -1 : videoDeviceID.getProjectID());
        newSensor.setSensorName(inputSensor.getSensorName());
        newSensor.setSensorEnable(inputSensor.getSensorEnable());
//        newSensor.setChannelCode(inputSensor.getChannelCode());
        newSensor.setCaptureInterval(inputSensor.getCaptureInterval());

        // 设置默认值
//        newSensor.setVideoDeviceID(videoDeviceID.getVideoDeviceID());
        newSensor.setTemplateID(-1);
        newSensor.setDataSourceComposeType(1);
        newSensor.setMonitorType(MonitorType.VIDEO.getKey());
        newSensor.setKind(ByteUtil.intToByte(1));
        newSensor.setDisplayOrder(displayOrder);

        // 创建一个 JSON 对象
        JSONObject jsonObject = new JSONObject();

        // 设置 JSON 对象的字段
        jsonObject.put(DefaultConstant.VIDEO_DEVICE_SN, videoDeviceID.getDeviceSerial());
        jsonObject.put(DefaultConstant.VIDEO_CHANNEL, inputSensor.getChannelCode());
//        jsonObject.put(DefaultConstant.VIDEO_IMAGECAPTURE, inputSensor.getImageCapture());
//        jsonObject.put(DefaultConstant.VIDEO_CAPTUREINTERVAL, inputSensor.getCaptureInterval() != null ? inputSensor.getCaptureInterval() : 0);

        newSensor.setExValues(jsonObject.toString());
        newSensor.setCreateTime(new Date());
        newSensor.setCreateUserID(subjectID);
        newSensor.setUpdateTime(new Date());
        newSensor.setUpdateUserID(subjectID);

        newSensor.setVideoDeviceSourceID(inputSensor.getVideoDeviceSourceID());

        return newSensor;
    }

    public static SensorBaseInfoV1 createUpdateSensor(SensorBaseInfoV1 inputSensor,
                                                      Integer subjectID,
                                                      VideoDeviceInfoV3 videoDeviceInfoV3) {

        SensorBaseInfoV1 newSensor = new SensorBaseInfoV1();

        // 复制输入对象的属性到新对象
        newSensor.setProjectID(videoDeviceInfoV3.getProjectID()==null ? -1 : videoDeviceInfoV3.getProjectID());
        newSensor.setSensorID(inputSensor.getSensorID());
        newSensor.setSensorName(inputSensor.getSensorName());
        newSensor.setSensorEnable(inputSensor.getSensorEnable());
//        newSensor.setChannelCode(inputSensor.getChannelCode());
//        newSensor.setCaptureInterval(inputSensor.getCaptureInterval());

        // 创建一个 JSON 对象
        JSONObject jsonObject = new JSONObject();

        // 设置 JSON 对象的字段
        jsonObject.put(DefaultConstant.VIDEO_DEVICE_SN, videoDeviceInfoV3.getDeviceSerial());
        jsonObject.put(DefaultConstant.VIDEO_CHANNEL, inputSensor.getChannelCode());
//        jsonObject.put(DefaultConstant.VIDEO_IMAGECAPTURE, inputSensor.getImageCapture());
//        jsonObject.put(DefaultConstant.VIDEO_CAPTUREINTERVAL, inputSensor.getCaptureInterval() != null ? inputSensor.getCaptureInterval() : 0);

        newSensor.setExValues(jsonObject.toString());
        newSensor.setUpdateTime(new Date());
        newSensor.setUpdateUserID(subjectID);

        return newSensor;


    }
}
