package cn.shmedo.monitor.monibotbaseapi.cache;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.response.Model4Web;
import cn.shmedo.monitor.monibotbaseapi.util.CustomizeBeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author wuxl
 * @Date 2023/10/11 15:11
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.cache
 * @ClassName: FormModelCache
 * @Description: TODO
 * @Version 1.0
 */
@Component
public class FormModelCache {
    private final TbPropertyModelMapper tbPropertyModelMapper;
    private final TbPropertyMapper tbPropertyMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public FormModelCache(TbPropertyModelMapper tbPropertyModelMapper,
                          TbPropertyMapper tbPropertyMapper,
                          RedisTemplate<String, String> redisTemplate) {
        this.tbPropertyModelMapper = tbPropertyModelMapper;
        this.tbPropertyMapper = tbPropertyMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 初始化表单缓存（工程项目、设备、工作流）
     */
    @PostConstruct
    public void init() {
        List<TbPropertyModel> tbPropertyModelList = tbPropertyModelMapper.selectList(new QueryWrapper<>());
        List<TbProperty> tbPropertyList = tbPropertyMapper.selectList(new QueryWrapper<>());
        putBatch(tbPropertyModelList, tbPropertyList);
    }

    /**
     * 数据转换
     *
     * @param tbPropertyModelList 模板列表
     * @param tbPropertyList      属性列表
     * @return Map<String, String>
     */
    public Map<String, String> wrapperModelData(List<TbPropertyModel> tbPropertyModelList, List<TbProperty> tbPropertyList) {
        List<Model4Web> formModelDataList = CustomizeBeanUtil.copyToList(tbPropertyModelList, Model4Web.class);
        Map<Integer, List<TbProperty>> propertyGroup = tbPropertyList.stream().collect(Collectors.groupingBy(TbProperty::getModelID));
        return formModelDataList.stream().collect(Collectors.toMap((Model4Web model1) ->
                        String.valueOf(model1.getID()),
                model2 -> {
                    model2.setPropertyList(propertyGroup.get(model2.getID()));
                    return JSONUtil.toJsonStr(model2);
                }));
    }

    /**
     * 批量更新缓存
     *
     * @param tbPropertyModelList 模板列表
     * @param tbPropertyList      属性列表
     * @return Map<String, String>
     */
    public void putBatch(List<TbPropertyModel> tbPropertyModelList, List<TbProperty> tbPropertyList) {
        Assert.notEmpty(tbPropertyModelList, "更新缓存-模板列表不能为空");
        Assert.notEmpty(tbPropertyList, "更新缓存-属性列表不能为空");
        Map<String, String> modelDataMap = wrapperModelData(tbPropertyModelList, tbPropertyList);
        redisTemplate.opsForHash().putAll(RedisKeys.FORM_MODEL_KEY, modelDataMap);
    }

    /**
     * todo 考虑缓存一致性
     * 删除表单时，同步缓存
     *
     * @param modelIds 模板ID列表
     */
    public void removeBatch(Collection<Integer> modelIds) {
        Assert.notEmpty(modelIds, "模板ID列表不能为空");
        List<String> modelIdList = modelIds.stream().map(String::valueOf).toList();
        redisTemplate.opsForHash().delete(RedisKeys.FORM_MODEL_KEY, modelIdList.toArray(new Object[modelIds.size()]));
    }

}
