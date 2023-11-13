package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-09 15:05
 */
public interface IDataDisplayCheck {
    Integer getStatisticalMethod();

    Integer getDisplayDensity();

    List<Integer> getInspectedPointIDList();

    void setTbMonitorPointList(final List<TbMonitorPoint> tbMonitorPointList);

    /**
     * 校验显示密度{@code displayDensity},统计方式{@code StatisticalMethods}和待检查的监测点IDList{@code inspectedPointIDList}。<br>
     * 同时会校验这些待检查的监测点是否存在。
     * <p>
     * 显示密度{@code displayDensity}和统计方式{@code StatisticalMethods}配置在monitorType的ExValues字段里，形如{"displayDensity":[1,2,3],"StatisticalMethods":[1,2,3]};<br>
     * 显示密度:全部0,小时1,日2,周3,月4,年5，默认显示密度[0,1,2,3,4,5];<br>
     * 统计方式:1.最新一条(不做存储),2.平均值(tb_11_data_avg),3.阶段累计(tb_11_data_sum),4.阶段变化(tb_11_data_diff)，默认统计方式最新一条;<br>
     * </p>
     *
     * @throws JSONException            JSON内容不合法解析失败
     * @throws IllegalArgumentException 部分监测点不存在
     */
    default boolean valid() throws JSONException, IllegalArgumentException {
        final List<Integer> pointIDList = getInspectedPointIDList();
        if (CollUtil.isEmpty(pointIDList)) {
            return false;
        }
        final Integer displayDensity = getDisplayDensity();
        final Integer statisticalMethod = getStatisticalMethod();
        List<TbMonitorPoint> pointList = ContextHolder.getBean(TbMonitorPointMapper.class).selectList(
                new LambdaQueryWrapper<TbMonitorPoint>().in(TbMonitorPoint::getID, pointIDList));
        if (pointList.size() != pointIDList.size()) {
            throw new IllegalArgumentException("有部分监测点不存在!");
        }
        setTbMonitorPointList(pointList);
        Set<String> exValuesList = ContextHolder.getBean(TbMonitorTypeMapper.class).selectList(
                        new LambdaQueryWrapper<TbMonitorType>().in(TbMonitorType::getMonitorType, pointList.stream()
                                .map(TbMonitorPoint::getMonitorType).collect(Collectors.toSet()))).stream()
                .map(TbMonitorType::getExValues).collect(Collectors.toSet());
        // Stream#allMath always return {@code true} while the collection is empty.
        return exValuesList.stream().filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj).allMatch(u ->
                (!u.containsKey(DbConstant.DISPLAY_DENSITY) || (u.containsKey(DbConstant.DISPLAY_DENSITY)
                        && JSONUtil.parseArray(u.get(DbConstant.DISPLAY_DENSITY)).contains(displayDensity)))
                        && (!u.containsKey(DbConstant.STATISTICAL_METHODS) || (u.containsKey(DbConstant.STATISTICAL_METHODS)
                        && JSONUtil.parseArray(u.get(DbConstant.STATISTICAL_METHODS)).contains(statisticalMethod))));
    }
}
