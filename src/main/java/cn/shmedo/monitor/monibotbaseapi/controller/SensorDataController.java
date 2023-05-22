package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensordata.StatisticsSensorDataParam;
import cn.shmedo.monitor.monibotbaseapi.service.SensorDataService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-05-18 15:14
 **/
@RestController
@AllArgsConstructor
public class SensorDataController {
    private final SensorDataService sensorDataService;

    /**
     * @api {POST} /StatisticsSensorData 计算传感器统计数据
     * @apiVersion 1.0.0
     * @apiGroup 设备模块
     * @apiName StatisticsSensorData
     * @apiDescription 计算传感器的统计数据。当天的统计数据的时间为当天的00:00:00.000
     * @apiParam (请求体) {Int[]} sensorIDList 传感器列表,[1-100],必须为同一种类型的传感器
     * @apiParam (请求体) {DateTime} begin 开始统计时间，包含
     * @apiParam (请求体) {DateTime} end 结束统计时间，包含
     * @apiParam (请求体) {Bool} raw true:原始数据,false:修正数据
     * @apiSuccess (响应结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限+应用权限:iot:ManagerMDMBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ManagerMDMBaseSensorData", allowApplication = true)
    @RequestMapping(value = "/StatisticsSensorData", method = RequestMethod.POST,
            produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object statisticsSensorData(@Valid @RequestBody StatisticsSensorDataParam pa) {
        sensorDataService.statisticsSensorData(pa);
        return ResultWrapper.successWithNothing();
    }


}
