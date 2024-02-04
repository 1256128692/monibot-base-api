package cn.shmedo.monitor.monibotbaseapi.util;


import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DisplayDensity;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.StatisticalMethods;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MonitorTypeUtil {

    public static String getMeasurement(Integer monitorType, boolean raw, boolean avg) {
        StringBuilder result = new StringBuilder();
        result.append("tb_");
        if (raw) {
            result.append("raw_");
        }
        result.append(monitorType);
        result.append("_data");
        if (avg) {
            result.append("_avg");
        }
        return result.toString();
    }

    public static String getMeasurement(Integer monitorType, boolean raw, String stat) {
        StringBuilder result = new StringBuilder();
        result.append("tb_");
        if (raw) {
            result.append("raw_");
        }
        result.append(monitorType);
//        result.append("_data_");
//        result.append(stat);
        result.append("_data");
        Optional.ofNullable(stat).filter(ObjectUtil::isNotEmpty).ifPresent(u -> result.append("_").append(u));
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
            vo.setFieldName("水质等级");
            vo.setFieldOrder(0);
            vo.setFieldStatisticsType("1");
            fieldList.add(vo);
        }

        // 风力
        if (monitorType.equals(MonitorType.WIND_SPEED.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("windPower");
            vo.setFieldName("风力");
            vo.setFieldOrder(0);
            vo.setFieldStatisticsType("1");
            vo.setFieldExValue("38");
            fieldList.add(vo);
        }


        // 土壤PH
        if (monitorType.equals(MonitorType.SOIL_PH.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("soilphQuality");
            vo.setFieldName("土壤酸碱度");
            vo.setFieldOrder(0);
            vo.setFieldStatisticsType("1");
            vo.setFieldExValue("18");
            fieldList.add(vo);
        }


        // 土壤盐分电导率
        if (monitorType.equals(MonitorType.SOIL_SALINITY_ELECTRICAL_CONDUCTIVITY.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("soilconductivityQuality");
            vo.setFieldName("土壤类型");
            vo.setFieldOrder(0);
            vo.setFieldStatisticsType("1");
            vo.setFieldExValue("18");
            fieldList.add(vo);
        }

        return fieldList;
    }

    /**
     * 处理查询历史数据接口的额外的业务字段
     */
    public static void handleHistoryDatafieldList(Integer monitorType, List<FieldSelectInfo> fieldList) {

        // 水质
        if (monitorType.equals(MonitorType.WATER_QUALITY.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("waterQuality");
            vo.setFieldName("水质等级");
            vo.setFieldOrder(0);
            vo.setFieldStatisticsType("1");
            fieldList.add(vo);
        }

        // 风力
        if (monitorType.equals(MonitorType.WIND_SPEED.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("windPower");
            vo.setFieldName("风力");
            vo.setFieldOrder(0);
            vo.setFieldStatisticsType("1");
            vo.setFieldExValue("38");
            fieldList.add(vo);
        }

        // 水位
//        if (monitorType.equals(MonitorType.WATER_LEVEL.getKey())){
//            FieldSelectInfo vo = new FieldSelectInfo();
//            vo.setFieldToken("levelChange");
//            vo.setFieldName("水位变化");
//            vo.setFieldOrder(0);
//            vo.setFieldExValue("9");
//            fieldList.add(vo);
//        }

        // 压力
        if (monitorType.equals(MonitorType.STRESS.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("strainPeriodDisp");
            vo.setFieldName("压力变化");
            vo.setFieldOrder(0);
            vo.setFieldExValue("13");
            fieldList.add(vo);
        }

        // 压强
        if (monitorType.equals(MonitorType.PRESSURE.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("pressurePeriodDisp");
            vo.setFieldName("压强变化");
            vo.setFieldOrder(0);
            vo.setFieldExValue("13");
            fieldList.add(vo);
        }

        // 一维
        if (monitorType.equals(MonitorType.ONE_DIMENSIONAL_DISPLACEMENT.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("xPeriodDisp");
            vo.setFieldName("阶段位移");
            vo.setFieldOrder(0);
            vo.setFieldExValue("1");
            fieldList.add(vo);
        }

        // 三维
        if (monitorType.equals(MonitorType.THREE_DIMENSIONAL_DISPLACEMENT.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("xPeriodDisp");
            vo.setFieldName("X轴阶段位移");
            vo.setFieldOrder(0);
            vo.setFieldExValue("1");

            FieldSelectInfo vo1 = new FieldSelectInfo();
            vo1.setFieldToken("yPeriodDisp");
            vo1.setFieldName("Y轴阶段位移");
            vo1.setFieldOrder(0);
            vo1.setFieldExValue("1");


            FieldSelectInfo vo2 = new FieldSelectInfo();
            vo2.setFieldToken("zPeriodDisp");
            vo2.setFieldName("Z轴阶段位移");
            vo2.setFieldOrder(0);
            vo2.setFieldExValue("1");
            fieldList.addAll(List.of(vo,vo1,vo2));
        }

        // 内部三轴
        if (monitorType.equals(MonitorType.INTERNAL_TRIAXIAL_DISPLACEMENT.getKey())){
            FieldSelectInfo vo = new FieldSelectInfo();
            vo.setFieldToken("aPeriodDisp");
            vo.setFieldName("A轴阶段位移");
            vo.setFieldOrder(0);
            vo.setFieldExValue("1");

            FieldSelectInfo vo1 = new FieldSelectInfo();
            vo1.setFieldToken("bPeriodDisp");
            vo1.setFieldName("B轴阶段位移");
            vo1.setFieldOrder(0);
            vo1.setFieldExValue("1");


            FieldSelectInfo vo2 = new FieldSelectInfo();
            vo2.setFieldToken("cPeriodDisp");
            vo2.setFieldName("C轴阶段位移");
            vo2.setFieldOrder(0);
            vo2.setFieldExValue("1");
            fieldList.addAll(List.of(vo,vo1,vo2));
        }

    }

    public static String getMeasurementByStatisticsTypeAndDensityType(Integer monitorType, Integer statisticsType,
                                                                      Integer densityType) {
        StringBuilder result = new StringBuilder();
        result.append("tb_");
        result.append(monitorType);
        result.append("_data");
        // 查询最新数据的时候查原始表即可
        if (statisticsType != null) {
            if (statisticsType != StatisticalMethods.LATEST.getValue()) {
                if (statisticsType == StatisticalMethods.AVERAGE1.getValue()) {
                    result.append("_");
                    result.append(StatisticalMethods.fromValue(statisticsType).getName());
                } else {
                    // 查询小时的时候也查询原始表即可
                    if (densityType != DisplayDensity.HOUR.getValue() && densityType != DisplayDensity.TWO_HOUR.getValue() &&
                            densityType != DisplayDensity.FOUR_HOUR.getValue() && densityType != DisplayDensity.SIX_HOUR.getValue() &&
                            densityType != DisplayDensity.TWELVE_HOUR.getValue() && densityType != DisplayDensity.DAY.getValue()) {
                        result.append("_");
                        result.append(StatisticalMethods.fromValue(statisticsType).getName());
                    }
                }

            }
        }
        return result.toString();


    }
}
