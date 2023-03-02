package cn.shmedo.monitor.monibotbaseapi.cache;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 预定义模板属性缓存
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-02 13:26
 **/
@Component
public class PredefinedModelProperTyCache {
    private static List<TbProperty> propertyList;
    public static Map<Byte, List<TbProperty>> projectTypeAndPropertyListMap;


    private TbPropertyMapper tbPropertyMapper;
    @Autowired
    public PredefinedModelProperTyCache(TbPropertyMapper tbPropertyMapper) {
        this.tbPropertyMapper = tbPropertyMapper;
    }

    @PostConstruct
    void init(){
        propertyList = tbPropertyMapper.queryByCreateType(CreateType.PREDEFINED.getType());
        projectTypeAndPropertyListMap = propertyList.stream().collect(Collectors.groupingBy(TbProperty::getProjectType));
    }
}
