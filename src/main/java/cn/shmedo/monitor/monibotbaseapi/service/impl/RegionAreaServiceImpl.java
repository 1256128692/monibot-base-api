package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.RegionAreaMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.GetLocationParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.ListAreaCodeByLocationNameParam;
import cn.shmedo.monitor.monibotbaseapi.service.RegionAreaService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RegionAreaServiceImpl extends ServiceImpl<RegionAreaMapper, RegionArea> implements RegionAreaService {

    private final RedisService redisService;

    @Bean
    ApplicationRunner runner() {
        return args -> {
            if (!redisService.hasKey(RedisKeys.REGION_AREA_KEY)) {
                Map<Long, RegionArea> dataMap = new HashMap<>();
                baseMapper.streamQuery(resultContext -> {
                    RegionArea regionArea = resultContext.getResultObject();
                    dataMap.put(regionArea.getAreaCode(), regionArea);
                    if (dataMap.size() == 1000) {
                        redisService.putAll(RedisKeys.REGION_AREA_KEY, dataMap);
                        dataMap.clear();
                    }
                }, new LambdaQueryWrapper<>());

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
        record result(String name, String lat, String lon) {
        }
        return new result(area.getName(), area.getLat().toString(), area.getLng().toString());
    }

    @Override
    public Object listAreaCodeByLocationName(ListAreaCodeByLocationNameParam param) {
        List<RegionArea> regionAreaList = baseMapper.selectList(new LambdaQueryWrapper<RegionArea>()
                .and(wrapper -> {
                    for (String locationName : param.getLocationNameList()) {
                        String[] nameSplit = locationName.split(",");
                        wrapper.or().eq(RegionArea::getMergerName, locationName).eq(RegionArea::getShortName, nameSplit[nameSplit.length - 1]);
                    }
                }).ne(RegionArea::getName, "直辖区").ne(RegionArea::getName, "市辖区"));
        if (regionAreaList.isEmpty() || regionAreaList.size() != param.getLocationNameList().size()) {
            throw new InvalidParameterException("非法的地区名称");
        }
        record result(String locationName, String code) {
        }
        return regionAreaList.stream().map(regionArea -> new result(regionArea.getMergerName(),
                buildLocation(regionArea))).collect(Collectors.toList());
    }

    private String buildLocation(RegionArea regionArea) {
        record location(Long province, Long city, Long area, Long town) {
        }
        return switch (regionArea.getLevel()) {
            case 0 -> JsonUtil.toJson(new location(regionArea.getAreaCode(), null, null, null));
            case 1 ->
                    JsonUtil.toJson(new location((regionArea.getAreaCode() / 10000) * 10000, regionArea.getAreaCode(), null, null));
            case 2 ->
                    JsonUtil.toJson(new location((regionArea.getAreaCode() / 10000) * 10000, (regionArea.getAreaCode() / 100) * 100,
                            regionArea.getAreaCode(), null));
            case 3 ->
                    JsonUtil.toJson(new location((regionArea.getAreaCode() / 10000000) * 10000, (regionArea.getAreaCode() / 100000) * 100,
                            regionArea.getAreaCode() / 1000, regionArea.getAreaCode()));
            default -> null;
        };
    }
}
