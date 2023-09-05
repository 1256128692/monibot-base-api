package cn.shmedo.monitor.monibotbaseapi.service.third.iot;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.*;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-04 17:30
 **/
public interface IotService {
    @RequestLine("POST /QueryModelFieldBatch")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Map<String, List<ModelField>>> queryModelFieldBatch(QueryModelFieldBatchParam pojo,
                                                                      @Param("appKey") String appKey,
                                                                      @Param("appSecret") String appSecret);

    @RequestLine("POST /QueryDeviceAndSensorList")
    ResultWrapper<List<DeviceWithSensor>> queryDeviceAndSensorList(QueryDeviceAndSensorRequest request);

    /**
     * 查询设备基本信息
     *
     * @param param 请求参数
     */
    @RequestLine("POST /QueryDeviceBaseInfo")
    ResultWrapper<List<DeviceBaseInfo>> queryDeviceBaseInfo(QueryDeviceBaseInfoParam param);

    /**
     * 根据推送地址查询设备简单信息列表
     * @param param 请求参数
     * @return  {@link ResultWrapper<List<SimpleDeviceV5>>}
     */
    @RequestLine("POST /QueryDeviceSimpleBySenderAddress")
    ResultWrapper<List<SimpleDeviceV5>> queryDeviceSimpleBySenderAddress(QueryDeviceSimpleBySenderAddressParam param);

    @RequestLine("POST /QueryByProductIDList")
    ResultWrapper<List<ProductBaseInfo>> queryByProductIDList(QueryByProductIDListParam param);

    /**
     * 查询设备最新状态
     * @return {@link ResultWrapper<DeviceStateResponse>}
     */
    @RequestLine("POST /QueryDeviceNewState")
    ResultWrapper<DeviceStateResponse> queryDeviceState(QueryDeviceStateParam param);

    /**
     * 查询设备最新扩展状态
     * @return {@link ResultWrapper<DeviceStateResponse>}
     */
    @RequestLine("POST /QueryDeviceExpandNewState")
    ResultWrapper<DeviceStateResponse> queryDeviceExpandState(QueryDeviceStateParam param);


    /**
     * 批量创建设备
     * @return {@link ResultWrapper<Boolean>}
     */
    @RequestLine("POST /CreateMultipleDevice")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}", "Authorization: Bearer {accessToken}"})
    ResultWrapper<Boolean> createMultipleDevice(CreateMultipleDeviceParam param,
                                                @Param("appKey") String appKey,
                                                @Param("appSecret") String appSecret,
                                                @Param("accessToken") String accessToken);


    /**
     * 批量删除设备
     * @return {@link ResultWrapper<Boolean>}
     */
    @RequestLine("POST /DeleteDevice")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Boolean> deleteDevice(DeleteDeviceParam param,
                                        @Param("appKey") String appKey,
                                        @Param("appSecret") String appSecret);
}
