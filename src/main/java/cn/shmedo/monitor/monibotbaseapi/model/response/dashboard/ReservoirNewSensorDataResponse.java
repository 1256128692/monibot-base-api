package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ReservoirNewSensorDataResponse {

    private Integer projectID;
    private String projectName;
    private String shortName;
    // 水库规模,1:小(Ⅰ)型水库,2:小(Ⅱ)型水库,3:中型水库,4:大(Ⅰ)型水库,5:大(Ⅱ)型水库
    private Integer reservoirScale;

    @JsonIgnore
    private String location;
    private String areaCode;
    private String areaName;
    private Date dataTime;
    private Double waterValue;
    private Double periodRainValue;
    private Double currentRainValue;


    public static ReservoirNewSensorDataResponse toNewVo(TbProjectInfo tbProjectInfo,
                                                         List<TbProjectProperty> tbProjectProperties,
                                                         List<Map<String, Object>> resultList, String areaName) {
        ReservoirNewSensorDataResponse vo = new ReservoirNewSensorDataResponse();

        vo.setProjectID(tbProjectInfo.getID());
        vo.setProjectName(tbProjectInfo.getProjectName());
        vo.setShortName(tbProjectInfo.getShortName());
        vo.setLocation(tbProjectInfo.getLocation());
        vo.setAreaCode(tbProjectInfo.getLocation());
        vo.setAreaName(areaName);

        String latestTimeString = getLatestTimeString(resultList);
        if (latestTimeString != null) {
            DateTime latestTime = DateUtil.parse(latestTimeString);
            vo.setDataTime(latestTime);
        } else {
            vo.setDataTime(null);
        }

        // 水库规模,1:小(Ⅰ)型水库,2:小(Ⅱ)型水库,3:中型水库,4:大(Ⅰ)型水库,5:大(Ⅱ)型水库
        if (!CollectionUtil.isNullOrEmpty(tbProjectProperties)) {
            tbProjectProperties.forEach(p -> {
                if (StringUtils.isNotEmpty(p.getValue())) {
                    if (p.getValue().contains("小(Ⅰ)型")) {
                        vo.setReservoirScale(1);
                    }
                    if (p.getValue().contains("小(Ⅱ)型")) {
                        vo.setReservoirScale(2);
                    }
                    if (p.getValue().contains("中型")) {
                        vo.setReservoirScale(3);
                    }
                    if (p.getValue().contains("大(Ⅰ)型")) {
                        vo.setReservoirScale(4);
                    }
                    if (p.getValue().contains("大(Ⅱ)型")) {
                        vo.setReservoirScale(5);
                    }
                }

            });
        }

        Map<String, Object> latestWaterValueData = getLatestDataWithItem(resultList, "distance");
        Map<String, Object> latestPeriodRainfallData = getLatestDataWithItem(resultList, "periodRainfall");

        if (ObjectUtil.isNotNull(latestWaterValueData)) {
            vo.setWaterValue((Double) latestWaterValueData.get("distance"));
        }

        if (ObjectUtil.isNotNull(latestPeriodRainfallData)) {
            vo.setCurrentRainValue((Double) latestPeriodRainfallData.get("currentRainfall"));
            vo.setPeriodRainValue((Double) latestPeriodRainfallData.get("periodRainfall"));
        }


        return vo;
    }


    private static String getLatestTimeString(List<Map<String, Object>> resultList) {
        if (resultList != null && !resultList.isEmpty()) {
            String latestTimeString = null;
            DateTime latestTime = null;

            for (Map<String, Object> result : resultList) {
                Object timeObject = result.get("time");
                if (timeObject != null) {
                    String timeString = timeObject.toString();
                    DateTime currentTime = DateUtil.parse(timeString);

                    if (latestTime == null || currentTime.isAfter(latestTime)) {
                        latestTime = currentTime;
                        latestTimeString = timeString;
                    }
                }
            }

            return latestTimeString;
        }

        return null;
    }


    private static Map<String, Object> getLatestDataWithItem(List<Map<String, Object>> dataList, String itemName) {
        Map<String, Object> latestData = null;

        for (Map<String, Object> data : dataList) {
            if (data.containsKey(itemName)) {
                DateTime time = DateUtil.parse((String) data.get("time"));
                if (latestData == null || time.isAfter(DateUtil.parse((String) latestData.get("time")))) {
                    latestData = data;
                }
            }
        }

        return latestData;
    }


}
