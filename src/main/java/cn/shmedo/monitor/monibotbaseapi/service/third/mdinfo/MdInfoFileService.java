package cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.multipart.MultipartFile;

public interface MdInfoFileService {


    @RequestLine("POST /StreamFileUpload")
    @Headers("Content-Type: multipart/form-data")
    ResultWrapper<FilePathResponse> streamUploadFile(@Param("file") MultipartFile file, @Param("companyID") Integer companyID,
                                                     @Param("bucketName") String bucketName, @Param("fileName") String fileName,
                                                     @Param("fileType") String fileType, @Param("fileSecret") String fileSecret,
                                                     @Param("fileDesc") String fileDesc, @Param("userID") Integer userID, @Param("folderID") Integer folderID,
                                                     @Param("exValue") String exValue);
}