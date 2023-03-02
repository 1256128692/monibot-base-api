package cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.CompanyThird;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface MdInfoService {


    @RequestLine("POST /AddFileUpload")
//    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<FilePathResponse> AddFileUpload(AddFileUploadRequest addFileUploadRequest);

}
