package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.lang.Assert;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.RegionAreaMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.GetLocationParam;
import cn.shmedo.monitor.monibotbaseapi.service.RegionAreaService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class RegionAreaServiceImpl extends ServiceImpl<RegionAreaMapper, RegionArea> implements RegionAreaService {

    private final RedisService redisService;

    @Bean
    ApplicationRunner runner() {
        return args -> {
            if (!redisService.hasKey(RedisKeys.REGION_AREA_KEY)) {
                Map<Integer, RegionArea> dataMap = new HashMap<>();
                baseMapper.streamQuery(resultContext -> {
                    RegionArea regionArea = resultContext.getResultObject();
                    dataMap.put(regionArea.getId(), regionArea);
                    if (dataMap.size() == 1000) {
                        redisService.putAll(RedisKeys.REGION_AREA_KEY, dataMap);
                        dataMap.clear();
                    }
                }, new LambdaQueryWrapper<RegionArea>().eq(RegionArea::getStatus, 1));

                if (!dataMap.isEmpty()) {
                    redisService.putAll(RedisKeys.REGION_AREA_KEY, dataMap);
                }
            }
        };
    }

    @Override
    public Object getLocationById(GetLocationParam param) {
        RegionArea area = redisService.get(RedisKeys.REGION_AREA_KEY, param.getCode().toString(), RegionArea.class);
        Assert.notNull(area, "地区编号不存在");
        record result(String name, String lat, String lon){}
        return new result(area.getName(), area.getLatitude(), area.getLongitude());
    }
}
