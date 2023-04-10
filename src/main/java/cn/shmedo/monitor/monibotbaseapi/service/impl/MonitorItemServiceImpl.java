package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.cache.MonitorTypeCache;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectMonitorClassMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorClassType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitoringItem;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem.QueryWtMonitorItemListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorClassInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorItemBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeAndChildMonitorItemInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtMonitorItemInfo;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorItemService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@EnableTransactionManagement
@Service
@AllArgsConstructor
public class MonitorItemServiceImpl implements MonitorItemService {

    private final TbProjectInfoMapper tbProjectInfoMapper;

    private final TbProjectMonitorClassMapper tbProjectMonitorClassMapper;

    private final TbMonitorItemMapper tbMonitorItemMapper;

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
