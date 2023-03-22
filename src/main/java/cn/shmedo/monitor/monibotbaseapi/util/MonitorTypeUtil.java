package cn.shmedo.monitor.monibotbaseapi.util;


import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;

import java.util.List;

public class MonitorTypeUtil {

    public static String getMeasurement(Integer monitorType, boolean raw, boolean avg) {
        StringBuilder result = new StringBuilder();
        result.append("tb_");
        if (raw) {
            result.append("raw_");
        }
        result.append(monitorType);
        if (avg) {
            result.append("_avg");
        }
        return result.toString();
    }


    /**
     * 处理额外的业务字段
     */
    public static List<FieldSelectInfo> handlefieldList(Integer monitorType, List<FieldSelectInfo> fieldList) {

        // 水质
        if (monitorType.equals(MonitorType.WATER_QUALITY.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("waterQuality");
            vo.setFieldName("水质");
            vo.setFieldOrder(0);
            fieldList.add(vo);
        }

        // 风力
        if (monitorType.equals(MonitorType.WIND_SPEED.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("windPower");
            vo.setFieldName("风力");
            vo.setFieldOrder(0);
            fieldList.add(vo);
        }

        // 当前降雨量
        if (monitorType.equals(MonitorType.RAINFALL.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("currentRainfall");
            vo.setFieldName("当前降雨量");
            vo.setFieldOrder(0);
            // 雨量单位ID:1
            vo.setFieldExValue("1");
            fieldList.add(vo);
        }

        // 流速
        if (monitorType.equals(MonitorType.FLOW_VELOCITY.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("flow");
            vo.setFieldName("流量");
            vo.setFieldOrder(0);
            vo.setFieldExValue("15");
            fieldList.add(vo);
        }

        return fieldList;
    }
}
