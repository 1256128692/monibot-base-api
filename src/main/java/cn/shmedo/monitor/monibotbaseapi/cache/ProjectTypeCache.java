package cn.shmedo.monitor.monibotbaseapi.cache;

import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
@Component("projectTypeCache")
public class ProjectTypeCache {

    private static List<TbProjectType> projectTypeList;
    public static Map<Byte, TbProjectType> projectTypeMap;


    private final TbProjectTypeMapper tbProjectTypeMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public ProjectTypeCache(TbProjectTypeMapper tbProjectTypeMapper, RedisTemplate<String, String> redisTemplate) {
        this.tbProjectTypeMapper = tbProjectTypeMapper;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    void init() {
        projectTypeList = tbProjectTypeMapper.selectAll();
        projectTypeMap = projectTypeList.stream().collect(Collectors.toMap(TbProjectType::getID, Function.identity()));
        redisTemplate.delete(RedisKeys.PROJECT_TYPE_KEY);
        redisTemplate.opsForHash().putAll(RedisKeys.PROJECT_TYPE_KEY, projectTypeList.stream()
                .collect(Collectors.toMap(k -> k.getID().toString(), JSONUtil::toJsonStr)));
    }
}
