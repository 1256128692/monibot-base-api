package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.hutool.core.map.MapUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorSatusType;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import lombok.Data;

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
        if (!CollectionUtils.isEmpty(sensorList)) {
            Map<Byte, List<TbSensor>> listMap = sensorList.stream().filter(sensor -> sensor.getStatus() != null)
                    .collect(Collectors.groupingBy(TbSensor::getStatus));
            if (MapUtil.isNotEmpty(listMap)){
                listMap.entrySet().forEach(item -> {
                    if (item.getKey().toString().equals(String.valueOf(SensorSatusType.TYPE_0.getKey()))) {
                        vo.setNormalCount(item.getValue().size());
                    } else if (item.getKey().toString().equals(String.valueOf(SensorSatusType.TYPE_1.getKey()))) {
                        vo.setLevelOneCount(item.getValue().size());
                    } else if (item.getKey().toString().equals(String.valueOf(SensorSatusType.TYPE_2.getKey()))) {
                        vo.setLevelTwoCount(item.getValue().size());
                    } else if (item.getKey().toString().equals(String.valueOf(SensorSatusType.TYPE_3.getKey()))) {
                        vo.setLevelThreeCount(item.getValue().size());
                    } else if (item.getKey().toString().equals(String.valueOf(SensorSatusType.TYPE_4.getKey()))) {
                        vo.setLevelFourCount(item.getValue().size());
                    } else if (item.getKey().toString().equals(String.valueOf(SensorSatusType.TYPE_ERROR.getKey()))) {
                        vo.setNoDataCount(item.getValue().size());
                    }
                });
            }
        }
        vo.setNoDataCount(vo.getNoDataCount() == null ? 0 : vo.getNoDataCount());
        vo.setNormalCount(vo.getNormalCount() == null ? 0 : vo.getNormalCount());
        vo.setLevelOneCount(vo.getLevelOneCount() == null ? 0 : vo.getLevelOneCount());
        vo.setLevelTwoCount(vo.getLevelTwoCount() == null ? 0 : vo.getLevelTwoCount());
        vo.setLevelThreeCount(vo.getLevelThreeCount() == null ? 0 : vo.getLevelThreeCount());
        vo.setLevelFourCount(vo.getLevelFourCount() == null ? 0 : vo.getLevelFourCount());

        return vo;
    }
}
