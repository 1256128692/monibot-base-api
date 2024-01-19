package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint.QueryPresetPointListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.weather.QueryWeatherForecastParameter;
import cn.shmedo.monitor.monibotbaseapi.service.WeatherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wuxl
 * @Date 2023/11/28 17:00
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller
 * @ClassName: WeatherController
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@AllArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    /**
     * @api {POST} /QueryWeatherForecast 查询天气预报
     * @apiDescription 查询天气预报
     * @apiVersion 1.0.0
     * @apiGroup 预报管理模块
     * @apiName QueryWeatherForecast
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Object} projectInfo 项目信息
     * @apiSuccess (返回结果) {Int} projectInfo.id 项目ID
     * @apiSuccess (返回结果) {String} projectInfo.projectName 项目名称
     * @apiSuccess (返回结果) {String} projectInfo.shortName 项目简称
     * @apiSuccess (返回结果) {Int} projectInfo.projectType 项目类型
     * @apiSuccess (返回结果) {Int} projectInfo.level 项目等级
     * @apiSuccess (返回结果) {String} projectInfo.directManageUnit 直管单位
     * @apiSuccess (返回结果) {DateTime} projectInfo.expiryDate 项目有效期
     * @apiSuccess (返回结果) {Bool} projectInfo.enable 是否有效
     * @apiSuccess (返回结果) {String} projectInfo.projectAddress 项目地址
     * @apiSuccess (返回结果) {Double} projectInfo.longitude 项目经度
     * @apiSuccess (返回结果) {Double} projectInfo.latitude 项目纬度
     * @apiSuccess (返回结果) {String} projectInfo.[imagePath] 项目图片地址
     * @apiSuccess (返回结果) {String} projectInfo.[projectDesc] 项目简介
     * @apiSuccess (返回结果) {DateTime} projectInfo.createTime 创建时间
     * @apiSuccess (返回结果) {Int} projectInfo.createUserID 创建用户ID
     * @apiSuccess (返回结果) {DateTime} projectInfo.updateTime 修改时间
     * @apiSuccess (返回结果) {Int} projectInfo.updateUserID 修改用户ID
     * @apiSuccess (返回结果) {Object} now 实时天气
     * @apiSuccess (返回结果) {Date} now.obsTime 数据观测时间
     * @apiSuccess (返回结果) {Int} now.temp 温度，默认单位：摄氏度
     * @apiSuccess (返回结果) {Int} now.feelsLike 体感温度，默认单位：摄氏度
     * @apiSuccess (返回结果) {Int} now.icon 天气状况的图标代码，另请参考天气图标项目
     * @apiSuccess (返回结果) {String} now.text 天气状况的文字描述，包括阴晴雨雪等天气状态的描述
     * @apiSuccess (返回结果) {Int} now.wind360 风向360角度
     * @apiSuccess (返回结果) {String} now.windDir 风向
     * @apiSuccess (返回结果) {Int} now.windScale 风力等级
     * @apiSuccess (返回结果) {Int} now.windSpeed 风速，公里/小时
     * @apiSuccess (返回结果) {Int} now.humidity 相对湿度，百分比数值
     * @apiSuccess (返回结果) {Double} now.precip 当前小时累计降水量，默认单位：毫米
     * @apiSuccess (返回结果) {Int} now.pressure 大气压强，默认单位：百帕
     * @apiSuccess (返回结果) {Int} now.vis 能见度，默认单位：公里
     * @apiSuccess (返回结果) {Int} [now.cloud] 云量，百分比数值
     * @apiSuccess (返回结果) {Int} [now.dew] 露点温度
     * @apiSuccess (返回结果) {Object} hourly 实时天气
     * @apiSuccess (返回结果) {Date} hourly.fxTime 预报时间
     * @apiSuccess (返回结果) {Int} hourly.temp 温度，默认单位：摄氏度
     * @apiSuccess (返回结果) {Int} hourly.icon 天气状况的图标代码，另请参考天气图标项目
     * @apiSuccess (返回结果) {String} hourly.text 天气状况的文字描述，包括阴晴雨雪等天气状态的描述
     * @apiSuccess (返回结果) {Int} hourly.wind360 风向360角度
     * @apiSuccess (返回结果) {String} hourly.windDir 风向
     * @apiSuccess (返回结果) {String} hourly.windScale 风力等级
     * @apiSuccess (返回结果) {Int} hourly.windSpeed 风速，公里/小时
     * @apiSuccess (返回结果) {Int} hourly.humidity 相对湿度，百分比数值
     * @apiSuccess (返回结果) {Double} hourly.precip 当前小时累计降水量，默认单位：毫米
     * @apiSuccess (返回结果) {Int} [hourly.pop] 逐小时预报降水概率，百分比数值
     * @apiSuccess (返回结果) {Int} hourly.pressure 大气压强，默认单位：百帕
     * @apiSuccess (返回结果) {Int} [hourly.cloud] 云量，百分比数值
     * @apiSuccess (返回结果) {Int} [hourly.dew] 露点温度
     * @apiSuccess (返回结果) {Object} daily 每日天气（7天）
     * @apiSuccess (返回结果) {Date} daily.fxDate 预报日期
     * @apiSuccess (返回结果) {String} daily.sunrise 日出时间，在高纬度地区可能为空
     * @apiSuccess (返回结果) {String} daily.sunset 日落时间，在高纬度地区可能为空
     * @apiSuccess (返回结果) {String} daily.moonrise 当天月升时间，可能为空
     * @apiSuccess (返回结果) {String} daily.moonset 当天月落时间，可能为空
     * @apiSuccess (返回结果) {Int} daily.moonPhase 月相名称
     * @apiSuccess (返回结果) {Int} daily.moonPhaseIcon 月相图标代码，另请参考天气图标项目
     * @apiSuccess (返回结果) {Int} daily.tempMax 预报当天最高温度
     * @apiSuccess (返回结果) {Int} daily.tempMin 预报当天最低温度
     * @apiSuccess (返回结果) {Int} daily.iconDay 预报白天天气状况的图标代码，另请参考天气图标项目
     * @apiSuccess (返回结果) {String} daily.textDay 预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述
     * @apiSuccess (返回结果) {Int} daily.iconNight 预报夜间天气状况的图标代码，另请参考天气图标项目
     * @apiSuccess (返回结果) {String} daily.textNight 预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述
     * @apiSuccess (返回结果) {Int} daily.wind360Day 预报白天风向360角度
     * @apiSuccess (返回结果) {String} daily.windDirDay 预报白天风向
     * @apiSuccess (返回结果) {String} daily.windScaleDay 预报白天风力等级
     * @apiSuccess (返回结果) {Int} daily.windSpeedDay 预报白天风速，公里/小时
     * @apiSuccess (返回结果) {Int} daily.wind360Night 预报夜间风向360角度
     * @apiSuccess (返回结果) {String} daily.windDirNight 预报夜间当天风向
     * @apiSuccess (返回结果) {String} daily.windScaleNight 预报夜间风力等级
     * @apiSuccess (返回结果) {Int} daily.windSpeedNight 预报夜间风速，公里/小时
     * @apiSuccess (返回结果) {Double} daily.precip 预报当天总降水量，默认单位：毫米
     * @apiSuccess (返回结果) {Int} daily.uvIndex 紫外线强度指数
     * @apiSuccess (返回结果) {Int} daily.humidity 相对湿度，百分比数值
     * @apiSuccess (返回结果) {Int} daily.pressure 大气压强，默认单位：百帕
     * @apiSuccess (返回结果) {Int} daily.vis 能见度，默认单位：公里
     * @apiSuccess (返回结果) {Int} [daily.cloud] 云量，百分比数值。可能为空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:QueryWeatherForecast
     */
    @Permission(permissionName = "mdmbase:QueryWeatherForecast")
    @PostMapping(value = "/QueryWeatherForecast", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWeatherForecast(@Valid @RequestBody QueryWeatherForecastParameter param) {
        return weatherService.queryWeatherForecast(param);
    }
}
