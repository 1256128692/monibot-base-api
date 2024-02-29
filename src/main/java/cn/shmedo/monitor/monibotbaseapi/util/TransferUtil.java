package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.DeviceBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2023/4/27
 */
@Slf4j
public enum TransferUtil {

    @Getter
    INSTANCE;

    private final UserService userService;
    private final IotService iotService;
    private final FileConfig fileConfig;

    TransferUtil() {
        this.userService = ContextHolder.getBean(UserService.class);
        this.iotService = ContextHolder.getBean(IotService.class);
        this.fileConfig = ContextHolder.getBean(FileConfig.class);
    }

    /**
     * 转换设备基础信息
     *
     * @param records     需要转换的记录
     * @param pSupplier   查询参数提供器
     * @param keyFun      分组key提供器
     * @param transferFun 转换函数
     */
    public <T> void applyDeviceBase(@Nonnull Collection<T> records,
                                    @Nonnull Supplier<QueryDeviceBaseInfoParam> pSupplier,
                                    @Nonnull Function<? super T, String> keyFun,
                                    @Nonnull BiConsumer<T, DeviceBaseInfo> transferFun) {
        QueryDeviceBaseInfoParam param = pSupplier.get();
        if (ObjUtil.isEmpty(param) || CollUtil.isEmpty(param.getDeviceTokens())) {
            return;
        }
        ResultWrapper<List<DeviceBaseInfo>> wrapper = iotService.queryDeviceBaseInfo(pSupplier.get());
        if (wrapper.apiSuccess()) {
            Map<String, DeviceBaseInfo> tokenProductMap = wrapper.getData().stream()
                    .collect(Collectors.toMap(DeviceBaseInfo::getDeviceToken, e -> e));
            records.forEach(e -> {
                DeviceBaseInfo deviceBaseInfo = tokenProductMap.get(keyFun.apply(e));
                if (deviceBaseInfo != null) {
                    transferFun.accept(e, deviceBaseInfo);
                }
            });
        }
    }

    /**
     * 转换设备基本信息中的产品名称
     *
     * @param records       需要转换的记录
     * @param paramSupplier 查询参数提供器
     * @param keyFun        分组key提供器
     * @param transferFun   转换函数
     */
    public <T> void applyProductName(@Nonnull Collection<T> records,
                                     @Nonnull Supplier<QueryDeviceBaseInfoParam> paramSupplier,
                                     @Nonnull Function<? super T, String> keyFun,
                                     @Nonnull BiConsumer<T, String> transferFun) {
        applyDeviceBase(records, paramSupplier, keyFun, (e, device) -> {
            transferFun.accept(e, device.getProductName());
        });
    }

    /**
     * 通过用户id获取用户名字典
     *
     * @param uidList 用户id集合
     * @return 用户id -> 用户名
     */
    public Optional<Map<Integer, String>> getUserNameDict(List<Integer> uidList) {
        if (uidList == null || uidList.isEmpty()) {
            return Optional.empty();
        }
        QueryUserIDNameParameter parameter = new QueryUserIDNameParameter(uidList);
        ResultWrapper<List<UserIDName>> wrapper = userService.queryUserIDName(parameter,
                fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
        wrapper.checkApi();
        return Optional.ofNullable(wrapper.getData())
                .map(e -> e.stream().collect(Collectors.toMap(UserIDName::getUserID, UserIDName::getUserName)));
    }
}