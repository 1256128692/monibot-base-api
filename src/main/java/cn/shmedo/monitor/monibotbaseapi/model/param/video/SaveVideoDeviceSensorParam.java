package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class SaveVideoDeviceSensorParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;

    @NotEmpty
    private List<@NotNull VideoDeviceInfoV3> list;


    @JsonIgnore
    private List<VideoDeviceInfoV4> videoDeviceList = new LinkedList<>();

    @Override
    public ResultWrapper validate() {

        TbVideoDeviceMapper tbVideoDeviceMapper = ContextHolder.getBean(TbVideoDeviceMapper.class);
        videoDeviceList = tbVideoDeviceMapper.selectByIdList(list.stream().map(VideoDeviceInfoV3::getVideoDeviceID).collect(Collectors.toList()));
        if (CollectionUtil.isNullOrEmpty(videoDeviceList) || videoDeviceList.size() != list.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前请求中有视频设备不存在");
        }
        videoDeviceList.forEach(videoDeviceInfoV4 ->  {
            videoDeviceInfoV4.setProjectID(list.stream()
                    .filter(l -> Objects.equals(l.getVideoDeviceID(), videoDeviceInfoV4.getVideoDeviceID()))
                    .filter(l -> l.getProjectID() != null)
                    .map(VideoDeviceInfoV3::getProjectID).findFirst().orElse(-1));
        });

        List<SensorBaseInfoV1> allSensorBaseInfoList = list.stream()
                .filter(v -> v.getAddSensorList() != null)
                .flatMap(v -> v.getAddSensorList().stream())
                .collect(Collectors.toList());


        if (!CollectionUtil.isNullOrEmpty(allSensorBaseInfoList)) {
            List<String> sensorNameList = allSensorBaseInfoList.stream().map(SensorBaseInfoV1::getSensorName).collect(Collectors.toList());

            int sensorListnum = 0;
            for (VideoDeviceInfoV3 videoDeviceInfo : list) {
                if (videoDeviceInfo.getAddSensorList() != null) {
                    sensorListnum += videoDeviceInfo.getAddSensorList().size();
                }
            }
            if (CollectionUtil.isNullOrEmpty(sensorNameList) || sensorNameList.size() != sensorListnum) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器名称不能为空");
            }


            List<String> sensorNamesWithNullSensorID = new ArrayList<>();
            List<Integer> notNullSensorIDList = new ArrayList<>();
            TbSensorMapper sensorMapper = ContextHolder.getBean(TbSensorMapper.class);
            List<VideoDeviceInfoV6> videoDeviceInfoV6List = sensorMapper.selectListByDeviceSerialList(list.stream().map(VideoDeviceInfoV3::getDeviceSerial).distinct().collect(Collectors.toList()));

            for (int i = 0; i < list.size(); i++) {
                VideoDeviceInfoV3 videoDeviceInfo = list.get(i);
                if (!CollectionUtil.isNullOrEmpty(videoDeviceInfoV6List)) {
                    List<VideoDeviceInfoV6> includeMonitorPointSensorList = videoDeviceInfoV6List.stream()
                            .filter(s -> s.getDeviceSerial().equals(videoDeviceInfo.getDeviceSerial()))
                            .filter(s -> s.getMonitorPointID() != null)
                            .filter(s -> !s.getProjectID().equals(videoDeviceInfo.getProjectID())).collect(Collectors.toList());

                    if (!CollectionUtil.isNullOrEmpty(includeMonitorPointSensorList)) {
                        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "设备转移企业前,传感器需要先解绑监测点");
                    }
                }
                if (videoDeviceInfo.getAddSensorList() != null) {
                    for (int j = 0; j < videoDeviceInfo.getAddSensorList().size(); j++) {
                        SensorBaseInfoV1 sensor = videoDeviceInfo.getAddSensorList().get(j);
                        if (sensor.getSensorID() == null) {
                            sensorNamesWithNullSensorID.add(sensor.getSensorName());
                        } else {
                            notNullSensorIDList.add(sensor.getSensorID());
                        }

                    }
                }
            }

            if (!CollectionUtil.isNullOrEmpty(sensorNamesWithNullSensorID)) {
                List<SensorBaseInfoV1> sensorList = sensorMapper.selectListByNameAndProjectID(sensorNamesWithNullSensorID,
                        list.get(0).getProjectID());
                if (!CollectionUtil.isNullOrEmpty(sensorList)) {
                    List<String> sensorNameInTableList = sensorList.stream().map(SensorBaseInfoV1::getSensorName).collect(Collectors.toList());
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传输参数中,有传感器名称已存在:"+sensorNameInTableList.toString());
                }
            }

            if (!CollectionUtil.isNullOrEmpty(notNullSensorIDList)) {
                List<TbSensor> tbSensors = sensorMapper.selectBatchIds(notNullSensorIDList);
                if (CollectionUtil.isNullOrEmpty(tbSensors) || tbSensors.size() != notNullSensorIDList.size()) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传输参数中有不存在的传感器设备");
                }
            }
        }

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }


}
