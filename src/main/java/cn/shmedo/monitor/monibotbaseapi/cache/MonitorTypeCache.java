package cn.shmedo.monitor.monibotbaseapi.cache;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 17:14
 **/
@Component
public class MonitorTypeCache {

    private static List<TbMonitorType> monitorTypeList;
    public static Map<Integer, TbMonitorType> monitorTypeMap;


    private TbMonitorTypeMapper tbMonitorTypeMapper;
    public MonitorTypeCache(TbMonitorTypeMapper tbMonitorTypeMapper) {
        this.tbMonitorTypeMapper = tbMonitorTypeMapper;
    }
    @PostConstruct
    void init(){
        monitorTypeList = tbMonitorTypeMapper.selectAll();
        monitorTypeMap = monitorTypeList.stream().collect(Collectors.toMap(TbMonitorType::getMonitorType, Function.identity()));
    }
}
