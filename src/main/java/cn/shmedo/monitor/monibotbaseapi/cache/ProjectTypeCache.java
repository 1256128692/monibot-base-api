package cn.shmedo.monitor.monibotbaseapi.cache;

import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import org.springframework.stereotype.Component;

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
public class ProjectTypeCache {
    private static List<TbProjectType> projectTypeList;
    public static Map<Integer, TbProjectType> projectTypeMap;
    public ProjectTypeCache() {
        init();
    }

    void init(){
        projectTypeList = ContextHolder.getBean(TbProjectTypeMapper.class).selectAll();
        projectTypeMap = projectTypeList.stream().collect(Collectors.toMap(TbProjectType::getID, Function.identity()));
    }
}
