package cn.shmedo.monitor.monibotbaseapi.listener;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDeviceWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbNotifyRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbNotifyRelation;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.UpdateDeviceGroupSenderDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataDeviceWarnType;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.UpdateDeviceGroupSenderEventParam;
import cn.shmedo.monitor.monibotbaseapi.service.notify.NotifyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 更新事件监听器,目前只监听了<b>更新设备数据推送</b>事件
 *
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-18 14:44
 */
@Configuration
@RequiredArgsConstructor
public class UpdateEventListener {
    private final NotifyService notifyService;
    private final TbDeviceWarnLogMapper tbDeviceWarnLogMapper;
    private final TbNotifyRelationMapper tbNotifyRelationMapper;

    /**
     * 监听<b>更新设备数据推送</b>事件
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void listenUpdateDeviceGroupSender(final UpdateDeviceGroupSenderDto dto) {
        clearDeviceWarnLog(dto);
    }

    /**
     * 清空符合条件的设备报警记录<br>
     * 目前这里有2个关联删除的动作,如果需要添加3个以上的动作,建议修改成pipeline的形式
     *
     * @see UpdateDeviceGroupSenderEventParam
     */
    private void clearDeviceWarnLog(UpdateDeviceGroupSenderDto dto) {
        List<UpdateDeviceGroupSenderEventParam> dataList = dto.getDataList();
        if (CollUtil.isNotEmpty(dataList)) {
            LambdaQueryWrapper<TbDeviceWarnLog> wrapper = new LambdaQueryWrapper<TbDeviceWarnLog>().nested(wr ->
                    dataList.forEach(data -> wr.or(u -> {
                        u.eq(TbDeviceWarnLog::getDeviceToken, data.getDeviceToken());
                        Optional.ofNullable(data.getProjectIDList()).filter(CollUtil::isNotEmpty)
                                .ifPresent(w -> u.notIn(TbDeviceWarnLog::getProjectID, w));
                    })));
            List<Integer> deviceWarnLogIDList = tbDeviceWarnLogMapper.selectList(wrapper).stream().map(TbDeviceWarnLog::getId).toList();
            if (CollUtil.isNotEmpty(deviceWarnLogIDList)) {
                tbDeviceWarnLogMapper.deleteBatchIds(deviceWarnLogIDList);
                List<Integer> notifyIDList = tbNotifyRelationMapper.selectList(new LambdaQueryWrapper<TbNotifyRelation>()
                        .eq(TbNotifyRelation::getType, DataDeviceWarnType.DEVICE.getCode())
                        .in(TbNotifyRelation::getRelationID, deviceWarnLogIDList)).stream().map(TbNotifyRelation::getNotifyID).toList();
                if (CollUtil.isNotEmpty(notifyIDList)) {
                    notifyService.clearNotify(notifyIDList, dto.getAppend());
                }
            }
        }
    }
}
