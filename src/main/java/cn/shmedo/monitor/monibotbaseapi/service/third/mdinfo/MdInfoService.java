package cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.*;
import feign.Headers;
import feign.RequestLine;

import java.util.List;

public interface MdInfoService {


    @RequestLine("POST /AddFileUpload")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<FilePathResponse> addFileUpload(AddFileUploadRequest addFileUploadRequest);

    @RequestLine("POST /QueryFileInfo")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<FileInfoResponse> queryFileInfo(QueryFileInfoRequest pojo);


    @RequestLine("POST /QueryFileListInfo")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<FileInfoResponse>> queryFileListInfo(QueryFileListInfoRequest pojo);
}
