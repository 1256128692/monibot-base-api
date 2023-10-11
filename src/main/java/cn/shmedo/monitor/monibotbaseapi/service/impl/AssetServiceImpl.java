package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetHouseMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbAssetMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetHouse;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AssetType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AssetUnit;
import cn.shmedo.monitor.monibotbaseapi.model.param.asset.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.asset.TbAsset4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.asset.TbAssetLog4Web;
import cn.shmedo.monitor.monibotbaseapi.service.IAssetService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 17:09
 **/
@Service
@AllArgsConstructor
public class AssetServiceImpl extends ServiceImpl<TbAssetMapper, TbAsset> implements IAssetService {
    private final TbAssetHouseMapper tbAssetHouseMapper;
    private final TbAssetLogMapper tbAssetLogMapper;
    private final RedisService redisService;

    @Override
    public void addAssetHouse(AddAssetHouseParam pa, Integer subjectID) {
        tbAssetHouseMapper.insert(pa.toEntity(subjectID));
    }

    @Override
    public void updateAssetHouse(UpdateAssetHouseParam pa, Integer subjectID) {
        tbAssetHouseMapper.updateById(pa.update(subjectID));
    }

    @Override
    public void deleteAssetHouse(List<Integer> houseIDList) {
        tbAssetHouseMapper.deleteBatchIds(houseIDList);
    }

