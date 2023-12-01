package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.weather.QueryWeatherForecastParameter;

/**
 * @Author wuxl
 * @Date 2023/11/28 17:02
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service
 * @ClassName: WeatherService
 * @Description: TODO
 * @Version 1.0
 */
public interface WeatherService {
    Object queryWeatherForecast(QueryWeatherForecastParameter param);
}
