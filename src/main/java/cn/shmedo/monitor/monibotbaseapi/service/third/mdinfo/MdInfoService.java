package cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.QueryFileInfoRequest;
import feign.RequestLine;

public interface MdInfoService {


    @RequestLine("POST /AddFileUpload")
//    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<FilePathResponse> AddFileUpload(AddFileUploadRequest addFileUploadRequest);

    @RequestLine("POST /QueryFileInfo")
    ResultWrapper<FileInfoResponse> queryFileInfo(QueryFileInfoRequest pojo);
}
