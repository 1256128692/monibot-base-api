package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.weather.QueryWeatherForecastParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.weather.Day;
import cn.shmedo.monitor.monibotbaseapi.model.response.weather.Hour;
import cn.shmedo.monitor.monibotbaseapi.model.response.weather.Now;
import cn.shmedo.monitor.monibotbaseapi.model.response.weather.QueryWeatherForecastResponse;
import cn.shmedo.monitor.monibotbaseapi.service.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author wuxl
 * @Date 2023/11/28 17:03
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service.impl
 * @ClassName: WeatherServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
@AllArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    public static final String REDIS_KEY_OF_WEATHER_NOW = "cn.shmedo.job.weather.hefeng.now";
    public static final String REDIS_KEY_OF_WEATHER_HOURLY = "cn.shmedo.job.weather.hefeng.hourly";
    public static final String REDIS_KEY_OF_WEATHER_DAILY = "cn.shmedo.job.weather.hefeng.daily";
    private static final String area = "area";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Object queryWeatherForecast(QueryWeatherForecastParameter param) {
        QueryWeatherForecastResponse forecastResponse = new QueryWeatherForecastResponse();
        TbProjectInfo tbProjectInfo = param.getTbProjectInfo();
        String areaValue = JSONUtil.parseObj(tbProjectInfo.getLocation()).getStr(area);
        Object now = redisTemplate.opsForHash().get(REDIS_KEY_OF_WEATHER_NOW, areaValue);
        Object hourly = redisTemplate.opsForHash().get(REDIS_KEY_OF_WEATHER_HOURLY, areaValue);
        Object daily = redisTemplate.opsForHash().get(REDIS_KEY_OF_WEATHER_DAILY, areaValue);

        forecastResponse.setTbProjectInfo(param.getTbProjectInfo());
        if(Objects.nonNull(now)){
            forecastResponse.setNow(JSONUtil.toBean(JSONUtil.toJsonStr(now), Now.class));
        }
        if(Objects.nonNull(hourly)){
            forecastResponse.setHourly(JSONUtil.toList(JSONUtil.toJsonStr(hourly), Hour.class));
        }
        if(Objects.nonNull(daily)){
            forecastResponse.setDaily(JSONUtil.toList(JSONUtil.toJsonStr(daily), Day.class));
        }
        return forecastResponse;
    }
}
