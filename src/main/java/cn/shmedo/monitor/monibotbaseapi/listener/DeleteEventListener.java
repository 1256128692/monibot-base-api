package cn.shmedo.monitor.monibotbaseapi.listener;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogHistoryMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbNotifyRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLogHistory;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbNotifyRelation;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.DeleteSensorEventDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataDeviceWarnType;
import cn.shmedo.monitor.monibotbaseapi.service.notify.NotifyService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 删除事件监听器,目前只监听了<b>删除监测传感器</b>事件,并清空传感器对应的阈值配置和报警记录
 *
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-18 13:58
 */
@Configuration
@RequiredArgsConstructor
public class DeleteEventListener {
    @SuppressWarnings("all")
    @Resource(name = RedisConstant.MONITOR_REDIS_SERVICE)
    private final RedisService monitorRedisService;
    private final NotifyService notifyService;
    private final TbWarnThresholdConfigMapper tbWarnThresholdConfigMapper;
    private final TbDataWarnLogMapper tbDataWarnLogMapper;
    private final TbDataWarnLogHistoryMapper tbDataWarnLogHistoryMapper;
    private final TbNotifyRelationMapper tbNotifyRelationMapper;

    /**
     * 监听<b>删除监测传感器</b>事件
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void listenDeleteSensorEvent(DeleteSensorEventDto dto) {
        clearDataWarn(dto);
    }

    /**
     * 清空传感器对应的报警配置、报警记录和通知消息<br>
     * 目前这里只有3个动作,如果需要添加更多的动作,建议修改成pipeline的形式
     */
    private void clearDataWarn(final DeleteSensorEventDto dto) {
        List<Integer> thresholdConfigIDList = tbWarnThresholdConfigMapper.selectList(
                        new LambdaQueryWrapper<TbWarnThresholdConfig>().in(TbWarnThresholdConfig::getSensorID, dto.getSensorIDList()))
                .stream().map(TbWarnThresholdConfig::getId).toList();
        if (CollUtil.isNotEmpty(thresholdConfigIDList)) {
            clearDataWarnConfig(thresholdConfigIDList);
            List<Integer> dataWarnLogIDList = tbDataWarnLogMapper.selectList(new LambdaQueryWrapper<TbDataWarnLog>()
                    .in(TbDataWarnLog::getWarnThresholdID, thresholdConfigIDList)).stream().map(TbDataWarnLog::getId).toList();
            if (CollUtil.isNotEmpty(dataWarnLogIDList)) {
                clearDataWarnLog(dataWarnLogIDList);
                List<Integer> notifyIDList = tbNotifyRelationMapper.selectList(new LambdaQueryWrapper<TbNotifyRelation>()
                        .eq(TbNotifyRelation::getType, DataDeviceWarnType.DATA.getCode())
                        .in(TbNotifyRelation::getRelationID, dataWarnLogIDList)).stream().map(TbNotifyRelation::getNotifyID).toList();
                if (CollUtil.isNotEmpty(notifyIDList)) {
                    notifyService.clearNotify(notifyIDList, dto.getAppend());
                }
            }
        }
    }

    /**
     * 清空对应传感器的报警阈值配置(包括缓存)
     *
     * @param thresholdConfigIDList 阈值IDList
     */
    private void clearDataWarnConfig(final List<Integer> thresholdConfigIDList) {
        // 阈值配置缓存
        List<String> thresholdCacheKeyList = thresholdConfigIDList.stream().map(u -> RedisKeys.WARN_THRESHOLD + u).toList();
        // 沉默周期缓存
        List<String> silenceCycleCacheKeyList = thresholdConfigIDList.stream().map(u -> RedisKeys.WARN_SILENCE_CYCLE + u).toList();
        monitorRedisService.del(CollUtil.union(thresholdCacheKeyList, silenceCycleCacheKeyList));
        // 数据库阈值配置
        tbWarnThresholdConfigMapper.deleteBatchIds(thresholdConfigIDList);
    }

    /**
     * 清空对应传感器的报警记录
     *
     * @param thresholdConfigIDList 阈值IDList
     */
    private void clearDataWarnLog(final List<Integer> dataWarnLogIDList) {
        // 清空数据报警变化历史
        tbDataWarnLogHistoryMapper.delete(new LambdaQueryWrapper<TbDataWarnLogHistory>().in(TbDataWarnLogHistory::getWarnLogID, dataWarnLogIDList));
        // 清空数据报警
        tbDataWarnLogMapper.deleteBatchIds(dataWarnLogIDList);
    }
}
