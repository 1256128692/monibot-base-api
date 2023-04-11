package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.cache.MonitorTypeCache;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorClassType;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItem4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorTypeFieldV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.TbMonitorTypeFieldWithItemID;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorItemService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
                List<MonitorItemBaseInfo> monitorItemBaseInfos = tbMonitorItemMapper.selectListByMonitorClassAndProID(monitorClassIDList, request.getProjectID());
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

            List<MonitorItemBaseInfo> monitorItemBaseInfos = tbMonitorItemMapper.selectListByMonitorClassAndProID(null, request.getProjectID());
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

        tbMonitorItemFieldMapper.insertBatch(tbMonitorItem.getID(), pa.getFieldIDList());
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
        if (CollectionUtils.isNotEmpty(pa.getFieldIDList())) {
            tbMonitorItemFieldMapper.deleteByMonitorItemIDList(List.of(pa.getMonitorItemID()));
            tbMonitorItemFieldMapper.insertBatch(pa.getMonitorItemID(), pa.getFieldIDList());
        }
    }

    @Override
    public void addCompanyMonitorItem(AddCompanyMonitorItemParam pa, Integer userID) {
        tbMonitorItemMapper.updateProjectIDBatch(
                pa.getMonitorItemIDList(), -1, userID, new Date()
        );
    }

    @Override
    public PageUtil.Page<MonitorItem4Web> queryMonitorItemPageList(QueryMonitorItemPageListParam pa) {
        Page<MonitorItem4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());

        List<Integer> idList = null;
        if (StringUtils.isNotBlank(pa.getFieldName()) || StringUtils.isNotBlank(pa.getFieldToken())) {
            idList = tbMonitorItemFieldMapper.queryItemListByFieldTokenAndName(pa.getFieldName(), pa.getFieldToken());
            if (CollectionUtils.isEmpty(idList)){
                return PageUtil.Page.empty();

            }
        }
        IPage<MonitorItem4Web> pageData = tbMonitorItemMapper.queryPage(page, pa.getProjectID(), pa.getCreateType(), pa.getMonitorItemName(), pa.getMonitorType(), idList);
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
        List<MonitorItemV1> list= tbMonitorItemMapper.queryMonitorItemV1By(pa.getProjectID(), pa.getMonitorItemName(), pa.getMonitorType());
        if (CollectionUtils.isNotEmpty(list)){
            List<Integer> monitorItemIDList = list.stream().map(MonitorItemV1::getItemID).collect(Collectors.toList());
            List<MonitorTypeFieldV1> temp = tbMonitorTypeFieldMapper.queryMonitorTypeFieldV1ByMonitorItems(monitorItemIDList);
            Map<Integer, List<MonitorTypeFieldV1>> fieldMap = temp.stream().collect(Collectors.groupingBy(MonitorTypeFieldV1::getItemID));
            list.forEach(item -> {
                item.setFieldList(fieldMap.get(item.getItemID()));

            });
        }
        return list;
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
        Map<Integer, TbMonitorType> monitorTypeMap = MonitorTypeCache.monitorTypeMap;

        if (!CollectionUtil.isNullOrEmpty(monitorItemBaseInfos)) {
            Map<Integer, List<MonitorItemBaseInfo>> monitorTypeGroup = monitorItemBaseInfos.stream()
                    .collect(Collectors.groupingBy(MonitorItemBaseInfo::getMonitorType));

            List<MonitorTypeAndChildMonitorItemInfo> monitorTypeAndChildMonitorItemInfos = new LinkedList<>();
            // 遍历monitorTypeGroup分组,封装回返回对象中
            for (Map.Entry<Integer, List<MonitorItemBaseInfo>> entry : monitorTypeGroup.entrySet()) {
                List<MonitorItemBaseInfo> list = entry.getValue();
                TbMonitorType tbMonitorType = monitorTypeMap.get(entry.getKey());
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
