package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.monitor.monibotbaseapi.config.MonitorItemDefaultCheckedConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItemField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorClassType;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorClassInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeAndChildMonitorItemInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtMonitorItemInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.*;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorItemService;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorTypeService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@EnableTransactionManagement
@Service
@AllArgsConstructor
public class MonitorItemServiceImpl implements MonitorItemService {


    private final TbProjectMonitorClassMapper tbProjectMonitorClassMapper;

    private final TbMonitorItemMapper tbMonitorItemMapper;

    private final TbMonitorItemFieldMapper tbMonitorItemFieldMapper;
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    private final MonitorTypeService monitorTypeService;

    @Override
    public WtMonitorItemInfo queryWtMonitorItemList(QueryWtMonitorItemListParam request) {

        WtMonitorItemInfo result = new WtMonitorItemInfo();
        List<MonitorClassInfo> monitorClassList = new LinkedList<>();
        Map<Integer, List<MonitorItemBaseInfo>> monitorClassGroup = new HashMap<>();

        if (request.getMonitorClass() == null) {
            // 查询该工程的全部监测类别,并返回该工程监测类别下的监测类型和监测项目
            LambdaQueryWrapper<TbProjectMonitorClass> wrapper = new LambdaQueryWrapper<TbProjectMonitorClass>()
                    .eq(TbProjectMonitorClass::getProjectID, request.getProjectID());
            // 项目配置监测类别密度信息列表
            List<TbProjectMonitorClass> tbProjectMonitorClassList = tbProjectMonitorClassMapper.selectList(wrapper);
            if (!CollectionUtil.isNullOrEmpty(tbProjectMonitorClassList)) {
                List<Integer> monitorClassIDList = tbProjectMonitorClassList.stream().map(TbProjectMonitorClass::getMonitorClass).collect(Collectors.toList());
                List<MonitorItemBaseInfo> monitorItemBaseInfos = tbMonitorItemMapper.selectListByMonitorClassAndProID(monitorClassIDList, request.getProjectID(), request.getEnable());
                if (!CollectionUtil.isNullOrEmpty(monitorItemBaseInfos)) {
                    // 按照监测类别进行分组
                    monitorClassGroup = monitorItemBaseInfos.stream()
                            .collect(Collectors.groupingBy(MonitorItemBaseInfo::getMonitorClass));
                }
            }

            for (MonitorClassType type : MonitorClassType.values()) {
                MonitorClassInfo vo = new MonitorClassInfo();
                vo.setMonitorClass(type.getValue());
                vo.setMonitorClassCnName(type.getName());

                // monitorClass对应的MonitorItemList列表
                List<MonitorItemBaseInfo> monitorItemBaseInfos = monitorClassGroup.get(type.getValue());
                // 处理监测类型列表
                handleMonitorTypeList(vo, monitorItemBaseInfos);
                monitorClassList.add(vo);
            }
        } else {
            // 查询该工程的全部的监测项目
            MonitorClassType monitorClassType = MonitorClassType.fromInt(request.getMonitorClass());
            MonitorClassInfo vo = new MonitorClassInfo();
            vo.setMonitorClass(monitorClassType.getValue());
            vo.setMonitorClassCnName(monitorClassType.getName());

            List<MonitorItemBaseInfo> monitorItemBaseInfos = tbMonitorItemMapper.selectListByMonitorClassAndProID(null, request.getProjectID(), request.getEnable());
            // 处理监测类型列表
            handleMonitorTypeList(vo, monitorItemBaseInfos);
            monitorClassList.add(vo);
        }

        result.setMonitorClassList(monitorClassList);

        // 处理密度
        handleMonitorClassDensity(request.getProjectID(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMonitorItem(AddMonitorItemParam pa, Integer userID) {
        TbMonitorItem tbMonitorItem = Param2DBEntityUtil.fromAddMonitorItemParam2TbMonitorItem(pa, userID);
        tbMonitorItemMapper.insert(tbMonitorItem);

        tbMonitorItemFieldMapper.insertBatch(tbMonitorItem.getID(), pa.getFieldItemList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitorItem(List<Integer> monitorItemIDList) {
        tbMonitorItemMapper.deleteBatchIds(monitorItemIDList);
        tbMonitorItemFieldMapper.deleteByMonitorItemIDList(monitorItemIDList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMonitorItem(UpdateMonitorItemParam pa, Integer userID) {
        tbMonitorItemMapper.updateByPrimaryKey(pa.update(userID, new Date()));
        if (CollectionUtils.isNotEmpty(pa.getFieldItemList())) {
            tbMonitorItemFieldMapper.deleteByMonitorItemIDList(List.of(pa.getMonitorItemID()));
            tbMonitorItemFieldMapper.insertBatch(pa.getMonitorItemID(), pa.getFieldItemList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCompanyMonitorItem(AddCompanyMonitorItemParam pa, Integer userID) {
        List<TbMonitorItem> tbMonitorItems = tbMonitorItemMapper.selectBatchIds(pa.getMonitorItemIDList());
        List<TbMonitorItemField> tbMonitorItemFields = tbMonitorItemFieldMapper.selectList(
                new QueryWrapper<TbMonitorItemField>().in("MonitorItemID", pa.getMonitorItemIDList())
        );
        Map<Integer, List<TbMonitorItemField>> temp = tbMonitorItemFields.stream().collect(Collectors.groupingBy(TbMonitorItemField::getMonitorItemID));
        Map<TbMonitorItem, List<TbMonitorItemField>> map = new HashMap<>();
        Date now = new Date();
        for (TbMonitorItem tbMonitorItem : tbMonitorItems) {
            map.put(tbMonitorItem, temp.get(tbMonitorItem.getID()));
            tbMonitorItem.setProjectID(-1);
            tbMonitorItem.setCreateTime(now);
            tbMonitorItem.setCreateUserID(userID);
            tbMonitorItem.setUpdateTime(now);
            tbMonitorItem.setUpdateUserID(userID);
            tbMonitorItem.setCreateType(CreateType.PREDEFINED.getType());
        }
        tbMonitorItemMapper.insertBatch(map.keySet());
        map.forEach((key, value) -> {
            value.forEach(item -> {
                item.setMonitorItemID(key.getID());
            });
        });
        tbMonitorItemFieldMapper.insertEntityBatch(map.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }

    @Override
    public PageUtil.Page<MonitorItem4Web> queryMonitorItemPageList(QueryMonitorItemPageListParam pa) {
        Page<MonitorItem4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());

        List<Integer> idList = null;
        if (StringUtils.isNotBlank(pa.getQueryCode())) {
            idList = tbMonitorItemFieldMapper.queryItemListByFieldTokenAndName(null, null, pa.getQueryCode());
            if (CollectionUtils.isEmpty(idList)) {
                return PageUtil.Page.empty();
            }
        }
        IPage<MonitorItem4Web> pageData = tbMonitorItemMapper.queryPage(page, pa.getCompanyID(), pa.getProjectID(), pa.getCreateType(), pa.getQueryCode(), pa.getMonitorType(), idList, pa.getCompanyItem(), pa.getMonitorItemID(), pa.getEnable());
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return PageUtil.Page.empty();
        }
        List<Integer> monitorItemIDList = pageData.getRecords().stream().map(MonitorItem4Web::getID).collect(Collectors.toList());
        List<TbMonitorTypeFieldWithItemID> temp = tbMonitorTypeFieldMapper.queryByMonitorItemIDs(monitorItemIDList);
        Map<Integer, List<TbMonitorTypeFieldWithItemID>> fieldMap = temp.stream().collect(Collectors.groupingBy(TbMonitorTypeFieldWithItemID::getItemID));
        pageData.getRecords().forEach(item -> {
            item.setFieldList(fieldMap.get(item.getID()));
        });
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public List<MonitorItemV1> queryMonitorItemList(QueryMonitorItemListParam pa) {
        List<MonitorItemV1> list = tbMonitorItemMapper.queryMonitorItemV1By(pa.getProjectID(), pa.getMonitorItemName(), pa.getMonitorType(), pa.getEnable());
        if (CollectionUtils.isNotEmpty(list)) {
            List<Integer> monitorItemIDList = list.stream().map(MonitorItemV1::getItemID).collect(Collectors.toList());
            List<MonitorTypeFieldV1> temp = tbMonitorTypeFieldMapper.queryMonitorTypeFieldV1ByMonitorItems(monitorItemIDList);
            Map<Integer, List<MonitorTypeFieldV1>> fieldMap = temp.stream().collect(Collectors.groupingBy(MonitorTypeFieldV1::getItemID));
            list.forEach(item -> {
                item.setFieldList(fieldMap.get(item.getItemID()));

            });
        }
        return list.stream().filter(monitorItemV1 -> Objects.isNull(pa.getEnable()) || pa.getEnable().equals(monitorItemV1.getEnable())).toList();
    }

    @Override
    public List<MonitorItemWithDefaultChecked> querySuperMonitorItemList(QuerySuperMonitorItemListParam pa) {
        LambdaQueryWrapper<TbMonitorItem> queryWrapper = new QueryWrapper<TbMonitorItem>().lambda();
        if (pa.getCreateType() != null) {
            queryWrapper.eq(TbMonitorItem::getCreateType, pa.getCreateType());
        }
        if (pa.getCompanyID() != null) {
            if (pa.getContainPredefine() != null && pa.getContainPredefine()) {
                queryWrapper.and(wrapper ->
                        wrapper.eq(TbMonitorItem::getCompanyID, pa.getCompanyID()).or().eq(TbMonitorItem::getCompanyID, -1));
            } else {
                queryWrapper.eq(TbMonitorItem::getCompanyID, pa.getCompanyID());
            }
        }
        if (pa.getProjectID() != null) {
            queryWrapper.eq(TbMonitorItem::getProjectID, pa.getProjectID());
        }
        if (pa.getProjectType() != null) {
            queryWrapper.eq(TbMonitorItem::getProjectType, pa.getProjectType());
        }
        if (pa.getEnable() != null) {
            queryWrapper.eq(TbMonitorItem::getEnable, pa.getEnable());
        }
        queryWrapper.orderByDesc(TbMonitorItem::getID);
        Optional.ofNullable(pa.getKeyword()).filter(e -> !e.isBlank()).ifPresent(e -> queryWrapper
                .and(wrapper -> wrapper.like(TbMonitorItem::getAlias, e).or().like(TbMonitorItem::getName, e)));
        List<TbMonitorItem> tbMonitorItems = tbMonitorItemMapper.selectList(
                queryWrapper
        );
        List<MonitorItemWithDefaultChecked> list = tbMonitorItems.stream().map(
                e -> {
                    MonitorItemWithDefaultChecked obj = BeanUtil.copyProperties(e, MonitorItemWithDefaultChecked.class);
                    if (obj.getCreateType().equals(CreateType.PREDEFINED.getType()) && MonitorItemDefaultCheckedConfig.idDefault(pa.getProjectType(), e.getName())) {
                        obj.setDefaultChecked(true);
                    } else {
                        obj.setDefaultChecked(false);
                    }
                    return obj;
                }
        ).toList();
        return list;
    }

    @Override
    public List<CompanyMonitorItemNameInfo> queryMonitorItemNameList(QueryWorkOrderStatisticsParam pa) {
        Map<String, List<MonitorItemNameFullInfo>> monitorClassMap = tbMonitorItemMapper.queryMonitorItemNameFullInfo(
                pa).stream().collect(Collectors.groupingBy(MonitorItemNameFullInfo::getMonitorClassName));
        return monitorClassMap.entrySet().stream().map(u -> {
            Map<Integer, List<MonitorItemNameFullInfo>> monitorTypemap = u.getValue().stream().collect(
                    Collectors.groupingBy(MonitorItemNameFullInfo::getMonitorTypeID));
            return CompanyMonitorItemNameInfo.builder().MonitorClassName(u.getKey()).dataList(monitorTypemap.entrySet()
                    .stream().map(w -> {
                        MonitorItemNameFullInfo info = w.getValue().stream().findFirst()
                                .orElseThrow(() -> new RuntimeException("Unreachable RuntimeException"));
                        return MonitorTypeItemNameInfo.builder().monitorTypeID(w.getKey())
                                .displayOrder(info.getDisplayOrder()).multiSensor(info.getMultiSensor())
                                .monitorTypeName(info.getMonitorTypeName())
                                .monitorItemNameList(w.getValue().stream().map(MonitorItemNameFullInfo::getMonitorItemName)
                                        .distinct().toList()).build();
                    }).sorted(Comparator.comparing(MonitorTypeItemNameInfo::getDisplayOrder, Comparator.nullsLast(Integer::compareTo)))
                    .toList()).build();
        }).toList();
    }

    @Override
    public List<MonitorItemSimple> listMonitorItemSimple(QueryMonitorItemSimpleListParam param) {
        LambdaQueryWrapper<TbMonitorItem> query = Wrappers.<TbMonitorItem>lambdaQuery()
                .eq(TbMonitorItem::getProjectID, param.getProjectID())
                .eq(TbMonitorItem::getEnable, param.getEnable());
        Optional.ofNullable(param.getMonitorType()).ifPresent(e -> query.eq(TbMonitorItem::getMonitorType, e));
        Optional.ofNullable(param.getCreateType()).ifPresent(e -> query.eq(TbMonitorItem::getCreateType, e));

        return tbMonitorItemMapper.selectList(query.select(TbMonitorItem::getID, TbMonitorItem::getName,
                                TbMonitorItem::getAlias, TbMonitorItem::getProjectID, TbMonitorItem::getCreateType,
                                TbMonitorItem::getMonitorType, TbMonitorItem::getEnable)
                        .orderByDesc(TbMonitorItem::getID))
                .stream().map(MonitorItemSimple::valueOf).toList();
    }

    private void handleMonitorClassDensity(Integer projectID, WtMonitorItemInfo result) {

        List<Integer> monitorClassIDList = result.getMonitorClassList().stream().map(MonitorClassInfo::getMonitorClass).collect(Collectors.toList());
        LambdaQueryWrapper<TbProjectMonitorClass> wrapper = new LambdaQueryWrapper<TbProjectMonitorClass>()
                .eq(TbProjectMonitorClass::getProjectID, projectID)
                .in(TbProjectMonitorClass::getMonitorClass, monitorClassIDList);
        // 项目配置监测类别密度信息列表
        List<TbProjectMonitorClass> tbProjectMonitorClassList = tbProjectMonitorClassMapper.selectList(wrapper);

        result.getMonitorClassList().stream().forEach(item -> {
            if (!CollectionUtil.isNullOrEmpty(tbProjectMonitorClassList)) {
                TbProjectMonitorClass vo = tbProjectMonitorClassList.stream().filter(pojo -> pojo.getMonitorClass().equals(item.getMonitorClass())).findFirst().orElse(null);
                if (vo != null) {
                    item.setDensity(vo.getDensity());
                    item.setEnable(vo.getEnable());
                }
            }
        });
    }


    /**
     * 处理监测类型列表
     *
     * @param vo
     * @param monitorItemBaseInfos
     */
    private void handleMonitorTypeList(MonitorClassInfo vo, List<MonitorItemBaseInfo> monitorItemBaseInfos) {
        Map<String, MonitorTypeCacheData> monitorTypeMap = monitorTypeService.queryMonitorTypeMap();

        if (!CollectionUtil.isNullOrEmpty(monitorItemBaseInfos)) {
            Map<Integer, List<MonitorItemBaseInfo>> monitorTypeGroup = monitorItemBaseInfos.stream()
                    .collect(Collectors.groupingBy(MonitorItemBaseInfo::getMonitorType));

            List<MonitorTypeAndChildMonitorItemInfo> monitorTypeAndChildMonitorItemInfos = new LinkedList<>();
            // 遍历monitorTypeGroup分组,封装回返回对象中
            for (Map.Entry<Integer, List<MonitorItemBaseInfo>> entry : monitorTypeGroup.entrySet()) {
                List<MonitorItemBaseInfo> list = entry.getValue();
                MonitorTypeCacheData tbMonitorType = monitorTypeMap.get(String.valueOf(entry.getKey()));
                MonitorTypeAndChildMonitorItemInfo monitorTypeVo = new MonitorTypeAndChildMonitorItemInfo();
                monitorTypeVo.setMonitorType(tbMonitorType.getMonitorType());
                monitorTypeVo.setMonitorTypeName(tbMonitorType.getTypeName());
                monitorTypeVo.setMonitorTypeAlias(tbMonitorType.getTypeAlias());
                monitorTypeVo.setMonitorItemList(list);
                monitorTypeAndChildMonitorItemInfos.add(monitorTypeVo);
            }
            vo.setMonitorTypeList(monitorTypeAndChildMonitorItemInfos);
        }
    }


}
