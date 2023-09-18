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
     * @api {POST} /DescribeFileList 查看资料文件列表
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName DescribeFileList
     * @apiDescription 查看资料文件列表
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [fileName] 文件名称
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {int} ID 主键ID
     * @apiSuccess (返回结果) {int} subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiSuccess (返回结果) {int} subjectID 对象ID
     * @apiSuccess (返回结果) {String} fileName 文件名称
     * @apiSuccess (返回结果) {String} fileType 文件类型
     * @apiSuccess (返回结果) {int} fileSize 文件大小
     * @apiSuccess (返回结果) {String} filePath 文件地址
     * @apiSuccess (返回结果) {String} [fileDesc] 文件描述
     * @apiSuccess (返回结果) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} createUserName 创建人名称
     * @apiSuccess (返回结果) {Date} createTime 创建时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeFileList
     */
    @Permission(permissionName = "mdmbase:DescribeFileList")
    @PostMapping("/DescribeFileList")
    public Object describeFileList(@Valid @RequestBody QueryFileListParameter queryFileListParameter) {
        return iTbFileService.describeFileList(queryFileListParameter);
    }

    /**
     * @api {POST} /AddFile 新增资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName AddFile
     * @apiDescription 新增资料文件
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {int} subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiParam (请求体) {int} SubjectID  对象ID
     * @apiParam (请求体) {String} fileName 文件名称
     * @apiParam (请求体) {String} fileName 文件
     * @apiParam (请求体) {String} fileType 文件类型
     * @apiParam (请求体) {int} fileSize 文件大小
     * @apiParam (请求体) {String} filePath 文件地址
     * @apiParam (请求体) {String} [fileDesc] 文件描述
     * @apiParam (请求体) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {int} fileID 主键ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddFile
     */
    @LogParam(moduleName = "资料文件模块", operationName = "新增资料文件", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddFile")
    @PostMapping("/AddFile")
    public Object addFile(@Valid @RequestBody AddFileParameter addFileParameter) {
        return iTbFileService.addFile(addFileParameter);
    }

    /**
     * @api {POST} /DeleteFile 删除资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName DeleteFile
     * @apiDescription 删除资料文件
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int[]} fileIDList 文件ID列表
     * @apiSuccess (返回结果) {Int} rows 删除行数
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteFile
     */
    @LogParam(moduleName = "资料文件模块", operationName = "删除资料文件", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeleteFile")
    @PostMapping("/DeleteFile")
    public Object deleteFile(@Valid @RequestBody DeleteFileParameter deleteFileParameter) {
        return iTbFileService.deleteFile(deleteFileParameter);
    }

    /**
     * @api {POST} /DescribeFile 查询资料文件
     * @apiVersion 1.0.0
     * @apiGroup 资料文件模块
     * @apiName DescribeFile
     * @apiDescription 查询资料文件
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int[]} fileID 文件ID
     * @apiSuccess (返回结果) {int} ID 主键ID
     * @apiSuccess (返回结果) {int} subjectType 对象类型 （1.工程项目  2.其他设备）
     * @apiSuccess (返回结果) {int} SubjectID  对象ID
     * @apiSuccess (返回结果) {String} fileName 文件名称
     * @apiSuccess (返回结果) {String} fileType 文件类型
     * @apiSuccess (返回结果) {int} fileSize 文件大小
     * @apiSuccess (返回结果) {String} filePath 文件地址
     * @apiSuccess (返回结果) {String} [fileDesc] 文件描述
     * @apiSuccess (返回结果) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} createUserName 创建人名称
     * @apiSuccess (返回结果) {Date} createTime 创建时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeFile
     */
    @Permission(permissionName = "mdmbase:DescribeFile")
    @PostMapping("/DescribeFile")
    public Object describeFile(@Valid @RequestBody QueryFileParameter queryFileParameter) {
        return iTbFileService.describeFile(queryFileParameter);
    }

}
