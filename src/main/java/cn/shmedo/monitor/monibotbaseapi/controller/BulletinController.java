package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-26 16:10
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BulletinController {
    /**
     * @api {POST} /QueryBulletinList 查询公告列表(不分页)
     * @apiDescription 查询公告列表(不分页)
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName QueryBulletinList
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} platform 平台key
     * @apiParam (请求参数) {Int} [size] 数量限制
     * @apiParam (请求参数) {Int} [status] 发布状态 0.未发布 1.已发布
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.bulletinID 公告ID
     * @apiSuccess (返回结果) {Int} dataList.companyID 公司ID
     * @apiSuccess (返回结果) {Int[]} dataList.platform 平台key(多选)
     * @apiSuccess (返回结果) {String} dataList.platformDesc 平台描述,多个平台间用逗号分隔
     * @apiSuccess (返回结果) {Int} dataList.type 类型 0.其他 1.行业政策 2.重要新闻 3.工作公告
     * @apiSuccess (返回结果) {String} dataList.name 公告标题
     * @apiSuccess (返回结果) {String} dataList.content 公告内容
     * @apiSuccess (返回结果) {DateTime} dataList.updateTime 修改时间,如果发布状态为1.已发布,那么就是对应的发布时间
     * @apiSuccess (返回结果) {String} dataList.updateUser 作者信息
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryBulletinList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryBulletinList(@Valid @RequestBody Object param) {
        // 按置顶、发布时间排序
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryBulletinPage 查询公告列表(分页)
     * @apiDescription 查询公告列表(分页)
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName QueryBulletinPage
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} [platform] 平台key
     * @apiParam (请求参数) {Int} [status] 发布状态 0.未发布 1.已发布. 发布状态传参为 1.已发布 时将按照发布时间倒序排序,否则将按照创建时间倒序排序
     * @apiParam (请求参数) {String} [queryCode] 模糊查询关键字
     * @apiParam (请求参数) {Int} [type] 类型 0.其他 1.行业政策 2.重要新闻 3.工作公告
     * @apiParam (请求参数) {Datetime} [startTime] 开始时间
     * @apiParam (请求参数) {Datetime} [endTime] 结束时间
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.bulletinID 公告ID
     * @apiSuccess (返回结果) {Int} currentPageData.companyID 公司ID
     * @apiSuccess (返回结果) {Int[]} currentPageData.platform 平台key(多选)
     * @apiSuccess (返回结果) {String} currentPageData.platformDesc 平台描述,多个平台间用逗号分隔
     * @apiSuccess (返回结果) {Int} currentPageData.type 类型 0.其他 1.行业政策 2.重要新闻 3.工作公告
     * @apiSuccess (返回结果) {Int} currentPageData.status 发布状态 0.未发布 1.已发布
     * @apiSuccess (返回结果) {String} currentPageData.name 公告标题
     * @apiSuccess (返回结果) {String} currentPageData.content 公告内容
     * @apiSuccess (返回结果) {DateTime} currentPageData.createTime 创建时间
     * @apiSuccess (返回结果) {DateTime} currentPageData.updateTime 修改时间,如果发布状态为1.已发布,那么就是对应的发布时间
     * @apiSuccess (返回结果) {String} currentPageData.updateUser 作者信息
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryBulletinPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryBulletinPage(@Valid @RequestBody Object param) {
        // 按置顶、发布时间排序
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryBulletinDetail 查询公告详情
     * @apiDescription 查询公告详情
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName QueryBulletinDetail
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} bulletinID 公告ID
     * @apiSuccess (返回结果) {Int} currentPageData.companyID 公司ID
     * @apiSuccess (返回结果) {Int[]} platform 平台key(多选)
     * @apiSuccess (返回结果) {String} platformDesc 平台描述,多个平台间用逗号分隔
     * @apiSuccess (返回结果) {Int} type 类型 0.其他 1.行业政策 2.重要新闻 3.工作公告
     * @apiSuccess (返回结果) {Int} status 发布状态 0.未发布 1.已发布
     * @apiSuccess (返回结果) {String} name 公告标题
     * @apiSuccess (返回结果) {String} content 公告内容
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {DateTime} updateTime 修改时间,如果发布状态为1.已发布,那么就是对应的发布时间
     * @apiSuccess (返回结果) {String} updateUser 作者信息
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryBulletinDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryBulletinDetail(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryBulletinAttachmentPage 查询公告附件(分页)
     * @apiDescription 查询公告附件(分页)
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName QueryBulletinAttachmentPage
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} bulletinID 公告ID
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {String} [fileName] 文件名(支持模糊搜索)
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.ID 附件ID
     * @apiSuccess (返回结果) {String} currentPageData.fileName 文件名
     * @apiSuccess (返回结果) {String} currentPageData.fileType 文件类型
     * @apiSuccess (返回结果) {Int} currentPageData.fileSize 文件大小
     * @apiSuccess (返回结果) {String} currentPageData.fileSizeDesc 文件大小描述,例: 3.81KB 2.81MB
     * @apiSuccess (返回结果) {String} currentPageData.createUser 创建人名称
     * @apiSuccess (返回结果) {DateTime} currentPageData.createTime 创建时间
     * @apiSuccess (返回结果) {String} currentPageData.filePath 文件绝对路径
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryBulletinAttachmentPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryBulletinAttachmentPage(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UploadBulletinAttachment 上传公告附件
     * @apiDescription 上传公告附件,附件上传完成后必须绑定公告,绑定公告接口<a href="#api-公告模块-AddBulletinAttachmentBindBatch">/AddBulletinAttachmentBindBatch</a>接口
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName UploadBulletinAttachment
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} fileName 附件文件名称
     * @apiParam (请求参数) {String} fileContent 附件文件内容(Base64字符串)
     * @apiParam (请求参数) {String} fileType 附件文件类型(文件拓展名)，例如jpg,pdf
     * @apiSuccess (返回结果) {Int} data 附件ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/UploadBulletinAttachment", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object uploadBulletinAttachment(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteBulletinAttachmentBatch 批量删除公告附件
     * @apiDescription 批量删除公告附件
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName DeleteBulletinAttachmentBatch
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} attachmentIDList 附件IDList
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/DeleteBulletinAttachmentBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteBulletinAttachmentBatch(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddBulletinAttachmentBindBatch 批量公告附件绑定公告
     * @apiDescription 批量公告附件绑定公告
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName AddBulletinAttachmentBindBatch
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} bulletinID 公告ID
     * @apiParam (请求参数) {Int[]} attachmentIDList 附件IDList
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/AddBulletinAttachmentBindBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addBulletinAttachmentBindBatch(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddBulletinData 新增公告
     * @apiDescription 新增公告
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName AddBulletinData
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} platform 平台key(多选)
     * @apiParam (请求参数) {Int} type 类型 0.其他 1.行业政策 2.重要新闻 3.工作公告
     * @apiParam (请求参数) {String} name 公告名称(标题),限制100个字符
     * @apiParam (请求参数) {String} content 公告内容
     * @apiSuccess (返回结果) {Int} data 公告ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/AddBulletinData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addBulletinData(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateBulletinData 编辑公告
     * @apiDescription 编辑公告
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName UpdateBulletinData
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} bulletinID 公告ID
     * @apiParam (请求参数) {Int[]} [platform] 平台key(多选)
     * @apiParam (请求参数) {Int} [type] 类型 0.其他 1.行业政策 2.重要新闻 3.工作公告
     * @apiParam (请求参数) {String} [name] 公告名称(标题),限制100个字符
     * @apiParam (请求参数) {String} [content] 公告内容
     * @apiParam (请求参数) {Boolean} [topMost] 公告置顶 true:置顶; false:取消置顶
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/UpdateBulletinData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateBulletinData(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /PublishBulletinDataBatch 批量发布公告
     * @apiDescription 批量发布公告
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName PublishBulletinDataBatch
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} bulletinIDList 公告IDList
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/PublishBulletinDataBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object publishBulletinDataBatch(@Valid @RequestBody Object param) {
        // 发布时,更新UpdateTime但是不更新UpdateUser,因为UpdateTime将被视为发布时间
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /RevertBulletinDataBatch 批量撤销公告
     * @apiDescription 批量撤销公告
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName RevertBulletinDataBatch
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} bulletinIDList 公告IDList
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/RevertBulletinDataBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object revertBulletinDataBatch(@Valid @RequestBody Object param) {
        // 撤销时,取消置顶
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteBulletinDataBatch 批量删除公告
     * @apiDescription 批量删除公告
     * @apiVersion 1.0.0
     * @apiGroup 公告模块
     * @apiName DeleteBulletinDataBatch
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} bulletinIDList 公告IDList
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 user:
     */
    //    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/DeleteBulletinDataBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteBulletinDataBatch(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }
}
