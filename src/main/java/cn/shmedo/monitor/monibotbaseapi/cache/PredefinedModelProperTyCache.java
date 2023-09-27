package cn.shmedo.monitor.monibotbaseapi.cache;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预定义模板属性缓存
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-02 13:26
 **/
@Component
public class PredefinedModelProperTyCache {
    public static Map<Byte, List<TbProperty>> projectTypeAndPropertyListMap;

    private TbPropertyMapper tbPropertyMapper;
    public PredefinedModelProperTyCache(TbPropertyMapper tbPropertyMapper) {
        this.tbPropertyMapper = tbPropertyMapper;
    }

    @PostConstruct
    void init(){
        List<TbProperty> propertyList = tbPropertyMapper.queryByCreateType(CreateType.PREDEFINED.getType());
        // 由于之前设计，projectType字段类型为Byte，项目类型最多只能有128种，因此，这里projectTypeAndPropertyListMap只缓存项目模板
        projectTypeAndPropertyListMap = propertyList.stream()
                .filter(item -> item.getGroupID() <= Byte.MAX_VALUE)
                .collect(Collectors.groupingBy(
                        property -> Byte.valueOf(String.valueOf(property.getGroupID()))
                ));
    }
}
