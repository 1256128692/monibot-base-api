package cn.shmedo.monitor.monibotbaseapi.model.response.weather;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * @Author wuxl
 * @Date 2023/11/28 17:51
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.weather
 * @ClassName: QueryWeatherForecastResponse
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class QueryWeatherForecastResponse {
    private TbProjectInfo tbProjectInfo;
    private Now now;
    private List<Hour> hourly;
    private List<Day> daily;
}
