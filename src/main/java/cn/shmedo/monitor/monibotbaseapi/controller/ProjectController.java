package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-21 10:39
 **/
@RestController
public class ProjectController {
    private ProjectService projectService;
    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * @api {POST} /AddProject 新增工程项目
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName AddProject
     * @apiDescription 新增工程项目
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} projectName 项目名称(<=50)
     * @apiParam (请求体) {String} [shortName] 项目简称(<=20)
     * @apiParam (请求体) {Int} projectType 项目类型
     * @apiParam (请求体) {String} [imageContent] 图片内容,该项存在则imageSuffix不能为空
     * @apiParam (请求体) {String} [imageSuffix] 图片格式
     * @apiParam (请求体) {DateTime} expiryDate 有效日期，精度到天
     * @apiParam (请求体) {String} directManageUnit 直管单位(<=50)
     * @apiParam (请求体) {Int} platformType 所属平台类型
     * @apiParam (请求体) {Boolean} enable 开启状态
     * @apiParam (请求体) {String} location 四级行政区域信息(<=500)
     * @apiParam (请求体) {String} projectAddress 项目地址(<=100)
     * @apiParam (请求体) {Double} latitude 项目位置经度
     * @apiParam (请求体) {Double} longitude 项目位置纬度
     * @apiParam (请求体) {String} [desc] 项目描述(<=2000)
     * @apiParam (请求体) {Int[]} [tagIDList] 标签ID列表
     * @apiParam (请求体) {Json[]} [tagList] 标签列表
     * @apiParam (请求体) {String} tagList.key 标签键
     * @apiParam (请求体) {String} [tagList.value] 标签值
     * @apiParam (请求体) {Int[]} [monitorTypeList] 检测类型列表
     * @apiParam (请求体) {Int} modelID 模型ID
     * @apiParam (请求体) {Jons[]} modelValueList 模型值列表
     * @apiParam (请求体) {String} modelValueList.name 属性名称（<=50）
     * @apiParam (请求体) {String} [modelValueList.value] 属性值（<=50） 可为null， 不能为空字符串
     * @apiSuccess (返回结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    //@LogParam(moduleName = "设备模块", operationName = "创建单个设备", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "iot:xxx")
    @RequestMapping(value = "AddProject", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addProject(@Validated @RequestBody AddProjectParam pa) {
        projectService.addProject(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }
    /**
     * @api {post} /QueryProjectPageList 分页查询工程项目列表
     * @apiDescription 分页查询工程项目列表
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName QueryProjectPageList
     * @apiParam (请求体) {String} [projectName] 项目名称,支持模糊查询
     * @apiParam (请求体) {String} [directManageUnit] 直管单位,支持模糊查询
     * @apiParam (请求体) {String} [location] 行政区域
     * @apiParam (请求体) {Int} [companyId] 企业名称-先调用接口查询具体企业，发送id
     * @apiParam (请求体) {Int} [projectType] 项目类型
     * @apiParam (请求体) {Boolean} [enable] 项目状态，null:全选，1:启用，0:停用
     * @apiParam (请求体) {Int[]} [platformTypeList] 平台类型列表
     * @apiParam (请求体) {DateTime} [expiryDate] 有效期
     * @apiParam (请求体) {DateTime} [beginCreateTime] 创建时间-开始
     * @apiParam (请求体) {DateTime} [endCreateTime] 创建时间-结束
     * @apiParam (请求体) {Object[]} [propertyEntity] 属性查询实体
     * @apiParam (请求体) {String} propertyEntity.name 属性名称
     * @apiParam (请求体) {String} [propertyEntity.value] 属性值，仅字符串类型支持模糊查询
     * @apiParam (请求体) {Bool} [property] 是否带出属性信息，默认false
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} dataList 项目信息列表
     * @apiSuccess (返回结果) {Int} dataList.ID 项目id
     * @apiSuccess (返回结果) {String} dataList.projectName 项目名称
     * @apiSuccess (返回结果) {String} dataList.shortName 项目简称
     * @apiSuccess (返回结果) {Int} dataList.projectType 项目类型
     * @apiSuccess (返回结果) {Int} dataList.projectTypeName 项目类型名称
     * @apiSuccess (返回结果) {Int} dataList.platformType 平台类型
     * @apiSuccess (返回结果) {Int} dataList.platformTypeName 平台名称
     * @apiSuccess (返回结果) {String} dataList.directManageUnit 直管单位
     * @apiSuccess (返回结果) {DateTime} dataList.expiryDate 项目有效期
     * @apiSuccess (返回结果) {Bool} dataList.enable 是否有效
     * @apiSuccess (返回结果) {String} dataList.location 四级行政区域信息
     * @apiSuccess (返回结果) {String} dataList.projectAddress 项目地址
     * @apiSuccess (返回结果) {Double} dataList.latitude 项目经度
     * @apiSuccess (返回结果) {Double} dataList.longitude 项目纬度
     * @apiSuccess (返回结果) {String} [dataList.imagePath] 项目图片地址
     * @apiSuccess (返回结果) {String} [dataList.projectDesc] 项目简介
     * @apiSuccess (返回结果) {DateTime} dataList.createTime 创建时间
     * @apiSuccess (返回结果) {Int} dataList.createUserID 创建用户ID
     * @apiSuccess (返回结果) {DateTime} dataList.updateTime 修改时间
     * @apiSuccess (返回结果) {Int} dataList.updateUserID 修改用户ID
     * @apiSuccess (返回结果) {Object} company 公司信息
     * @apiSuccess (返回结果) {Int} company.companyID 公司id
     * @apiSuccess (返回结果) {String} company.companyName 所属公司名称
     * @apiSuccess (返回结果) {String} company.companyContacts 公司联系人
     * @apiSuccess (返回结果) {String} company.contactsPhone 公司联系人电话
     * @apiSuccess (返回结果) {String} company.contactsAddress 公司联系地址
     * @apiSuccess (返回结果) {Object[]} tagInfo 标签信息
     * @apiSuccess (返回结果) {Int} tagInfo.ID 标签id
     * @apiSuccess (返回结果) {String} tagInfo.tagKey 标签键
     * @apiSuccess (返回结果) {String} tagInfo.tagValue 标签值
     * @apiSuccess (返回结果) {Object[]} propertyList 项目基础信息列表
     * @apiSuccess (返回结果) {Int} propertyList.projectID 工程项目ID
     * @apiSuccess (返回结果) {Int} propertyList.propertyID 项目属性ID
     * @apiSuccess (返回结果) {Int} propertyList.projectType 项目类型
     * @apiSuccess (返回结果) {Int} propertyList.type 属性类型:1.数值,2.字符串,3.枚举,4.日期时间
     * @apiSuccess (返回结果) {String} propertyList.className 结构名称
     * @apiSuccess (返回结果) {String} propertyList.name 属性名称
     * @apiSuccess (返回结果) {Bool} propertyList.required 是否必填
     * @apiSuccess (返回结果) {Bool} propertyList.multiSelect 是否多选
     * @apiSuccess (返回结果) {Int} propertyList.createType 创建类型 0-预定义 1-自定义
     * @apiSuccess (返回结果) {String} [propertyList.enumField] 枚举字段
     * @apiSuccess (返回结果) {String} [propertyList.unit] 属性单位
     * @apiSuccess (返回结果) {String} [propertyList.value] 属性值
     * @apiSuccess (返回结果) {String} [propertyList.exValue] 属性拓展信息
     * @apiSuccess (返回结果) {Int} [propertyList.displayOrder] 排序字段
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryProjectPageList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object queryProjectList(@Validated @RequestBody QueryProjectListParam pa){
        return projectService.getProjectInfoList(pa);
    }


    /**
     * @api {post} /QueryProjectInfo 查询工程项目详情
     * @apiDescription 查询工程项目详情
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName QueryProjectInfo
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiSuccess (返回结果) {Int} ID 项目id
     * @apiSuccess (返回结果) {String} projectName 项目名称
     * @apiSuccess (返回结果) {String} shortName 项目简称
     * @apiSuccess (返回结果) {Int} projectType 项目类型
     * @apiSuccess (返回结果) {Int} projectTypeName 项目类型名称
     * @apiSuccess (返回结果) {Byte} platformType 平台类型
     * @apiSuccess (返回结果) {String} directManageUnit 直管单位
     * @apiSuccess (返回结果) {DateTime} expiryDate 项目有效期
     * @apiSuccess (返回结果) {Bool} enable 是否有效
     * @apiSuccess (返回结果) {String} locationInfo 四级行政区域信息
     * @apiSuccess (返回结果) {String} projectAddress 项目地址
     * @apiSuccess (返回结果) {Double} latitude 项目经度
     * @apiSuccess (返回结果) {Double} longitude 项目纬度
     * @apiSuccess (返回结果) {String} [imagePath] 项目图片地址
     * @apiSuccess (返回结果) {String} [projectDesc] 项目简介
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {Int} createUserID 创建用户ID
     * @apiSuccess (返回结果) {DateTime} updateTime 修改时间
     * @apiSuccess (返回结果) {Int} updateUserID 修改用户ID
     * @apiSuccess (返回结果) {Object} company 公司信息
     * @apiSuccess (返回结果) {Int} company.companyID 公司id
     * @apiSuccess (返回结果) {String} company.companyName 所属公司名称
     * @apiSuccess (返回结果) {String} company.companyContacts 公司联系人
     * @apiSuccess (返回结果) {String} company.contactsPhone 公司联系人电话
     * @apiSuccess (返回结果) {String} company.contactsAddress 公司联系地址
     * @apiSuccess (返回结果) {Object[]} tagInfo 标签信息
     * @apiSuccess (返回结果) {Int} tagInfo.tagID 标签id
     * @apiSuccess (返回结果) {String} tagInfo.tagKey 标签键
     * @apiSuccess (返回结果) {String} tagInfo.tagValue 标签值
     * @apiSuccess (返回结果) {Object[]} propertyList 项目基础信息列表
     * @apiSuccess (返回结果) {Int} propertyList.projectID 工程项目ID
     * @apiSuccess (返回结果) {Int} propertyList.propertyID 项目属性ID
     * @apiSuccess (返回结果) {Int} propertyList.projectType 项目类型
     * @apiSuccess (返回结果) {Int} propertyList.type 属性类型:1.数值,2.字符串,3.枚举,4.日期时间
     * @apiSuccess (返回结果) {String} propertyList.className 结构名称
     * @apiSuccess (返回结果) {String} propertyList.name 属性名称
     * @apiSuccess (返回结果) {Bool} propertyList.required 是否必填
     * @apiSuccess (返回结果) {Bool} propertyList.multiSelect 是否多选
     * @apiSuccess (返回结果) {Int} propertyList.createType 创建类型 0-预定义 1-自定义
     * @apiSuccess (返回结果) {String} [propertyList.enumField] 枚举字段
     * @apiSuccess (返回结果) {String} [propertyList.unit] 属性单位
     * @apiSuccess (返回结果) {String} [propertyList.value] 属性值
     * @apiSuccess (返回结果) {String} [propertyList.exValue] 属性拓展信息
     * @apiSuccess (返回结果) {Int} [propertyList.displayOrder] 排序字段
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryProjectInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object queryProjectInfo(@Validated @RequestBody QueryProjectInfoParam pa){
        return projectService.getProjectInfoData(pa);
    }

    /**
     * @api {post} /UpdateProject 修改工程项目
     * @apiDescription 修改工程项目
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName UpdateProject
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} projectName 项目名称
     * @apiParam (请求体) {String} shortName 项目简称
     * @apiParam (请求体) {String} [directManageUnit] 直管单位
     * @apiParam (请求体) {Int} status 项目状态,1启用，0停用
     * @apiParam (请求体) {String} projectAddress 项目地址
     * @apiParam (请求体) {String} [imageContent] 图片内容,该项存在则imageSuffix不能为空
     * @apiParam (请求体) {String} [imageSuffix] 图片格式
     * @apiParam (请求体) {Int[]} [tagIDList] 标签ID列表
     * @apiParam (请求体) {Object[]} [tagDataList] 标签信息列表
     * @apiParam (请求体) {String} tagKey 标签键
     * @apiParam (请求体) {String} tagValue 标签值
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/UpdateProject", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission
    public Object updateProjectData(Object a){
        return null;
    }

    /**
     * @api {POST} /TransferProject 转移项目
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName TransferProject
     * @apiDescription 项目转移到其他企业
     * @apiParam (请求体) {Int} companyID 目标公司ID
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiSuccess (返回结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission 项目权限:
     */
    @RequestMapping(value = "TransferProject", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object transferProject(@Validated @RequestBody TransferProjectParam param) {
        projectService.transferProject(param, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /RaiseExpiryDate 推迟有效期
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName RaiseExpiryDate
     * @apiDescription 推迟有效期
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Date} newRetireDate 新有效期
     * @apiSuccess (返回结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission 项目权限:
     */
    @RequestMapping(value = "RaiseExpiryDate", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object raiseExpiryDate(@Validated @RequestBody RaiseExpiryDateParam param) {
        projectService.raiseExpiryDate(param, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryProjectType 查询项目类型
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName QueryProjectType
     * @apiDescription 查询项目类型
     * @apiSuccess (返回结果) {Object[]} projectType 项目类型
     * @apiSuccess (返回结果) {int} projectType.ID ID
     * @apiSuccess (返回结果) {String} projectType.typeName 类型名称
     * @apiSuccess (返回结果) {String} projectType.mainType 主类型名称
     * @apiSampleRequest off
     * @apiPermission 项目权限:
     */
    @RequestMapping(value = "QueryProjectType", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    @Permission
    public Object queryProjectType(@Validated @RequestBody AddProjectParam param){
        return projectService.getProjectType();
    }
}
