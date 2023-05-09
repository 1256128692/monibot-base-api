package cn.shmedo.monitor.monibotbaseapi.service.file;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.ErrorConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.*;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * @author Chengfs on 2023/4/13
 */
@Slf4j
@Component
@AllArgsConstructor
public class FileService {

    private final MdInfoService mdInfoService;

    private static final String BASE64_FLAG = ";base64,";


    /**
     * base64 文件上传
     *
     * @param fileContent base64文件内容
     * @param fileSuffix  文件后缀
     * @param userID      用户ID
     * @param fileName    文件名
     * @param companyID   公司ID
     * @return 文件key
     */
    public String base64Upload(String fileContent, String fileSuffix,
                               Integer userID, String fileName, Integer companyID) {
        AddFileUploadRequest pojo = new AddFileUploadRequest();
        pojo.setBucketName(DefaultConstant.MD_INFO_BUCKETNAME);
        pojo.setFileName(StrUtil.isNotBlank(fileName) ? fileName : UUID.randomUUID().toString());
        pojo.setFileContent(fileContent);
        pojo.setFileType(fileSuffix);
        pojo.setUserID(userID);
        pojo.setCompanyID(companyID);
        ResultWrapper<FilePathResponse> info = mdInfoService.addFileUpload(pojo);
        if (!info.apiSuccess()) {
            return ErrorConstant.IMAGE_INSERT_FAIL;
        } else {
            return info.getData().getPath();
        }
    }

    /**
     * base64 文件上传<br/>
     * 通过 {@link CurrentSubjectHolder} 获取必须用户信息，故仅限于请求处理线程中使用
     *
     * @param fileContent base64内容
     * @param fileSuffix  文件后缀
     * @param fileNane    文件名称
     * @return 文件key
     */
    public String base64Upload(String fileContent, String fileSuffix, String fileNane) {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        return base64Upload(fileContent, fileSuffix, subject.getSubjectID(), fileNane, subject.getCompanyID());
    }

    /**
     * base64 文件上传<br/>
     * 通过 {@link CurrentSubjectHolder} 获取必须用户信息，故仅限于请求处理线程中使用
     *
     * @param fileContent base64文件内容
     * @param fileSuffix  文件后缀
     * @return 文件key
     */
    public String base64Upload(String fileContent, String fileSuffix) {
        return base64Upload(fileContent, fileSuffix, null);
    }

    /**
     * base64 文件上传<br/>
     * 通过 {@link CurrentSubjectHolder} 获取必须用户信息，故仅限于请求处理线程中使用
     *
     * @param base64 带前缀的base64字符串，形如{@code data:image/png;base64,iVBORw0AAA}
     * @return 文件key
     */
    public String base64Upload(String base64) {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        String fileSuffix = StrUtil.subBetween(base64, StrUtil.SLASH, BASE64_FLAG);
        String fileContent = StrUtil.subAfter(base64, BASE64_FLAG, false);
        Assert.notEmpty(fileSuffix, "文件格式获取失败");
        Assert.notEmpty(fileContent, "文件内容为空");
        return base64Upload(fileContent, fileSuffix);
    }


    /**
     * 获取文件访问地址
     *
     * @param ossPath 文件路径
     * @return 文件访问地址
     */
    @SneakyThrows
    public String getFileUrl(String ossPath) {
        if (StrUtil.isNotEmpty(ossPath)) {
            QueryFileInfoRequest pojo = new QueryFileInfoRequest();
            pojo.setBucketName(DefaultConstant.MD_INFO_BUCKETNAME);
            pojo.setFilePath(ossPath);
            ResultWrapper<FileInfoResponse> info = mdInfoService.queryFileInfo(pojo);
            if (info.apiSuccess()) {
                return info.getData().getAbsolutePath();
            } else {
                log.error("获取文件 {} 信息失败，{} => {}", ossPath, info.getCode(), info.getMsg());
            }
        }
        return null;
    }



    /**
     * 获取文件访问地址
     *
     * @param ossPathList 文件路径
     * @return 文件访问地址
     */
    @SneakyThrows
    public List<FileInfoResponse> getFileUrlList(List<String> ossPathList, Integer companyID) {
        if (CollectionUtil.isNotEmpty(ossPathList)){
            QueryFileListInfoRequest pojo = new QueryFileListInfoRequest();
            pojo.setCompanyID(companyID);
            pojo.setBucketName(DefaultConstant.MD_INFO_BUCKETNAME);
            pojo.setFilePathList(ossPathList);
            ResultWrapper<List<FileInfoResponse>> info = mdInfoService.queryFileListInfo(pojo);
            if (info.apiSuccess()) {
                return info.getData();
            } else {
                log.error("获取文件 {} 信息失败，{} => {}", ossPathList.stream().toList(), info.getCode(), info.getMsg());
            }
        }
        return null;
    }
}

    
    