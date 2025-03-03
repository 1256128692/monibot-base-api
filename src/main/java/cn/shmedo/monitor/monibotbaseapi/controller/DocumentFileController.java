package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.core.annotation.ResourceSymbol;
import cn.shmedo.monitor.monibotbaseapi.model.param.documentfile.*;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDocumentFileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
public class DocumentFileController {
    @Autowired
    private ITbDocumentFileService iTbDocumentFileService;

    /**
     * @api {POST} /QueryDocumentPage 查看资料文件列表
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName QueryDocumentPage
     * @apiDescription 查看资料文件列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiParam (请求体) {Int} [subjectID] 对象ID
     * @apiParam (请求体) {String} [fileName] 文件名称
     * @apiParam (请求体) {Bool} [createTimeDesc] 文件上传时间降序（为空或true为降序，为false升序）
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
        return iTbDocumentFileService.queryDocumentPage(queryDocumentFilePageParameter);
    }

    /**
     * @api {POST} /AddDocumentFile 新增资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName AddDocumentFile
     * @apiDescription 新增资料文件（projectID非空，校验项目权限，否则以企业校验权限）
     * @apiHeader {String} Content-Type multipart/form-data
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [projectID] 项目ID
     * @apiParam (请求体) {MultipartFile} file 资产文件
     * @apiParam (请求体) {int} subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiParam (请求体) {int} subjectID  对象ID
     * @apiParam (请求体) {String} [fileDesc] 文件描述
     * @apiParam (请求体) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {int} ID ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddDocumentFile
     */
    @LogParam(moduleName = "资料文件模块", operationName = "新增资料文件", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddDocumentFile")
    @PostMapping("/AddDocumentFile")
    public Object addDocumentFile(
            @RequestParam(required = false) @Positive @ResourceSymbol(ResourceType.BASE_PROJECT) Integer projectID,
            @RequestParam @Valid @NotNull @Positive @ResourceSymbol(ResourceType.COMPANY) Integer companyID,
            @RequestParam MultipartFile file,
            @RequestParam @Valid @NotNull Integer subjectType,
            @RequestParam @Valid @NotNull Integer subjectID,
            @RequestParam(required = false) String fileDesc,
            @RequestParam(required = false) String exValue) {
        return iTbDocumentFileService.addDocumentFile(file, subjectType, subjectID, fileDesc, exValue);
    }

    /**
     * @api {POST} /DeleteDocumentFile 删除资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName DeleteDocumentFile
     * @apiDescription 删除资料文件
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [projectID] 项目ID
     * @apiParam (请求体) {Int[]} fileIDList 文件ID列表
     * @apiSuccess (返回结果) {Int} rows 删除行数
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteDocumentFile
     */
    @LogParam(moduleName = "资料文件模块", operationName = "删除资料文件", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeleteDocumentFile")
    @PostMapping("/DeleteDocumentFile")
    public Object deleteDocumentFile(@Valid @RequestBody DeleteDocumentFileParameter deleteDocumentFileParameter) {
        return iTbDocumentFileService.deleteDocumentFile(deleteDocumentFileParameter);
    }

    /**
     * @api {POST} /QueryDocumentFile 查询资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName QueryDocumentFile
     * @apiDescription 查询资料文件
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [projectID] 项目ID
     * @apiParam (请求体) {Int} fileID 文件ID
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
        return iTbDocumentFileService.queryDocumentFile(queryDocumentFileParameter);
    }

}
