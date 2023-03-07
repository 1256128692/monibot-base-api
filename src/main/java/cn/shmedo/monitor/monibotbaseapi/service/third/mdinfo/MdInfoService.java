package cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.QueryFileInfoRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface MdInfoService {


    @RequestLine("POST /AddFileUpload")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<FilePathResponse> AddFileUpload(AddFileUploadRequest addFileUploadRequest,
                                                  @Param("appKey") String appKey,
                                                  @Param("appSecret") String appSecret);

    @RequestLine("POST /QueryFileInfo")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<FileInfoResponse> queryFileInfo(QueryFileInfoRequest pojo,
                                                  @Param("appKey") String appKey,
                                                  @Param("appSecret") String appSecret);
}