    @Override
    public List<TbAssetHouse> queryAssetHouseList(Integer companyID) {
        return tbAssetHouseMapper.selectList(new LambdaQueryWrapper<TbAssetHouse>()
                .eq(TbAssetHouse::getCompanyID, companyID)
                .orderByDesc(TbAssetHouse::getID));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ioAsset(IOAssetParam pa, CurrentSubject currentSubject) {

        //操作数据库
        tbAssetLogMapper.insert(pa.toEntity(currentSubject));
        //操作缓存
        HashOperations<String, Object, Object> hashOperations = redisService.getTemplate().opsForHash();
        Map map = redisService.get(RedisKeys.ASSET_HOUSE_KEY, pa.getHouseID().toString(), Map.class);
        if (map == null) {
            map = Map.of(pa.getAssetID().toString(), pa.getValue());
        } else {
            if (map.containsKey(pa.getAssetID().toString())) {
                Integer value = (Integer) map.get(pa.getAssetID().toString());
                Integer i = value + pa.getValue();
                if (i == 0) {
                    map.remove(pa.getAssetID().toString());
                }
                map.put(pa.getAssetID().toString(), i);
            } else {
                map.put(pa.getAssetID().toString(), pa.getValue());
            }

        }
        hashOperations.put(RedisKeys.ASSET_HOUSE_KEY
                , pa.getHouseID().toString()
                , JsonUtil.toJson(map));
    }

    @Override
    public PageUtil.Page<TbAssetLog4Web> queryAssetIOLogPage(QueryAssetIOLogPageParam pa) {
        Page<TbAssetLog4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<TbAssetLog4Web> pageData = tbAssetLogMapper.queryAssetIOLogPage(page, pa);
        pageData.getRecords().forEach(e -> {
            e.setAssetTypeStr(AssetType.getStrByCode(e.getAssetType()));
            e.setAssetUnitStr(AssetUnit.getStrByCode(e.getAssetUnit()));
        });
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public PageUtil.Page<TbAsset4Web> queryAssetWithValuePage(QueryAssetWithValuePageParam pa) {
        List<TbAsset> assets;
        if (pa.getAssetID() != null) {
            TbAsset tbAsset = this.getById(pa.getAssetID());
            assets = new ArrayList<>();
            assets.add(tbAsset);
        } else {
            LambdaQueryChainWrapper<TbAsset> lambdaQuery = this.lambdaQuery();
            lambdaQuery.eq(TbAsset::getCompanyID, pa.getCompanyID());
            if (pa.getType() != null) {
                lambdaQuery.eq(TbAsset::getType, pa.getType());
            }

            if (pa.getFuzzyItem() != null) {
                lambdaQuery.and(
                        e -> {
                            e.like(TbAsset::getName, pa.getFuzzyItem());
                            e.or();
                            e.like(TbAsset::getVendor, pa.getFuzzyItem());
                        }
                );
            }
            assets = lambdaQuery.list();
        }

        if (ObjectUtil.isEmpty(assets)) {
            return new PageUtil.Page<>(0, null, 0);
        }
        // 获取缓存
        Map<String, Map> all = redisService.getAll(RedisKeys.ASSET_HOUSE_KEY, String.class, Map.class);
        List<TbAsset4Web> resultAll;
        if (pa.getHouseID() != null) {
            Map map = all.get(pa.getHouseID().toString());
            TbAssetHouse tbAssetHouse = tbAssetHouseMapper.selectById(pa.getHouseID());
            resultAll = assets.stream().filter(e -> map.containsKey(e.getID().toString())).map(
                    e -> {
                        TbAsset4Web tbAsset4Web = BeanUtil.copyProperties(e, TbAsset4Web.class);
                        tbAsset4Web.setHouseName(tbAssetHouse.getName());
                        tbAsset4Web.setHouseID(tbAssetHouse.getID());
                        tbAsset4Web.setCurrentValue((Integer) map.get(e.getID().toString()));
                        tbAsset4Web.setIsWarn(isWarn(tbAsset4Web));
                        return tbAsset4Web;
                    }
            ).toList();
        } else {

            List<TbAssetHouse> tbAssetHouses = tbAssetHouseMapper.selectList(new LambdaQueryWrapper<TbAssetHouse>()
                    .eq(TbAssetHouse::getCompanyID, pa.getCompanyID()));
            Map<Integer, TbAssetHouse> assetHouseMap = tbAssetHouses.stream().collect(Collectors.toMap(TbAssetHouse::getID, Function.identity()));
            List<TbAsset4Web> finalResultAll = new ArrayList<>();
            all.forEach(
                    (houseIDStr, aMap) -> {
                        TbAssetHouse tbAssetHouse = assetHouseMap.get(Integer.valueOf(houseIDStr));
                        finalResultAll.addAll(
                                assets.stream().filter(e -> aMap.containsKey(e.getID().toString())).map(
                                        e -> {
                                            TbAsset4Web tbAsset4Web = BeanUtil.copyProperties(e, TbAsset4Web.class);
                                            tbAsset4Web.setHouseName(tbAssetHouse.getName());
                                            tbAsset4Web.setHouseID(tbAssetHouse.getID());
                                            tbAsset4Web.setCurrentValue((Integer) aMap.get(e.getID().toString()));
                                            tbAsset4Web.setIsWarn(isWarn(tbAsset4Web));
                                            return tbAsset4Web;
                                        }
                                ).toList()
                        );
                    }

            );
            resultAll = finalResultAll;
        }
        if (pa.getIsWarn() != null) {
            resultAll = resultAll.stream().filter(e -> e.getIsWarn().equals(pa.getIsWarn())).toList();
        }
        resultAll = resultAll.stream().sorted(
                Comparator.comparing(TbAsset4Web::getID).reversed()
                        .thenComparing(TbAsset4Web::getHouseID).reversed()
        ).toList();
        return PageUtil.page(resultAll, pa.getPageSize(), pa.getCurrentPage());
    }

    @Override
    public PageUtil.Page<TbAssetHouse> queryAssetHousePage(QueryAssetHousePageParam pa) {
        Page<TbAssetHouse> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<TbAssetHouse> pageData = tbAssetHouseMapper.queryPage(page, pa);
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public PageUtil.Page<TbAsset> queryAssetPage(QueryAssetPageParam pa) {
        Page<TbAsset> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<TbAsset> pageData = this.baseMapper.queryPage(page, pa);
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    /**
     * 比较是否超限
     *
     * @param tbAsset4Web
     * @return
     */
    private Boolean isWarn(TbAsset4Web tbAsset4Web) {
        if (tbAsset4Web.getWarnValue() == null || ObjectUtil.isEmpty(tbAsset4Web.getComparison())) {
            return false;
        }
        switch (tbAsset4Web.getComparison()) {
            case ">":
                return tbAsset4Web.getCurrentValue() > tbAsset4Web.getWarnValue();
            case "<":
                return tbAsset4Web.getCurrentValue() < tbAsset4Web.getWarnValue();
            case ">=":
                return tbAsset4Web.getCurrentValue() >= tbAsset4Web.getWarnValue();
            case "<=":
                return tbAsset4Web.getCurrentValue() <= tbAsset4Web.getWarnValue();
            case "=":
                return tbAsset4Web.getCurrentValue().equals(tbAsset4Web.getWarnValue());
            default:
                return false;
        }
    }
}
