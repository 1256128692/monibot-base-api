package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnLevelType;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import jakarta.annotation.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-12 16:51
 */
public interface IDataWarnNotifyLevelCheck {
    List<Integer> getWarnLevel();

    /**
     * 数据报警通知报警等级合法性校验(提供{@link Consumer}让调用方可以获取校验后的值)<br>
     * 注意: 仅校验{@link List}里元素的合法性,不校验{@link List}本身是否<b>必须存在、非空</b>
     */
    default ResultWrapper<?> warnLevelValid(final Integer companyID, final Integer platform,
                                            @Nullable final Consumer<List<Integer>> consumer) {
        List<Integer> warnLevel = getWarnLevel();
        TbWarnBaseConfig tbWarnBaseConfig = ContextHolder.getBean(ITbWarnBaseConfigService.class).queryByCompanyIDAndPlatform(companyID, platform);
        Set<Integer> warnLevelSet = DataWarnLevelType.fromCode(tbWarnBaseConfig.getWarnLevelType()).getWarnLevelSet();
        if (warnLevel.stream().anyMatch(u -> !warnLevelSet.contains(u))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "报警等级配置不合法");
        }
        Optional.ofNullable(consumer).ifPresent(u -> u.accept(warnLevel));
        return null;
    }
}
