package cn.shmedo.monitor.monibotbaseapi.service.third.iot;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryModelFieldBatchParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.QueryFileInfoRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.ModelField;
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
}
