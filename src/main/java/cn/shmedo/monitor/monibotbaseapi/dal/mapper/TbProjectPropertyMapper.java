package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropWithValue;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropertyDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertySubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.QueryPropertyValueParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import reactor.util.function.Tuple3;

import java.util.Collection;
import java.util.List;

public interface TbProjectPropertyMapper extends BaseMapper<TbProjectProperty> {
    void updateBatch(Integer projectID, List<TbProjectProperty> projectPropertyList, Integer propertySubjectType);

    void insertBatch(List<TbProjectProperty> projectPropertyList);

    List<String> getPropertyValue(QueryPropertyValueParam param);

    List<PropertyDto> queryPropertyByProjectID(@Param("list") List<Integer> list,
                                               @Param("createType") Integer createType,
                                               @Param("propertySubjectType") Integer propertySubjectType
    );

    int deleteProjectPropertyList(List idList, Integer propertySubjectType);

    /**
     * 查询包含指定属性的项目id集合 <br/>
     * <br/>
     * 例：查询 水库规模 或 工程等别 包含'Ⅱ'，且 工程情况 为'在建'的项目id集合<br/>
     * <pre>
     * Tuple3<Collection<String>, String, Boolean> t1 = Tuples.of(List.of("水库规模", "工程等别"), "Ⅱ", Boolean.TRUE);
     * Tuple3<Collection<String>, String, Boolean> t2 = Tuples.of(List.of("工程情况"), "在建", Boolean.FALSE);
     * List<Integer> result = tbProjectPropertyMapper.queryPidByProps(List.of(), PropertySubjectType.Project, List.of(t1, t2));
     * </pre>
     *
     * @param projectIDList 项目id集合
     * @param subjectType   {@link PropertySubjectType}
     * @param params        属性集合 {@link Tuple3} 第一个参数为属性名集合，第二个参数为属性值，第三个参数为是否模糊匹配
     * @return 结果, 不存在重复值
     */
    List<Integer> queryPidByProps(@Param("projectIDList") Collection<Integer> projectIDList,
                                  @Param("subjectType") PropertySubjectType subjectType,
                                  @Param("props") Collection<Tuple3<Collection<String>, String, Boolean>> params);

    /**
     * 查询指定项目的指定属性值
     *
     * @param projectIDList 项目id集合
     * @param subjectType   {@link PropertySubjectType}
     * @param propNames     属性名集合
     * @return 结果
     */
    List<PropWithValue> queryPropByPids(@Param("projectIDList") Collection<Integer> projectIDList,
                                        @Param("subjectType") PropertySubjectType subjectType,
                                        @Param("propNames") Collection<String> propNames);
}