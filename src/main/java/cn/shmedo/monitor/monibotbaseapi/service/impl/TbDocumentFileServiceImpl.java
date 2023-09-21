package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDocumentFileMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDocumentFile;
import cn.shmedo.monitor.monibotbaseapi.model.param.documentfile.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FilePathResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.documentfile.DocumentFileResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDocumentFileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoFileService;
import cn.shmedo.monitor.monibotbaseapi.util.CustomizeBeanUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
public class TbDocumentFileServiceImpl implements ITbDocumentFileService, InitializingBean {
    private static final String BUCKET_NAME = TbDocumentFile.class.getAnnotation(TableName.class).value();
    private String appKey = null;
    private String appSecret = null;

    @Autowired
    private TbDocumentFileMapper tbDocumentFileMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MdInfoFileService mdInfoFileService;

    @Autowired
    private FileConfig fileConfig;

    @Override
    public void afterPropertiesSet() {
        appKey = fileConfig.getAuthAppKey();
        appSecret = fileConfig.getAuthAppSecret();
    }

    @Override
    public PageUtil.Page<DocumentFileResponse> queryDocumentPage(QueryDocumentFilePageParameter queryDocumentFilePageParameter) {
        // 分页条件
        Page<TbDocumentFile> queryPage = new Page<>(queryDocumentFilePageParameter.getCurrentPage(), queryDocumentFilePageParameter.getPageSize());
        // 查询条件
        QueryWrapper<TbDocumentFile> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(queryDocumentFilePageParameter.getFileName())) {
            queryWrapper.lambda().eq(TbDocumentFile::getFileName, queryDocumentFilePageParameter.getFileName());
        }
        IPage<TbDocumentFile> resultPage = tbDocumentFileMapper.selectPage(queryPage, queryWrapper);
        List<DocumentFileResponse> documentFileResponseList = new ArrayList<>();
        if (Objects.nonNull(resultPage)) {
            List<TbDocumentFile> records = resultPage.getRecords();
            List<Integer> userIDList = records.stream().map(TbDocumentFile::getCreateUserID).collect(Collectors.toList());
            ResultWrapper<Object> resultWrapper = userService.queryUserIDName(new QueryUserIDNameParameter(userIDList), appKey, appSecret);
            if (resultWrapper.apiSuccess()) {
                // 获取CreateUserID对应的CreateUserName
                List<Map<String, Object>> userIDNameList = (List<Map<String, Object>>) resultWrapper.getData();
                if (!CollectionUtil.isNullOrEmpty(userIDNameList)) {
                    List<UserIDName> newUserIDNameList = CustomizeBeanUtil.toBeanList(userIDNameList, UserIDName.class);
                    Map<Integer, UserIDName> userIDNameMap = newUserIDNameList.stream().collect(Collectors.toMap(UserIDName::getUserID, Function.identity()));
                    documentFileResponseList = CustomizeBeanUtil.copyListProperties(records, DocumentFileResponse::new);
                    documentFileResponseList.forEach(res -> res.setCreateUserName(userIDNameMap.get(res.getCreateUserId()).getUserName()));
                }
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
        TbDocumentFile tbDocumentFile = tbDocumentFileMapper.selectOne(new QueryWrapper<TbDocumentFile>().lambda().eq(TbDocumentFile::getFileName, fileName));
        if (Objects.nonNull(tbDocumentFile)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "资料文件名称不能重复");
        }

        // 文件上传到oss
        ResultWrapper<FilePathResponse> resultWrapper = mdInfoFileService
                .streamUploadFile(file, companyID, BUCKET_NAME, fileName, fileType, "", fileDesc, subjectID, -1, exValue);
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
        List<Integer> userIDList = Collections.singletonList(documentFileResponse.getCreateUserId());
        ResultWrapper<Object> resultWrapper = userService.queryUserIDName(new QueryUserIDNameParameter(userIDList), appKey, appSecret);
        if (resultWrapper.apiSuccess()) {
            List<Map<String, Object>> userIDNameList = (List<Map<String, Object>>) resultWrapper.getData();
            if (CollectionUtil.isNullOrEmpty(userIDNameList)) {
                return documentFileResponse;
            }
            List<UserIDName> newUserIDNameList = CustomizeBeanUtil.toBeanList(userIDNameList, UserIDName.class);
            documentFileResponse.setCreateUserName(newUserIDNameList.get(0).getUserName());
        }
        return documentFileResponse;
    }

}
