package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserContactParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDeviceWarnParam;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDeviceWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.notify.NotifyService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class TbDeviceWarnLogServiceImpl implements ITbDeviceWarnLogService {

    private static final String WARN_CONTENT_FORMAT = "{} 内 {} 于 {} 发生离线报警，设备型号: {} ，设备SN: {}，请关注！";

    private final TbDeviceWarnLogMapper tbDeviceWarnLogMapper;
    private final NotifyService notifyService;
    private final TbWarnNotifyRelationMapper notifyRelationMapper;
    private final FileConfig fileConfig;

    private final TbWarnNotifyConfigMapper tbWarnNotifyConfigMapper;

    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public void saveDeviceWarnLog(SaveDeviceWarnParam param) {


        String content = StrUtil.format(WARN_CONTENT_FORMAT,
                param.getProjectName(), param.getDeviceSource(),
                param.getTime(), param.getDeviceType(), param.getDeviceToken());

        if (param.getTbDeviceWarnLog() == null) {
            TbDeviceWarnLog tbDeviceWarnLog = new TbDeviceWarnLog(null, param.getCompanyID(), param.getPlatform(),
                    param.getProjectID(), param.getDeviceToken(), param.getTime(), null, content, null, 0,
                    null, null, null, null, null);

            if (param.getStatus() !=null && !param.getStatus()) {
                tbDeviceWarnLogMapper.insert(tbDeviceWarnLog);

                TbWarnNotifyConfig warnNotifyConfig = tbWarnNotifyConfigMapper.queryByCompanyIDAndPlatformID(param.getCompanyID(),
                        param.getPlatform(),1, param.getProjectID());

                if (ObjectUtil.isNotNull(warnNotifyConfig)) {
                    //通知 (需要发送通知)
                    if (warnNotifyConfig.getNotifyMethod() != null) {

                        ResultWrapper<Map<Integer, String>> wrapper = userService.queryUserContact(QueryUserContactParam.builder()
                                .depts(JSONUtil.toList(warnNotifyConfig.getDepts(), Integer.class))
                                .roles(JSONUtil.toList(warnNotifyConfig.getRoles(), Integer.class))
                                .users(JSONUtil.toList(warnNotifyConfig.getUsers(), Integer.class))
                                .build(), fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());

                        // 短信推送
                        if (warnNotifyConfig.getNotifyMethod().contains("2")) {
                            Map<Integer, String> phoneMap = Optional.ofNullable(wrapper.getData()).filter(e -> !e.isEmpty()).orElse(Map.of());

                            try {
                                boolean result = notifyService.smsNotify(DefaultConstant.SMS_SIGN_NAME, fileConfig.getDeviceWarnTemplateCode(),
                                        Map.of("projectName", param.getProjectName(),
                                                "deviceModel", param.getDeviceSource(),
                                                "time", param.getTime(),
                                                "deviceType", param.getDeviceType(),
                                                "deviceSn", param.getDeviceToken()), phoneMap.values().toArray(String[]::new));
                                assert result;
                            } catch (Exception e) {
                                log.info("设备SN: {}, 平台: {} 报警短信发送失败: {}", param.getDeviceToken(),
                                        param.getPlatform(), e.getMessage());
                            }
                        }

                        // 平台推送
                        if (warnNotifyConfig.getNotifyMethod().contains("1")) {
                            // 绑定通知ID与设备预警日志
                            final String warnName = "设备下线";
                            List<Integer> notifyIds = null;
                            Map<Integer, String> phoneUserMap = Optional.ofNullable(wrapper.getData()).filter(e -> !e.isEmpty()).orElse(Map.of());
                            try {
                                notifyIds = notifyService.sysNotify(param.getCompanyID(),
                                        () -> List.of(new SysNotify.Notify(SysNotify.Type.ALARM, warnName,
                                                content, SysNotify.Status.UNREAD, param.getTime())),
                                        phoneUserMap.keySet().toArray(Integer[]::new));
                            } catch (Exception e) {
                                log.info("设备SN: {}, 平台: {} 报警平台通知发送失败: {}", param.getDeviceToken(),
                                        param.getPlatform(), e.getMessage());
                            }

                            //通知关联
                            Optional.ofNullable(notifyIds).filter(e -> !e.isEmpty())
                                    .ifPresent(e -> notifyRelationMapper.insertBatchSomeColumn(e.stream()
                                            .map(item -> {
                                                TbWarnNotifyRelation relation = new TbWarnNotifyRelation();
                                                relation.setNotifyID(item);
                                                relation.setWarnLogID(tbDeviceWarnLog.getId());
                                                relation.setType(1);
                                                return relation;
                                            }).toList()));

                        }
                    } else {
                        log.info("设备SN:{}  平台: {} 未配置通知人, 无法发送通知", param.getDeviceToken(), param.getPlatform());
                    }
                }
            } else {
                if (param.getStatus() != null && param.getStatus() && param.getTbDeviceWarnLog() != null) {
                    param.getTbDeviceWarnLog().setWarnTime(param.getTime());
                    param.getTbDeviceWarnLog().setDataStatus(0);
                    tbDeviceWarnLogMapper.updateById(param.getTbDeviceWarnLog());
                }
            }
        }
    }


}
