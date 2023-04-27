package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.DeviceBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import jakarta.annotation.Nonnull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2023/4/27
 */
public class TransferUtil {

    /**
     * 转换设备基础信息
     *
     * @param records     需要转换的记录
     * @param pSupplier   查询参数提供器
     * @param keyFun      分组key提供器
     * @param transferFun 转换函数
     */
    public static <T> void applyDeviceBase(@Nonnull Collection<T> records,
                                           @Nonnull Supplier<QueryDeviceBaseInfoParam> pSupplier,
                                           @Nonnull Function<? super T, String> keyFun,
                                           @Nonnull BiConsumer<T, DeviceBaseInfo> transferFun) {
        ResultWrapper<List<DeviceBaseInfo>> wrapper = SpringUtil.getBean(IotService.class)
                .queryDeviceBaseInfo(pSupplier.get());
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
    public static <T> void applyDeviceBaseItem(@Nonnull Collection<T> records,
                                               @Nonnull Supplier<QueryDeviceBaseInfoParam> paramSupplier,
                                               @Nonnull Function<? super T, String> keyFun,
                                               @Nonnull BiConsumer<T, String> transferFun) {
        applyDeviceBase(records, paramSupplier, keyFun, (e, device) -> {
            transferFun.accept(e, device.getProductName());
        });
    }
}