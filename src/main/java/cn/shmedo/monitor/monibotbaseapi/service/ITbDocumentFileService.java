package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.documentfile.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.documentfile.DocumentFileResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author wuxl
 * @Date 2023/9/18 14:04
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service
 * @ClassName: ITbFileService
 * @Description: TODO
 * @Version 1.0
 */
public interface ITbDocumentFileService {
    Object queryDocumentPage(QueryDocumentFilePageParameter queryDocumentFilePageParameter);

    Object addDocumentFile(MultipartFile file, Integer subjectType, Integer subjectID, String fileDesc, String exValue);

    Object deleteDocumentFile(DeleteDocumentFileParameter deleteDocumentFileParameter);

    DocumentFileResponse queryDocumentFile(QueryDocumentFileParameter queryDocumentFileParameter);
}
