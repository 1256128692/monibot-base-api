package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.file.*;

/**
 * @Author wuxl
 * @Date 2023/9/18 14:04
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service
 * @ClassName: ITbFileService
 * @Description: TODO
 * @Version 1.0
 */
public interface ITbFileService {
    Object queryDocumentPage(QueryDocumentFilePageParameter queryDocumentFilePageParameter);

    Object addDocumentFile(AddDocumentFileParameter addDocumentFileParameter);

    Object deleteDocumentFile(DeleteDocumentFileParameter deleteDocumentFileParameter);

    Object queryDocumentFile(QueryDocumentFileParameter queryDocumentFileParameter);

}
