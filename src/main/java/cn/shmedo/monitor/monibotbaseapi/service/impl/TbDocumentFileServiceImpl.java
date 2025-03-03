package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDocumentFileMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDocumentFile;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DocumentSubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.documentfile.DeleteDocumentFileParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.documentfile.QueryDocumentFilePageParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.documentfile.QueryDocumentFileParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.documentfile.DocumentFileResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDocumentFileService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoFileService;
import cn.shmedo.monitor.monibotbaseapi.util.CustomizeBeanUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author wuxl
 * @Date 2023/9/18 14:04
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service.impl
 * @ClassName: TbFileServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class TbDocumentFileServiceImpl implements ITbDocumentFileService {
    private TbDocumentFileMapper tbDocumentFileMapper;
    private UserService userService;
    private FileService fileService;
    private MdInfoFileService mdInfoFileService;
    private FileConfig fileConfig;

    public TbDocumentFileServiceImpl(TbDocumentFileMapper tbDocumentFileMapper, UserService userService,
                                     FileService fileService, MdInfoFileService mdInfoFileService, FileConfig fileConfig) {
        this.tbDocumentFileMapper = tbDocumentFileMapper;
        this.userService = userService;
        this.fileService = fileService;
        this.mdInfoFileService = mdInfoFileService;
        this.fileConfig = fileConfig;
    }

    public PageUtil.Page<DocumentFileResponse> queryDocumentPage(QueryDocumentFilePageParameter parameter) {
        // 分页条件
        Page<TbDocumentFile> queryPage = new Page<>(parameter.getCurrentPage(), parameter.getPageSize());
        // 查询条件
        LambdaQueryWrapper<TbDocumentFile> queryWrapper = new QueryWrapper<TbDocumentFile>().lambda()
                .eq(TbDocumentFile::getSubjectType, parameter.getSubjectType())
                .eq(DocumentSubjectType.PROJECT.getCode().equals(parameter.getSubjectType()), TbDocumentFile::getSubjectID, parameter.getSubjectID())
                .eq(DocumentSubjectType.OTHER_DEVICE.getCode().equals(parameter.getSubjectType()) && Objects.nonNull(parameter.getSubjectID()),
                        TbDocumentFile::getSubjectID, parameter.getSubjectID())
                .like(StringUtils.isNotEmpty(parameter.getFileName()), TbDocumentFile::getFileName, parameter.getFileName())
                .orderByDesc(Objects.isNull(parameter.getCreateTimeDesc()) || parameter.getCreateTimeDesc(), TbDocumentFile::getCreateTime)
                .orderByAsc(Objects.nonNull(parameter.getCreateTimeDesc()) && !parameter.getCreateTimeDesc(), TbDocumentFile::getCreateTime);
        IPage<TbDocumentFile> resultPage = tbDocumentFileMapper.selectPage(queryPage, queryWrapper);
        List<DocumentFileResponse> documentFileResponseList = new ArrayList<>();
        if (Objects.nonNull(resultPage)) {
            Map<Integer, String> userIdNameMap = Maps.newHashMap();
            Map<String, String> fileInfoResponseMap = Maps.newHashMap();
            List<TbDocumentFile> records = resultPage.getRecords();
            Set<Integer> userIdSet = records.stream().map(TbDocumentFile::getCreateUserID).collect(Collectors.toSet());
            Set<String> ossKeySet = records.stream().map(TbDocumentFile::getFilePath).collect(Collectors.toSet());
            List<Integer> userIdList = new ArrayList<>(userIdSet);
            if (!CollectionUtil.isNullOrEmpty(userIdList)) {
                // 获取CreateUserID对应的CreateUserName
                ResultWrapper<List<UserIDName>> resultWrapper = userService.queryUserIDName(new QueryUserIDNameParameter(userIdList), fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
                if (resultWrapper.apiSuccess()) {
                    if (!CollectionUtil.isNullOrEmpty(resultWrapper.getData())) {
                        List<UserIDName> newUserIdNameList = resultWrapper.getData();
                        userIdNameMap = newUserIdNameList.stream().collect(Collectors.toMap(UserIDName::getUserID, UserIDName::getUserName));
                    }
                }
            }

            // 处理filePath
            List<FileInfoResponse> fileInfoResponseList = fileService.getFileUrlList(new ArrayList<>(ossKeySet), CurrentSubjectHolder.getCurrentSubject().getCompanyID());
            if (!CollectionUtil.isNullOrEmpty(fileInfoResponseList)) {
                fileInfoResponseMap = fileInfoResponseList.stream().collect(Collectors.toMap(FileInfoResponse::getFilePath, FileInfoResponse::getAbsolutePath));
            }

            // 包装返回数据
            documentFileResponseList = CustomizeBeanUtil.copyListProperties(records, DocumentFileResponse::new);
            for (DocumentFileResponse documentFileResponse : documentFileResponseList) {
                documentFileResponse.setCreateUserName(userIdNameMap.getOrDefault(documentFileResponse.getCreateUserId(), ""));
                documentFileResponse.setFilePath(fileInfoResponseMap.getOrDefault(documentFileResponse.getFilePath(), ""));
            }
        }
        return new PageUtil.Page<>(resultPage.getPages(), documentFileResponseList, resultPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object addDocumentFile(MultipartFile file, Integer subjectType, Integer subjectID, String fileDesc, String exValue) {
        Integer companyID = CurrentSubjectHolder.getCurrentSubject().getCompanyID();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        long fileSize = file.getSize();
        TbDocumentFile tbDocumentFile = tbDocumentFileMapper.selectOne(new QueryWrapper<TbDocumentFile>().lambda()
                .eq(TbDocumentFile::getFileName, fileName)
                .eq(TbDocumentFile::getSubjectType, subjectType)
                .eq(TbDocumentFile::getSubjectID, subjectID));
        if (Objects.nonNull(tbDocumentFile)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资料文件名称不能重复");
        }

        // 文件上传到oss
        ResultWrapper<FilePathResponse> resultWrapper = mdInfoFileService
                .streamUploadFile(file, companyID, DefaultConstant.MD_INFO_BUCKETNAME, fileName, fileType, "", fileDesc, subjectID, -1, exValue);
        if (!resultWrapper.apiSuccess()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, resultWrapper.getMsg());
        }
        FilePathResponse data = resultWrapper.getData();
        tbDocumentFile = new TbDocumentFile();
        tbDocumentFile.setSubjectType(subjectType);
        tbDocumentFile.setSubjectID(subjectID);
        tbDocumentFile.setFileType(fileType);
        tbDocumentFile.setFileName(fileName);
        tbDocumentFile.setFileSize(fileSize);
        tbDocumentFile.setFilePath(data.getPath());
        tbDocumentFile.setFileDesc(fileDesc);
        tbDocumentFile.setExValue(exValue);
        tbDocumentFile.setCreateTime(LocalDateTime.now());
        tbDocumentFile.setCreateUserID(CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        tbDocumentFileMapper.insert(tbDocumentFile);
        return tbDocumentFile.getID();
    }

    @Override
    public Object deleteDocumentFile(DeleteDocumentFileParameter deleteDocumentFileParameter) {
        return tbDocumentFileMapper.deleteBatchIds(deleteDocumentFileParameter.getFileIDList());
    }

    @Override
    public DocumentFileResponse queryDocumentFile(QueryDocumentFileParameter queryDocumentFileParameter) {
        DocumentFileResponse documentFileResponse = DocumentFileResponse.getDocumentFile(queryDocumentFileParameter.getTbDocumentFile());
        List<Integer> userIdList = Collections.singletonList(documentFileResponse.getCreateUserId());
        ResultWrapper<List<UserIDName>> resultWrapper = userService.queryUserIDName(new QueryUserIDNameParameter(userIdList), fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
        if (resultWrapper.apiSuccess()) {
            if (CollectionUtil.isNullOrEmpty(resultWrapper.getData())) {
                return documentFileResponse;
            }
            List<UserIDName> newUserIdNameList = resultWrapper.getData();
            documentFileResponse.setCreateUserName(newUserIdNameList.get(0).getUserName());
        }

        // 文件绝对路径
        String filePath = fileService.getFileUrl(queryDocumentFileParameter.getTbDocumentFile().getFilePath());
        documentFileResponse.setFilePath(filePath);
        return documentFileResponse;
    }

}
