package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.PermissionScope;
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
     * @apiParam (请求体) {String} [shortName] 项目简称(<=10)
     * @apiParam (请求体) {Int} projectType 项目类型
     * @apiParam (请求体) {String} [imageContent] 图片内容,该项存在则imageSuffix不能为空
     * @apiParam (请求体) {String} [imageSuffix] 图片格式
     * @apiParam (请求体) {DateTime} expiryDate 有效日期，精度到天,需大于今日
     * @apiParam (请求体) {String} directManageUnit 直管单位(<=50)
     * @apiParam (请求体) {Int} platformType 所属平台类型  1水文水利 2矿山 3国土地灾 4基建 5MD_Net3.0
     * @apiParam (请求体) {Boolean} enable 开启状态
     * @apiParam (请求体) {String} location 四级行政区域信息(<=500)
     * @apiParam (请求体) {String} projectAddress 项目地址(<=100)
     * @apiParam (请求体) {Double} latitude 项目位置经度
     * @apiParam (请求体) {Double} longitude 项目位置纬度
     * @apiParam (请求体) {String} [desc] 项目描述(<=2000)
     * @apiParam (请求体) {Int[]} [tagIDList] 标签ID列表
     * @apiParam (请求体) {Object[]} [tagList] 标签列表
     * @apiParam (请求体) {String} tagList.key 标签键
     * @apiParam (请求体) {String} [tagList.value] 标签值
     * @apiParam (请求体) {Int[]} [monitorTypeList] 检测类型列表
     * @apiParam (请求体) {Int} [modelID] 自定义模型ID
     * @apiParam (请求体) {Object[]} [modelValueList] 模型值列表(预定义与自定义部分的合集)
     * @apiParam (请求体) {String} modelValueList.ID 属性ID
     * @apiParam (请求体) {String} [modelValueList.value] 属性值（<=50） 可为null， 不能为空字符串
     * @apiSuccess (返回结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    //@LogParam(moduleName = "设备模块", operationName = "创建单个设备", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddBaseProject")
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
     * @apiParam (请求体) {Int} companyID 企业名称-先调用接口查询具体企业，发送id
     * @apiParam (请求体) {Int} [projectType] 项目类型
     * @apiParam (请求体) {Boolean} [enable] 项目状态，null:全选，true:启用，false:停用
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
     * @apiSuccess (返回结果) {Int} dataList.id 项目id
     * @apiSuccess (返回结果) {String} dataList.projectName 项目名称
     * @apiSuccess (返回结果) {String} dataList.shortName 项目简称
     * @apiSuccess (返回结果) {Int} dataList.projectType 项目类型
     * @apiSuccess (返回结果) {String} dataList.projectTypeName 项目类型名称
     * @apiSuccess (返回结果) {String} dataList.projectMainTypeName 项目主类型名称
     * @apiSuccess (返回结果) {Int} dataList.platformType 平台类型
     * @apiSuccess (返回结果) {Int} dataList.platformTypeName 平台名称
     * @apiSuccess (返回结果) {String} dataList.directManageUnit 直管单位
     * @apiSuccess (返回结果) {DateTime} dataList.expiryDate 项目有效期
     * @apiSuccess (返回结果) {Bool} dataList.enable 是否有效
     * @apiSuccess (返回结果) {String} dataList.location 四级行政区域信息
     * @apiSuccess (返回结果) {String} dataList.locationInfo  第四级区域名称
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
     * @apiSuccess (返回结果) {Int} company.ID ID
     * @apiSuccess (返回结果) {String} company.ShortName 公司简称
     * @apiSuccess (返回结果) {String} company.FullName 所属公司全称
     * @apiSuccess (返回结果) {Int} company.ParentID 所属公司id
     * @apiSuccess (返回结果) {String} company.Desc 简介
     * @apiSuccess (返回结果) {String} company.Address 公司联系地址
     * @apiSuccess (返回结果) {String} company.Phone 公司联系人电话
     * @apiSuccess (返回结果) {String} company.LegalPerson 公司法人
     * @apiSuccess (返回结果) {String} company.Scale 公司规模
     * @apiSuccess (返回结果) {String} company.Industry 行业
     * @apiSuccess (返回结果) {String} company.Nature 性质
     * @apiSuccess (返回结果) {String} company.WebSite 网站
     * @apiSuccess (返回结果) {String} company.displayOrder 排序字段
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
//    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "QueryProjectPageList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryProjectList(@Validated @RequestBody QueryProjectListRequest pa) {
        return projectService.queryProjectList(pa);
    }


    /**
     * @api {post} /QueryProjectInfo 查询工程项目详情
     * @apiDescription 查询工程项目详情
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName QueryProjectInfo
     * @apiParam (请求体) {Int} ID 项目ID
     * @apiSuccess (返回结果) {Int} ID 项目ID
     * @apiSuccess (返回结果) {String} projectName 项目名称
     * @apiSuccess (返回结果) {String} shortName 项目简称
     * @apiSuccess (返回结果) {Int} projectType 项目类型
     * @apiSuccess (返回结果) {String} projectTypeName 项目类型名称
     * @apiSuccess (返回结果) {String} projectMainTypeName 项目主类型名称
     * @apiSuccess (返回结果) {Byte} platformType 平台类型
     * @apiSuccess (返回结果) {String} directManageUnit 直管单位
     * @apiSuccess (返回结果) {DateTime} expiryDate 项目有效期
     * @apiSuccess (返回结果) {Bool} enable 是否有效
     * @apiSuccess (返回结果) {String} dataList.location 四级行政区域信息
     * @apiSuccess (返回结果) {String} dataList.locationInfo  第四级区域名称
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
     * @apiSuccess (返回结果) {Int} company.ID ID
     * @apiSuccess (返回结果) {String} company.ShortName 公司简称
     * @apiSuccess (返回结果) {String} company.FullName 所属公司全称
     * @apiSuccess (返回结果) {Int} company.ParentID 所属公司id
     * @apiSuccess (返回结果) {String} company.Desc 简介
     * @apiSuccess (返回结果) {String} company.Address 公司联系地址
     * @apiSuccess (返回结果) {String} company.Phone 公司联系人电话
     * @apiSuccess (返回结果) {String} company.LegalPerson 公司法人
     * @apiSuccess (返回结果) {String} company.Scale 公司规模
     * @apiSuccess (返回结果) {String} company.Industry 行业
     * @apiSuccess (返回结果) {String} company.Nature 性质
     * @apiSuccess (返回结果) {String} company.WebSite 网站
     * @apiSuccess (返回结果) {String} company.displayOrder 排序字段
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
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryProjectInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryProjectInfo(@Validated @RequestBody QueryProjectInfoParam pa) {
        return projectService.queryProjectInfo(pa);
    }

    /**
     * @api {post} /UpdateProject 修改工程项目
     * @apiDescription 修改工程项目
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName UpdateProject
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} projectName 项目名称(<=50)
     * @apiParam (请求体) {String} shortName 项目简称(<=10)
     * @apiParam (请求体) {String} directManageUnit 直管单位(<=50)
     * @apiParam (请求体) {Bool} enable 项目状态,true:启用，false:停用
     * @apiParam (请求体) {String} location 四级行政区域信息(<=500)
     * @apiParam (请求体) {String} projectAddress 项目地址(<=100)
     * @apiParam (请求体) {Double} longitude 项目位置经度
     * @apiParam (请求体) {Double} latitude 项目位置纬度
     * @apiParam (请求体) {String} [desc] 项目描述(<=1000)
     * @apiParam (请求体) {Int[]} [tagIDList] 标签ID列表
     * @apiParam (请求体) {Object[]} [tagList] 标签信息列表
     * @apiParam (请求体) {String} tagList.key 标签键
     * @apiParam (请求体) {String} [tagList.value] 标签值
     * @apiParam (请求体) {Object[]} [propertyList] 属性值列表
     * @apiParam (请求体) {String} propertyList.ID 属性ID
     * @apiParam (请求体) {String} [propertyList.value] 属性值
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "UpdateProject", method = RequestMethod.POST, produces = CommonVariable.JSON)
    @Permission(permissionName = "mdmbase:UpdateBaseProject")
    public Object updateProject(@Validated @RequestBody UpdateProjectParameter pa) {
        projectService.updateProject(pa);
        return ResultWrapper.successWithNothing();
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
     * @apiPermission 系统权限:
     */
    @Permission(permissionName = "mdmbase:UpdateBaseProjectCompany")
    @RequestMapping(value = "TransferProject", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object transferProject(@Validated @RequestBody TransferProjectParam param) {
        projectService.transferProject(param, CurrentSubjectHolder.getCurrentSubject());
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
    @Permission(permissionName = "mdmbase:UpdateBaseProject")
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
     * @apiPermission 登录权限
     */
    @Permission(permissionScope = PermissionScope.LOGGED)
    @RequestMapping(value = "QueryProjectType", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryProjectType() {
        return projectService.getProjectType();
    }

    /**
     * @api {POST} /DeleteProjectList 批量删除项目
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} dataIDList 项目ID列表
     * @apiDescription 批量删除项目, 需要级联删除模板值，标签关系
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限:mdmbase:DeleteBaseProject
     */
    @Permission(permissionName = "mdmbase:DeleteBaseProject")
    @RequestMapping(value = "DeleteProjectList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteProjectList(@Validated @RequestBody ProjectIDListParam dataIDList) {
        projectService.deleteProjectList(dataIDList);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {post} /UpdateProjectImage 修改工程项目图片
     * @apiDescription 修改工程项目图片
     * @apiVersion 1.0.0
     * @apiGroup 工程项目管理模块
     * @apiName UpdateProjectImage
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} fileName 文件名称,不要带后缀
     * @apiParam (请求体) {String} imageContent 图片内容,该项存在则imageSuffix不能为空
     * @apiParam (请求体) {String} imageSuffix 图片格式
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限:mdmbase:UpdateBaseProject
     */
    @Permission(permissionName = "mdmbase:UpdateBaseProject")
    @RequestMapping(value = "UpdateProjectImage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateProjectImage(@Validated @RequestBody UpdateProjectImageParam pa) {
        projectService.updateProjectImage(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }
}
