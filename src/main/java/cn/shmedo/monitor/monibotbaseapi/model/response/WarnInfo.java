package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.hutool.core.map.MapUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorSatusType;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class WarnInfo {
    private Integer normalCount;
    private Integer noDataCount;
    private Integer levelOneCount;
    private Integer levelTwoCount;
    private Integer levelThreeCount;
    private Integer levelFourCount;

    public static WarnInfo toBuliderNewVo(List<TbSensor> sensorList) {
        WarnInfo vo = new WarnInfo();
        vo.setNoDataCount(0);
        vo.setNormalCount(0);
        vo.setLevelOneCount(0);
        vo.setLevelTwoCount(0);
        vo.setLevelThreeCount(0);
        vo.setLevelFourCount(0);

        // 进行监测点分组,一个监测点下可能有多个传感器
        Map<Integer, List<TbSensor>> sensorsByMonitorPoint = sensorList.stream().filter(sensor -> sensor.getMonitorPointID() != null)
                .collect(Collectors.groupingBy(TbSensor::getMonitorPointID));

        // 遍历每个传感器
        for (Map.Entry<Integer, List<TbSensor>> entry : sensorsByMonitorPoint.entrySet()) {
            List<TbSensor> sensorsForMonitorPoint = entry.getValue().stream().filter(sensor -> sensor.getStatus() != null).collect(Collectors.toList());

            // 拿到最差的预警警报
            if (!CollectionUtils.isEmpty(sensorsForMonitorPoint)) {
                byte maxStatus = -1;
                for (TbSensor sensor : sensorsForMonitorPoint) {
                    if (sensor.getStatus() > maxStatus) {
                        maxStatus = sensor.getStatus();
                    }
                }
                switch (maxStatus) {
                    case -1:
                        vo.setNoDataCount(vo.getNoDataCount() + 1);
                        break;
                    case 0:
                        vo.setNormalCount(vo.getNormalCount() + 1);
                        break;
                    case 1:
                        vo.setLevelOneCount(vo.getLevelOneCount() + 1);
                        break;
                    case 2:
                        vo.setLevelTwoCount(vo.getLevelTwoCount() + 1);
                        break;
                    case 3:
                        vo.setLevelThreeCount(vo.getLevelThreeCount() + 1);
                        break;
                    case 4:
                        vo.setLevelFourCount(vo.getLevelFourCount() + 1);
                        break;
                }
            }
        }
        return vo;
    }
}
