package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.file.*;
import cn.shmedo.monitor.monibotbaseapi.service.ITbFileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wuxl
 * @Date 2023/9/18 14:02
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller
 * @ClassName: FileController
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@RequestMapping
public class FileController {
    @Autowired
    private ITbFileService iTbFileService;

    /**
     * @api {POST} /QueryDocumentPage 查看资料文件列表
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName QueryDocumentPage
     * @apiDescription 查看资料文件列表
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiParam (请求体) {String} [fileName] 文件名称
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {int} currentPageData.ID 主键ID
     * @apiSuccess (返回结果) {int} currentPageData.subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiSuccess (返回结果) {int} currentPageData.subjectID 对象ID
     * @apiSuccess (返回结果) {String} currentPageData.fileName 文件名称
     * @apiSuccess (返回结果) {String} currentPageData.fileType 文件类型
     * @apiSuccess (返回结果) {int} currentPageData.fileSize 文件大小
     * @apiSuccess (返回结果) {String} currentPageData.filePath 文件地址
     * @apiSuccess (返回结果) {String} [currentPageData.fileDesc] 文件描述
     * @apiSuccess (返回结果) {String} [currentPageData.exValue] 扩展字段
     * @apiSuccess (返回结果) {String} currentPageData.createUserID 创建人ID
     * @apiSuccess (返回结果) {String} currentPageData.createUserName 创建人名称
     * @apiSuccess (返回结果) {Date} currentPageData.createTime 创建时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:QueryDocumentPage
     */
    @Permission(permissionName = "mdmbase:QueryDocumentPage")
    @PostMapping("/QueryDocumentPage")
    public Object queryDocumentPage(@Valid @RequestBody QueryDocumentFilePageParameter queryDocumentFilePageParameter) {
        return iTbFileService.queryDocumentPage(queryDocumentFilePageParameter);
    }

    /**
     * @api {POST} /AddDocumentFile 新增资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName AddDocumentFile
     * @apiDescription 新增资料文件
     * @apiHeader {String} Content-Type multipart/form-data
     * @apiParam (请求体) {MultipartFile} file 资产文件
     * @apiParam (请求体) {int} subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiParam (请求体) {int} subjectID  对象ID
     * @apiParam (请求体) {String} fileName 文件名称
     * @apiParam (请求体) {String} fileName 文件
     * @apiParam (请求体) {String} fileType 文件类型
     * @apiParam (请求体) {int} fileSize 文件大小
     * @apiParam (请求体) {String} [fileDesc] 文件描述
     * @apiParam (请求体) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {int} ID ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddDocumentFile
     */
    @LogParam(moduleName = "资料文件模块", operationName = "新增资料文件", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddDocumentFile")
    @PostMapping("/AddDocumentFile")
    public Object addDocumentFile(@Valid @RequestBody AddDocumentFileParameter addDocumentFileParameter) {
        return iTbFileService.addDocumentFile(addDocumentFileParameter);
    }

    /**
     * @api {POST} /DeleteDocumentFile 删除资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName DeleteDocumentFile
     * @apiDescription 删除资料文件
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int[]} fileIDList 文件ID列表
     * @apiSuccess (返回结果) {Int} rows 删除行数
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteDocumentFile
     */
    @LogParam(moduleName = "资料文件模块", operationName = "删除资料文件", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeleteDocumentFile")
    @PostMapping("/DeleteDocumentFile")
    public Object deleteDocumentFile(@Valid @RequestBody DeleteDocumentFileParameter deleteDocumentFileParameter) {
        return iTbFileService.deleteDocumentFile(deleteDocumentFileParameter);
    }

    /**
     * @api {POST} /QueryDocumentFile 查询资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName QueryDocumentFile
     * @apiDescription 查询资料文件
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} ID ID
     * @apiSuccess (返回结果) {int} ID 主键ID
     * @apiSuccess (返回结果) {int} subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiSuccess (返回结果) {int} subjectID  对象ID
     * @apiSuccess (返回结果) {String} fileName 文件名称
     * @apiSuccess (返回结果) {String} fileType 文件类型
     * @apiSuccess (返回结果) {int} fileSize 文件大小
     * @apiSuccess (返回结果) {String} filePath 文件地址
     * @apiSuccess (返回结果) {String} [fileDesc] 文件描述
     * @apiSuccess (返回结果) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} createUserID 创建人ID
     * @apiSuccess (返回结果) {String} createUserName 创建人名称
     * @apiSuccess (返回结果) {Date} createTime 创建时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:QueryDocumentFile
     */
    @Permission(permissionName = "mdmbase:QueryDocumentFile")
    @PostMapping("/QueryDocumentFile")
    public Object queryDocumentFile(@Valid @RequestBody QueryDocumentFileParameter queryDocumentFileParameter) {
        return iTbFileService.queryDocumentFile(queryDocumentFileParameter);
    }

}
