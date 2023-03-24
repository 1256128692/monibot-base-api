package cn.shmedo.monitor.monibotbaseapi.cache;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataUnitMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataUnit;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataUnitCache {

    public static List<TbDataUnit> dataUnits;
    public static Map<Integer, TbDataUnit> dataUnitsMap;


    private TbDataUnitMapper tbDataUnitMapper;
    public DataUnitCache(TbDataUnitMapper tbDataUnitMapper) {
        this.tbDataUnitMapper = tbDataUnitMapper;
    }
    @PostConstruct
    void init(){
        dataUnits = tbDataUnitMapper.selectAll();
        dataUnitsMap = dataUnits.stream().collect(Collectors.toMap(TbDataUnit::getID, Function.identity()));
    }
}
