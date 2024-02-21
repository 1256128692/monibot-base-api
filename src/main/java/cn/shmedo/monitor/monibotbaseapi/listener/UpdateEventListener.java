package cn.shmedo.monitor.monibotbaseapi.listener;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDeviceWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.UpdateDeviceGroupSenderDto;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.UpdateDeviceGroupSenderEventParam;
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
    private final TbDeviceWarnLogMapper tbDeviceWarnLogMapper;

    /**
     * 监听<b>更新设备数据推送</b>事件
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void listenUpdateDeviceGroupSender(UpdateDeviceGroupSenderDto dto) {
        clearDeviceWarnLog(dto.getDataList());
    }

    /**
     * 清空符合条件的设备报警记录
     *
     * @see UpdateDeviceGroupSenderDto
     */
    private void clearDeviceWarnLog(final List<UpdateDeviceGroupSenderEventParam> dataList) {
        if (CollUtil.isNotEmpty(dataList)) {
            LambdaQueryWrapper<TbDeviceWarnLog> wrapper = new LambdaQueryWrapper<TbDeviceWarnLog>().nested(wr ->
                    dataList.forEach(data -> wr.or(u -> {
                        u.eq(TbDeviceWarnLog::getDeviceToken, data.getDeviceToken());
                        Optional.ofNullable(data.getProjectIDList()).filter(CollUtil::isNotEmpty)
                                .ifPresent(w -> u.notIn(TbDeviceWarnLog::getProjectID, w));
                    })));
            tbDeviceWarnLogMapper.delete(wrapper);
        }
    }
}
