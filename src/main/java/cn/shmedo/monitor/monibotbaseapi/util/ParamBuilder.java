package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.AddFileUploadRequest;
import lombok.Data;

import java.util.UUID;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 10:52
 **/
public class ParamBuilder {

    public static AddFileUploadRequest buildAddFileUploadRequest (String imageContent, String imageSuffix, Integer userID, String fileName,
                                                                  Integer companyID){

        AddFileUploadRequest pojo = new AddFileUploadRequest();
        if (StrUtil.isBlank(fileName)) {
            pojo.setFileName(UUID.randomUUID().toString());
        } else {
            pojo.setFileName(fileName);
        }
        pojo.setBucketName(DefaultConstant.MD_INFO_BUCKETNAME);
        pojo.setFileContent(imageContent);
        pojo.setFileType(imageSuffix);
        pojo.setUserID(userID);
        pojo.setCompanyID(companyID);
        return pojo;
    }
}
